package org.artorg.tools.phantomData.client.editor2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.editor.FxFactory;
import org.artorg.tools.phantomData.client.editor.select.AbstractTableViewSelector;
import org.artorg.tools.phantomData.client.editor.select.DbTableViewSelector;
import org.artorg.tools.phantomData.client.editor.select.TableViewSelector;
import org.artorg.tools.phantomData.client.editor.select.TitledPaneTableViewSelector;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.model.AbstractProperty;
import org.artorg.tools.phantomData.server.model.Identifiable;
import org.artorg.tools.phantomData.server.models.base.person.Person;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public abstract class PropertyNodeCreator<T> implements FxFactory<T> {
	private final Class<T> itemClass;
	private static final Map<Class<?>, List<Class<?>>> subItemClassesMap;
	private static final Map<Class<?>,
			Map<Class<?>, Function<Object, Collection<Object>>>> subItemGetterMap;
	private static final Map<Class<?>, Map<Class<?>, Boolean>> containsCollectionSetterMap;

	static {
		subItemClassesMap = new HashMap<>();
		subItemGetterMap = new HashMap<>();
		containsCollectionSetterMap = new HashMap<>();
	}
	

	public PropertyNodeCreator(Class<T> itemClass) {
		this.itemClass = itemClass;
	}
	
	private final List<PropertyNode<T>> nodes;
	
	{
		nodes = new ArrayList<>();
	}
	
	@Override
	public abstract Node getGraphic();

	@Override
	public Node create(T item) {
		nodes.stream().forEach(node -> node.entityToNodeAdd(item));
		return getGraphic();
	}

	@Override
	public Node edit(T item) {
		nodes.stream().forEach(node -> node.entityToNodeEdit(item));
		return getGraphic();
	}

	@Override
	public Node create() {
		nodes.stream().forEach(node -> node.setDefault());
		return getGraphic();
	}

	public PropertyNode<T> createTextField(BiConsumer<T, String> setter, Function<T, String> getter,
			Function<T,String> getterAdd, String defaultValue) {
		TextField node = new TextField();		
		PropertyNode<T> propertyNode = new PropertyNode<T>(itemClass, node) {

			@Override
			protected void nodeToEntityImpl(T item) {
				setter.accept(item, node.getText());
			}

			@Override
			protected void entityToNodeEditImpl(T item) {
				node.setText(getter.apply(item));
			}

			@Override
			protected void entityToNodeAddImpl(T item) {
				node.setText(getterAdd.apply(item));
			}

			@Override
			protected void setDefaultImpl() {
				node.setText(defaultValue);
			}
		};
		nodes.add(propertyNode);
		return propertyNode;
	}
	
	public PropertyNode<T> createCheckBox(BiConsumer<T, Boolean> setter, Function<T, Boolean> getter,
			Function<T,Boolean> getterAdd, Boolean defaultValue) {
		CheckBox node = new CheckBox();		
		PropertyNode<T> propertyNode = new PropertyNode<T>(itemClass, node) {

			@Override
			protected void nodeToEntityImpl(T item) {
				setter.accept(item, node.isSelected());
			}

			@Override
			protected void entityToNodeEditImpl(T item) {
				node.setSelected(getter.apply(item));
			}

			@Override
			protected void entityToNodeAddImpl(T item) {
				node.setSelected(getterAdd.apply(item));
			}

			@Override
			public void setDefaultImpl() {
				node.setSelected(defaultValue);
			}
		};
		nodes.add(propertyNode);
		return propertyNode;
	}
	
	public <U> PropertyNode<T> createComboBox(Class<U> subItemClass, BiConsumer<T, U> setter, Function<T, U> getter, Function<U,String> mapper) {
		return createComboBox(subItemClass, setter, getter, item -> null, mapper);
	}
	
	public <U> PropertyNode<T> createComboBox(Class<U> subItemClass, BiConsumer<T, U> setter, Function<T, U> getter,
			Function<T,U> getterAdd, Function<U,String> mapper) {
		ComboBox<U> node = new ComboBox<U>();		
		createComboBox(node, subItemClass, mapper);
		PropertyNode<T> propertyNode = new PropertyNode<T>(itemClass, node) {

			@Override
			protected void nodeToEntityImpl(T item) {
				setter.accept(item, node.getSelectionModel().getSelectedItem());
			}

			@Override
			protected void entityToNodeEditImpl(T item) {
				selectComboBoxItem(node, getter.apply(item));
			}

			@Override
			protected void entityToNodeAddImpl(T item) {
				selectComboBoxItem(node, getterAdd.apply(item));
			}

			@Override
			protected void setDefaultImpl() {
				node.getSelectionModel().clearSelection();
			}
		};
		nodes.add(propertyNode);
		return propertyNode;
	}
	
	protected <U> void createComboBox(ComboBox<U> comboBox, Class<U> itemClass,
			Function<U, String> mapper) {
		createComboBox(comboBox, itemClass, mapper, item -> {});
	}

	protected <U> void createComboBox(ComboBox<U> comboBox, Class<U> itemClass,
			Function<U, String> mapper, Consumer<U> selectedItemChangedConsumer) {
		ICrudConnector<U> connector = (ICrudConnector<U>) Connectors.getConnector(itemClass);
		FxUtil.createDbComboBox(comboBox, connector, mapper);

		ChangeListener<U> listener = (observable, oldValue, newValue) -> {
			try {
				selectedItemChangedConsumer.accept(newValue);
			} catch (Exception e) {}
		};
		comboBox.getSelectionModel().selectedItemProperty().addListener(listener);
	}

	protected <U> void selectComboBoxItem(ComboBox<U> comboBox, U item) {
		if (item == null) {
			comboBox.getSelectionModel().clearSelection();
			return;
		}
		for (int i = 0; i < comboBox.getItems().size(); i++)
			if (((Identifiable<?>) comboBox.getItems().get(i)).equalsId((Identifiable<?>) item)) {
				comboBox.getSelectionModel().select(i);
				break;
			}
	}
	
	public <U> PropertyNode<T> createSelector(Class<U> valueClass, BiConsumer<T, Collection<U>> setter, Function<T, Collection<U>> getter,
			Function<T,U> getterAdd, U defaultValue, T item) {
		TableViewSelector<U> selector = createSelector(item, valueClass);
		Node node = selector.getGraphic();
		PropertyNode<T> propertyNode = new PropertyNode<T>(itemClass, node) {

			@Override
			protected void nodeToEntityImpl(T item) {
				setter.accept(item, selector.getSelectedItems());
			}

			@Override
			protected void entityToNodeEditImpl(T item) {
//				node.setSelected(getter.apply(item));
			}

			@Override
			protected void entityToNodeAddImpl(T item) {
//				node.setSelected(getterAdd.apply(item));
			}

			@Override
			protected void setDefaultImpl() {
//				node.setSelected(defaultValue);
			}
		};
		nodes.add(propertyNode);
		return propertyNode;
	}
	
	
	@SuppressWarnings("unchecked")
	protected <U> DbTableViewSelector<T,U> createSelector(T item, Class<?> subItemClass) {
		List<U> selectableItems =
				DbTableViewSelector.getSelectableItems(getItemClass(), (Class<U>) subItemClass);
		if (selectableItems.isEmpty()) return null;

		if (DbTableViewSelector.containsCollectionSetter(getItemClass(), subItemClass)) {
			DbTableViewSelector<T, U> selector =
					new DbTableViewSelector<T, U>(getItemClass(), (Class<U>) subItemClass);
			selector.setItem(item);
			return selector;
		}
		return null;
	}
	
	
	

	public Class<T> getItemClass() {
		return itemClass;
	}

	

}
