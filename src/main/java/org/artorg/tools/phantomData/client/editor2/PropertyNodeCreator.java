package org.artorg.tools.phantomData.client.editor2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.editor.select.AbstractTableViewSelector;
import org.artorg.tools.phantomData.client.editor.select.TableViewSelector;
import org.artorg.tools.phantomData.client.editor.select.TitledPaneTableViewSelector;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.model.AbstractProperty;
import org.artorg.tools.phantomData.server.model.Identifiable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class PropertyNodeCreator<T> {
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

	public PropertyNode<T> createTextField(BiConsumer<T, String> setter, Function<T, String> getter,
			Function<T,String> getterAdd, String defaultValue) {
		TextField node = new TextField();		
		return new PropertyNode<T>(itemClass, node) {

			@Override
			protected void nodeToEntity(T item) {
				setter.accept(item, node.getText());
			}

			@Override
			protected void entityToNodeEdit(T item) {
				node.setText(getter.apply(item));
			}

			@Override
			protected void entityToNodeAdd(T item) {
				node.setText(getterAdd.apply(item));
			}

			@Override
			protected void defaultToNode() {
				node.setText(defaultValue);
			}
		};
	}
	
	public PropertyNode<T> createCheckBox(BiConsumer<T, Boolean> setter, Function<T, Boolean> getter,
			Function<T,Boolean> getterAdd, Boolean defaultValue) {
		CheckBox node = new CheckBox();		
		return new PropertyNode<T>(itemClass, node) {

			@Override
			protected void nodeToEntity(T item) {
				setter.accept(item, node.isSelected());
			}

			@Override
			protected void entityToNodeEdit(T item) {
				node.setSelected(getter.apply(item));
			}

			@Override
			protected void entityToNodeAdd(T item) {
				node.setSelected(getterAdd.apply(item));
			}

			@Override
			protected void defaultToNode() {
				node.setSelected(defaultValue);
			}
		};
	}
	
	public <U> PropertyNode<T> createComboBox(BiConsumer<T, U> setter, Function<T, U> getter,
			Function<T,U> getterAdd, U defaultValue) {
		ComboBox<U> node = new ComboBox<U>();		
		return new PropertyNode<T>(itemClass, node) {

			@Override
			protected void nodeToEntity(T item) {
				setter.accept(item, node.getSelectionModel().getSelectedItem());
			}

			@Override
			protected void entityToNodeEdit(T item) {
				selectComboBoxItem(node, getter.apply(item));
			}

			@Override
			protected void entityToNodeAdd(T item) {
				selectComboBoxItem(node, getterAdd.apply(item));
			}

			@Override
			protected void defaultToNode() {
				selectComboBoxItem(node, defaultValue);
			}
		};
	}

	protected <U> void selectComboBoxItem(ComboBox<U> comboBox, U item) {
		if (item == null) return;
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
		return new PropertyNode<T>(itemClass, node) {

			@Override
			protected void nodeToEntity(T item) {
				setter.accept(item, selector.getSelectedItems());
			}

			@Override
			protected void entityToNodeEdit(T item) {
//				node.setSelected(getter.apply(item));
			}

			@Override
			protected void entityToNodeAdd(T item) {
//				node.setSelected(getterAdd.apply(item));
			}

			@Override
			protected void defaultToNode() {
//				node.setSelected(defaultValue);
			}
		};
	}
	
	
	protected <U> TableViewSelector<U> createSelector(T item, Class<?> subItemClass) {
		Set<U> selectableItems = getSelectableItems((Class<U>) subItemClass);
		if (selectableItems.isEmpty()) return null;

		if (containsCollectionSetter(subItemClass)) {
			try {
				TableViewSelector<U> titledSelector =
						new TableViewSelector<U>((Class<U>) subItemClass);

				titledSelector.getSelectableItems().addAll(selectableItems);
				if (item != null) {
					Function<Object, Collection<Object>> subItemGetter =
							getSubItemGetter(subItemClass);
					if (subItemGetter != null) {
						Set<U> selectedItems = subItemGetter.apply(item).stream()
								.filter(e -> e != null).map(e -> (U) e).collect(Collectors.toSet());
						titledSelector.getSelectedItems().addAll(selectedItems);
					}
				}

				titledSelector.init();

//				TitledPane titledPane =
//						((TitledPane) ((TitledPaneTableViewSelector<?>) titledSelector)
//								.getGraphic());
//				titledPane.expandedProperty().addListener(new ChangeListener<Boolean>() {
//					@Override
//					public void changed(ObservableValue<? extends Boolean> observable,
//							Boolean oldValue, Boolean newValue) {
//						if (newValue) selectors.stream()
//								.map(titledSelector -> ((TitledPane) ((TitledPaneTableViewSelector<
//										?>) titledSelector).getGraphic()))
//								.filter(titledPane2 -> titledPane2 != titledPane)
//								.forEach(titledSelector -> {
//									titledSelector.setAnimated(true);
//									titledSelector.setExpanded(false);
//								});
//
//					}
//
//				});

				return titledSelector;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	protected <U> Set<U> getSelectableItems(Class<U> subItemClass) {
		ICrudConnector<U> connector = Connectors.getConnector(subItemClass);
		Set<U> items = connector.readAllAsSet();
		if (AbstractProperty.class.isAssignableFrom(subItemClass)) {
			items = items.stream().filter(item -> {
				try {
					String type = ((AbstractProperty<U, ?>) item).getPropertyField().getType();
					Class<?> classType = Class.forName(type);
					if (classType == getItemClass()) return true;
				} catch (NullPointerException | ClassNotFoundException e) {}
				return false;
			}).collect(Collectors.toSet());
		}
		return items;
	}
	
	private Function<Object, Collection<Object>> getSubItemGetter(Class<?> subItemClass) {
		Function<Object, Collection<Object>> subItemGetter = null;
		if (subItemGetterMap.containsKey(itemClass)) {
			if (subItemGetterMap.get(itemClass).containsKey(subItemClass))
				subItemGetter = subItemGetterMap.get(itemClass).get(subItemClass);
			else {
				subItemGetter = createSubItemGetter(subItemClass);
				if (subItemGetter == null) return null;
				subItemGetterMap.get(itemClass).put(subItemClass, subItemGetter);
			}
		} else {
			subItemGetter = createSubItemGetter(subItemClass);
			if (subItemGetter == null) return null;
			Map<Class<?>, Function<Object, Collection<Object>>> map = new HashMap<>();
			map.put(subItemClass, subItemGetter);
			subItemGetterMap.put(itemClass, map);
		}
		return subItemGetter;
	}
	
	private Function<Object, Collection<Object>> createSubItemGetter(Class<?> subItemClass) {
		Method selectedMethod = Reflect.getMethodByGenericReturnType(itemClass, subItemClass);
		if (selectedMethod == null) return null;
		Function<Object, Collection<Object>> subItemGetter;
		subItemGetter = i -> {
			if (i == null) return null;
			try {
				return (Collection<Object>) (selectedMethod.invoke(i));
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		};
		return subItemGetter;
	}
	
	private boolean containsCollectionSetter(Class<?> subItemClass) {
		boolean result;
		if (containsCollectionSetterMap.containsKey(itemClass)) {
			if (containsCollectionSetterMap.get(itemClass).containsKey(subItemClass))
				result = containsCollectionSetterMap.get(itemClass).get(subItemClass);
			else {
				result = Reflect.containsCollectionSetter(itemClass, subItemClass);
				containsCollectionSetterMap.get(itemClass).put(subItemClass, result);
			}
		} else {
			result = Reflect.containsCollectionSetter(itemClass, subItemClass);
			Map<Class<?>, Boolean> map = new HashMap<>();
			map.put(subItemClass, result);
			containsCollectionSetterMap.put(itemClass, map);
		}
		return result;
	}
	
	

	public Class<T> getItemClass() {
		return itemClass;
	}

}
