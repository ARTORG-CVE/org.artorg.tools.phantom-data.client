package org.artorg.tools.phantomData.client.editor;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.admin.UserAdmin;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.editor.select.DbTableViewSelector;
import org.artorg.tools.phantomData.client.editor.select.TableViewSelector;
import org.artorg.tools.phantomData.client.exceptions.NoUserLoggedInException;
import org.artorg.tools.phantomData.client.scene.control.VGridBoxPane;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.model.BackReference;
import org.artorg.tools.phantomData.server.model.DbPersistent;
import org.artorg.tools.phantomData.server.model.Identifiable;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

@SuppressWarnings("unchecked")
public abstract class ItemEditFactoryController<T>  implements FxFactory<T> {
	private static final Map<Class<?>, List<Class<?>>> subItemClassesMap;
	protected Button applyButton;
	private List<TableViewSelector<?>> selectors;
	private final Class<T> itemClass;
	private ICrudConnector<T> connector;
	private final List<Class<?>> subItemClasses;

	static {
		subItemClassesMap = new HashMap<>();
	}

	{
//		super.addColumn(80.0);
//		super.addColumn(180.0);
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
		setDefaultTemplate();
	}

	public abstract void setDefaultTemplate();

	protected abstract void applyChanges(T item);

	protected abstract AnchorPane createRootPane();

	protected abstract void addProperties(T item);

//	public abstract List<PropertyEntry> getPropertyEntries();

	
	public AnchorPane createButtonPane(Button button) {
		button.setPrefHeight(25.0);
		button.setMaxWidth(Double.MAX_VALUE);
		AnchorPane buttonPane = new AnchorPane();
		buttonPane.setPrefHeight(button.getPrefHeight() + 20);
		buttonPane.setMaxHeight(buttonPane.getPrefHeight());
		buttonPane.setPadding(new Insets(5, 10, 5, 10));
		buttonPane.getChildren().add(button);
		FxUtil.setAnchorZero(button);
		return buttonPane;
	}
	
	protected void setSelectedChildItems(T item, TableViewSelector<T> selector) {
		selector.setSelectedChildItems(item);
	}

	private List<TableViewSelector<?>> createSelectors(T item) {
		List<TableViewSelector<?>> selectors = new ArrayList<>();
		subItemClasses.forEach(subItemClass -> {
			TableViewSelector<?> selector = createSelector(item, subItemClass);
			if (selector != null) selectors.add(selector);
		});

		return selectors;
	}

	protected <U> TableViewSelector<U> createSelector(T item, Class<?> subItemClass) {
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

//	protected void initDefaultValues() {
//		rightNodes.forEach(node -> {
//			if (node instanceof ComboBox) ((ComboBox<?>) node).getSelectionModel().clearSelection();
//			else if (node instanceof TextField) ((TextField) node).setText("");
//			else if (node instanceof CheckBox) ((CheckBox) node).setSelected(false);
//		});
//	}

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
//		createRightNodes(getPropertyEntries());
//		initDefaultValues();
		if (item == null) setDefaultTemplate();
		else
			setAddTemplate(item);

		AnchorPane pane = createRootPane();
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
					Platform.runLater(() -> setAddTemplate(newItem));
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
//		Label label = new Label();
//		label.setText(((Identifiable<?>) item).getId().toString());
//		label.setDisable(true);
//		PropertyEntry idEntry = new PropertyEntry("Id", label);
//		getPropertyEntries().add(idEntry);
		addProperties(item);
//		createRightNodes(getPropertyEntries());
//		initDefaultValues();
		AnchorPane pane = createRootPane();
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

//	private void createRightNodes(List<PropertyEntry> entries) {
//		rightNodes = entries.stream().map(e -> e.getRightNode()).collect(Collectors.toList());
//	}

	public Class<T> getItemClass() {
		return itemClass;
	}

	public ICrudConnector<T> getConnector() {
		return this.connector;
	}

	public Node getGraphic() {
		throw new UnsupportedOperationException();
	}

	public List<TableViewSelector<?>> getSelectors() {
		return selectors;
	}

}
