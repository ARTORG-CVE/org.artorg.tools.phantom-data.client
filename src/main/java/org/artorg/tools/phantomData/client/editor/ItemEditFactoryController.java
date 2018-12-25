package org.artorg.tools.phantomData.client.editor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.admin.UserAdmin;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.editor.select.AbstractTableViewSelector;
import org.artorg.tools.phantomData.client.editor.select.TitledPaneTableViewSelector;
import org.artorg.tools.phantomData.client.exceptions.NoUserLoggedInException;
import org.artorg.tools.phantomData.client.scene.control.VGridBoxPane;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.model.AbstractProperty;
import org.artorg.tools.phantomData.server.model.BackReference;
import org.artorg.tools.phantomData.server.model.DbPersistent;
import org.artorg.tools.phantomData.server.model.Identifiable;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

@SuppressWarnings("unchecked")
public abstract class ItemEditFactoryController<T> extends VGridBoxPane implements FxFactory<T> {
	private static final Map<Class<?>, List<Class<?>>> subItemClassesMap;
	private static final Map<Class<?>,
			Map<Class<?>, Function<Object, Collection<Object>>>> subItemGetterMap;
	private static final Map<Class<?>, Map<Class<?>, Boolean>> containsCollectionSetterMap;
	protected Button applyButton;
	private List<Node> rightNodes;
	private List<AbstractTableViewSelector<?>> selectors;
	private AnchorPane pane;
	private final Class<T> itemClass;
	private ICrudConnector<T> connector;
	private final List<Class<?>> subItemClasses;

	static {
		subItemClassesMap = new HashMap<>();
		subItemGetterMap = new HashMap<>();
		containsCollectionSetterMap = new HashMap<>();
	}

	{
		super.addColumn(80.0);
		super.addColumn(180.0);
		applyButton = new Button("Apply");
	}

	public ItemEditFactoryController() {
		itemClass = (Class<T>) Reflect.findGenericClasstype(this);
		connector = (ICrudConnector<T>) Connectors.getConnector(itemClass);

		if (subItemClassesMap.containsKey(itemClass))
			subItemClasses = subItemClassesMap.get(itemClass);
		else {
			subItemClasses = Reflect.getCollectionSetterMethods(itemClass)
					.filter(m -> !m.isAnnotationPresent(BackReference.class)).map(m -> {
						Type type = m.getGenericParameterTypes()[0];
						Class<?> cls = Reflect.getGenericTypeClass(type);
						return cls;
					}).filter(c -> c != null).filter(c -> DbPersistent.class.isAssignableFrom(c))
					.collect(Collectors.toList());
			subItemClassesMap.put(itemClass, subItemClasses);
		}
	}

	public abstract T createItem();

	protected abstract void setEditTemplate(T item);

	public void setAddTemplate(T item) {
//		setEditTemplate();
	}

	protected abstract void applyChanges(T item);

	protected abstract AnchorPane createRootPane();

	protected abstract void addProperties(T item);

	public abstract List<PropertyEntry> getPropertyEntries();

	protected void setSelectedChildItems(T item, AbstractTableViewSelector<T> selector) {
		selector.setSelectedChildItems(item);
	}

	private List<AbstractTableViewSelector<?>> createSelectors(T item) {
		List<AbstractTableViewSelector<?>> selectors = new ArrayList<>();
		subItemClasses.forEach(subItemClass -> {
			Set<?> selectableItems = getSelectableItems(subItemClass);
			if (!selectableItems.isEmpty()) {
				AbstractTableViewSelector<?> selector =
						createSelector(item, subItemClass, selectableItems);
				if (selector != null) selectors.add(selector);
			}
		});

		return selectors;
	}

