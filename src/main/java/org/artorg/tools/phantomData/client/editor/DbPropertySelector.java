package org.artorg.tools.phantomData.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.AbstractFilterColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.modelUI.PropertyUI;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.AbstractPropertifiedEntity;
import org.artorg.tools.phantomData.server.model.AbstractProperty;
import org.artorg.tools.phantomData.server.models.base.property.BooleanProperty;
import org.artorg.tools.phantomData.server.models.base.property.PropertyField;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

@SuppressWarnings("rawtypes")
public class DbPropertySelector<T> extends VBox {
	private final Class<? extends AbstractPropertifiedEntity<?>> parentItemClass;
	private final ProTableView<AbstractProperty> tableView;
	private final ItemEditor<AbstractProperty> propertyEditor;
	private final ComboBox<Class<? extends AbstractProperty>> comboBoxPropertyType;
	private PropertyUI propertyUI;

	{
		comboBoxPropertyType = new ComboBox<>();
		Collection<Class<? extends AbstractProperty>> propertyClasses = Main.getPropertyclasses();
		comboBoxPropertyType.setItems(FXCollections.observableArrayList(propertyClasses));
		FxUtil.setComboBoxCellFactory(comboBoxPropertyType, item -> item.getSimpleName());
	}

	@SuppressWarnings("unchecked")
	public DbPropertySelector(Class<T> parentItemClass) {
		this.parentItemClass = (Class<? extends AbstractPropertifiedEntity<?>>) parentItemClass;
		propertyUI = Main.getPropertyUIEntity(BooleanProperty.class);

		propertyEditor = createEditFactory();
		Class<? extends AbstractProperty<?, ?>> propertyClass = BooleanProperty.class;
		propertyEditor.showCreateMode(createInstance(propertyClass));
		this.getChildren().add(propertyEditor);
		comboBoxPropertyType.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> {
					Platform.runLater(() -> {
						propertyUI = Main.getPropertyUIEntity(newValue);
						Class<? extends AbstractProperty<?, ?>> propertyClass2 =
								(Class<? extends AbstractProperty<?, ?>>) newValue;
						propertyEditor.showCreateMode(createInstance(propertyClass2));
					});
				});

		tableView = createTableView();
		this.getChildren().add(tableView);
	}

	@SuppressWarnings("unchecked")
	private ItemEditor<AbstractProperty> createEditFactory() {
		final AnchorPane valuePane = new AnchorPane();

		ItemEditor<AbstractProperty> editor =
				new ItemEditor<AbstractProperty>(AbstractProperty.class) {
					IPropertyNode valuePropertyNode = null;

					@Override
					public void onCreatedServer(AbstractProperty item) {
						getPropertyItems().add(item);
					}

					@Override
					public void onShowingCreateMode(AbstractProperty item) {
						Class<? extends AbstractProperty> propertyClass;
						if (item == null) propertyClass =
								comboBoxPropertyType.getSelectionModel().getSelectedItem();
						else
							propertyClass = item.getClass();
						propertyUI = Main.getPropertyUIEntity(propertyClass);
						comboBoxPropertyType.setDisable(false);
						updateValueNode();
					}

					@Override
					public void onShowingEditMode(AbstractProperty item) {
						propertyUI = Main.getPropertyUIEntity(item.getClass());
						Class<? extends AbstractProperty> propertyClass = item.getClass();
						comboBoxPropertyType.getSelectionModel().select(propertyClass);
						comboBoxPropertyType.setDisable(true);
						updateValueNode();
					}

					private void updateValueNode() {
						if (valuePropertyNode != null)
							getChildrenProperties().remove(valuePropertyNode);
						Node valueNode = propertyUI.createValueNode();
						valuePropertyNode = createNode(item -> item.getValue(),
								(item, value) -> item.setValue(value),
								item -> propertyUI.getDefaultValue(), valueNode,
								value -> propertyUI.setValueToNode(valueNode, value),
								() -> propertyUI.getValueFromNode(valueNode), () -> propertyUI
										.setValueToNode(valueNode, propertyUI.getDefaultValue()));
						FxUtil.addToPane(valuePane, valueNode);
						getChildrenProperties().add(valuePropertyNode);
					}

				};
		PropertyGridPane propertyPane = new PropertyGridPane();

		ComboBox<PropertyField> comboBoxPropertyField = new ComboBox<>();
		ICrudConnector<PropertyField> connector = Connectors.get(PropertyField.class);
		ObservableList<PropertyField> items = connector.readAllAsList().stream()
				.filter(propertyField -> propertyField.getType().equals(parentItemClass.getName()))
				.collect(Collectors.toCollection(() -> FXCollections.observableArrayList()));
		comboBoxPropertyField.setItems(items);
		FxUtil.setComboBoxCellFactory(comboBoxPropertyField, item -> item.getName());

		propertyPane.addEntry(new Label("Type"), comboBoxPropertyType);
		propertyPane.addEntry("Property Field", editor.createComboBox(comboBoxPropertyField,
				item -> item.getPropertyField(), (item, value) -> item.setPropertyField(value)));

		propertyPane.addEntry(new Label("Value"), valuePane);
		editor.add(propertyPane);

		Button applyButton = editor.getApplyButton();
		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(event -> {
			Class<AbstractProperty<?, ?>> propertyClass =
					(Class<AbstractProperty<?, ?>>) comboBoxPropertyType.getSelectionModel()
							.getSelectedItem();
			propertyEditor.showCreateMode(createInstance(propertyClass));
		});
		applyButton.setMaxWidth(Double.MAX_VALUE);
		VBox.setVgrow(applyButton, Priority.NEVER);
		cancelButton.setMaxWidth(Double.MAX_VALUE);
		VBox.setVgrow(cancelButton, Priority.NEVER);
		AnchorPane buttonPane = new AnchorPane();
		buttonPane.setPadding(new Insets(5, 10, 5, 10));
		HBox hBox = new HBox();
		hBox.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(applyButton, Priority.ALWAYS);
		HBox.setHgrow(cancelButton, Priority.ALWAYS);
		hBox.getChildren().add(applyButton);
		hBox.getChildren().add(cancelButton);
		buttonPane.getChildren().add(hBox);
		FxUtil.setAnchorZero(hBox);
		editor.getvBox().getChildren().add(buttonPane);

		return editor;
	}

	private ProTableView<AbstractProperty> createTableView() {
		ProTableView<AbstractProperty> tableView = new UIEntity<AbstractProperty>() {
			@Override
			public Class<AbstractProperty> getItemClass() {
				return AbstractProperty.class;
			}

			@Override
			public String getTableName() {
				return "Properties";
			}

			@SuppressWarnings("unchecked")
			@Override
			public List<AbstractColumn<AbstractProperty, ? extends Object>>
					createColumns(Table<AbstractProperty> table, List<AbstractProperty> items) {
				List<AbstractColumn<AbstractProperty, ?>> columns = new ArrayList<>();
				ColumnCreator<AbstractProperty, AbstractProperty> creator =
						new ColumnCreator<>(table);
				columns.add(creator.createFilterColumn("Name",
						path -> path.getPropertyField().getName()));
				AbstractFilterColumn<AbstractProperty, ?> column = creator.createFilterColumn(
						"Value", path -> String.valueOf(path.getValue()),
						(path, value) -> path.setValue(
								Main.getPropertyUIEntity(path.getClass()).fromString(value)));
				column.setItemsFilter(false);
				columns.add(column);
				createPersonifiedColumns(table, columns);
				return columns;
			}

			@Override
			public ItemEditor<AbstractProperty> createEditFactory() {
				throw new UnsupportedOperationException();
			}

		}.createProTableView();
		tableView.setRowFactory(
				new Callback<TableView<AbstractProperty>, TableRow<AbstractProperty>>() {
					@Override
					public TableRow<AbstractProperty> call(TableView<AbstractProperty> tableView) {
						final TableRow<AbstractProperty> row = new TableRow<>();

						ContextMenu contextMenu = new ContextMenu();
						MenuItem menuItem;
						menuItem = new MenuItem("Edit");
						menuItem.setOnAction(event -> propertyEditor.showEditMode(row.getItem()));
						contextMenu.getItems().add(menuItem);

						menuItem = new MenuItem("Remove");
						menuItem.setOnAction(event -> getPropertyItems().remove(row.getItem()));
						contextMenu.getItems().add(menuItem);

						menuItem = new MenuItem("Refresh");
						menuItem.setOnAction(event -> {});
						contextMenu.getItems().add(menuItem);

						row.contextMenuProperty()
								.bind(Bindings.when(Bindings.isNotNull(row.itemProperty()))
										.then(contextMenu).otherwise((ContextMenu) null));
						return row;
					};
				});
		return tableView;
	}

	private AbstractProperty<?, ?> createInstance(Class<? extends AbstractProperty<?, ?>> cls) {
		try {
			return cls.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException();
	}

	public List<AbstractProperty> getPropertyItems() {
		return tableView.getTable().getItems();
	}

	public void setPropertyItems(List<AbstractProperty> items) {
		tableView.getTable().getItems().clear();
		tableView.getTable().getItems().addAll(items);
	}

	@SuppressWarnings("unchecked")
	public Class<T> getParentItemClass() {
		return (Class<T>) parentItemClass;
	}

}