	protected <U> AbstractTableViewSelector<U> createSelector(T item, Class<?> subItemClass,
			Set<U> selectableItems) {
		if (containsCollectionSetter(subItemClass)) {
			try {
				TitledPaneTableViewSelector<U> titledSelector =
						new TitledPaneTableViewSelector<U>((Class<U>) subItemClass);

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

				TitledPane titledPane =
						((TitledPane) ((TitledPaneTableViewSelector<?>) titledSelector)
								.getGraphic());
				titledPane.expandedProperty().addListener(new ChangeListener<Boolean>() {
					@Override
					public void changed(ObservableValue<? extends Boolean> observable,
							Boolean oldValue, Boolean newValue) {
						if (newValue) selectors.stream()
								.map(titledSelector -> ((TitledPane) ((TitledPaneTableViewSelector<
										?>) titledSelector).getGraphic()))
								.filter(titledPane2 -> titledPane2 != titledPane)
								.forEach(titledSelector -> {
									titledSelector.setAnimated(true);
									titledSelector.setExpanded(false);
								});

					}

				});

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
			items = items.stream()
					.filter(item -> {
						try {
							String type = ((AbstractProperty<U, ?>)item).getPropertyField().getType();
							Class<?> classType = Class.forName(type);
							if (classType == getItemClass())
								return true;
						} catch (NullPointerException | ClassNotFoundException e) {}
						return false;
					})
					.collect(Collectors.toSet());
		}
		return items;
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

	protected void initDefaultValues() {
		rightNodes.forEach(node -> {
			if (node instanceof ComboBox) ((ComboBox<?>) node).getSelectionModel().clearSelection();
			else if (node instanceof TextField) ((TextField) node).setText("");
			else if (node instanceof CheckBox) ((CheckBox) node).setSelected(false);
		});
	}

	protected <U> void selectComboBoxItem(ComboBox<U> comboBox, U item) {
		if (item == null) return;
		for (int i = 0; i < comboBox.getItems().size(); i++)
			if (((Identifiable<?>) comboBox.getItems().get(i)).equalsId((Identifiable<?>) item)) {
				comboBox.getSelectionModel().select(i);
				break;
			}
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

	public AnchorPane create() {
		return create(null);
	}

	public AnchorPane create(T item) {
		selectors = createSelectors(item);
		addProperties(item);
		createRightNodes(getPropertyEntries());
		initDefaultValues();
		pane = createRootPane();
		if (item != null) setAddTemplate(item);
		applyButton.setOnAction(event -> {
			FxUtil.runNewSingleThreaded(() -> createAndPersistItem());
		});
		applyButton.setText("Create");
		return pane;
	}

	public T createAndPersistItem() {
		try {
			T newItem = createItem();
			if (newItem != null) {
				if (selectors != null) selectors.stream().filter(selector -> selector != null)
						.forEach(selector -> selector.setSelectedChildItems(newItem));
				if (getConnector().create(newItem)) {
					Platform.runLater(() -> initDefaultValues());
				}
			}
			return newItem;
		} catch (Exception e) {
			handleException(e);
		}
		throw new NullPointerException();
	}

	@Override
	public AnchorPane edit(T item) {
		selectors = createSelectors(item);
		Label label = new Label();
		label.setText(((Identifiable<?>) item).getId().toString());
		label.setDisable(true);
		PropertyEntry idEntry = new PropertyEntry("Id", label);
		getPropertyEntries().add(idEntry);
		addProperties(item);
		createRightNodes(getPropertyEntries());
		initDefaultValues();
		pane = createRootPane();
		if (item != null) setEditTemplate(item);
		applyButton.setOnAction(event -> {
			if (!UserAdmin.isUserLoggedIn()) Main.getMainController().openLoginLogoutFrame();
			else {
				try {
					applyChanges(item);

					selectors.forEach(selector -> selector.setSelectedChildItems(item));

					getConnector().update(item);

				} catch (Exception e) {
					handleException(e);
				}
			}
		});

		applyButton.setText("Save");
		return pane;
	}

	private void handleException(Exception e) {
		e.printStackTrace();
		if (e instanceof NoUserLoggedInException) System.err.println("log in !!");
	}

	private void createRightNodes(List<PropertyEntry> entries) {
		rightNodes = entries.stream().map(e -> e.getRightNode()).collect(Collectors.toList());
	}

	public Class<T> getItemClass() {
		return itemClass;
	}

	public ICrudConnector<T> getConnector() {
		return this.connector;
	}

	public Node getGraphic() {
		return pane;
	}

	public List<AbstractTableViewSelector<?>> getSelectors() {
		return selectors;
	}

}
