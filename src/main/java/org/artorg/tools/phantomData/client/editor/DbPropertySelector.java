package org.artorg.tools.phantomData.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.AbstractFilterColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.logging.Logger;
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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
	private final PropertySelectorItemEditor propertyEditor;
//	private final ComboBox<Class<? extends AbstractProperty>> comboBoxPropertyType;
	private PropertyUI propertyUI;
//	private Class<? extends AbstractProperty<?, ?>> propertyClass;

	{
//		comboBoxPropertyType = new ComboBox<>();
//		Collection<Class<? extends AbstractProperty>> propertyClasses = Main.getPropertyclasses();
//		comboBoxPropertyType.setItems(FXCollections.observableArrayList(propertyClasses));
//		FxUtil.setComboBoxCellFactory(comboBoxPropertyType, item -> item.getSimpleName());
//		propertyEditor = (DbPropertySelector<T>.PropertySelectorItemEditor) createEditFactory();
	}

	@SuppressWarnings("unchecked")
	public DbPropertySelector(Class<T> parentItemClass) {
		this.parentItemClass = (Class<? extends AbstractPropertifiedEntity<?>>) parentItemClass;
		propertyUI = Main.getPropertyUIEntity(BooleanProperty.class);

		propertyEditor = (DbPropertySelector<T>.PropertySelectorItemEditor) createEditFactory();
//		propertyClass = BooleanProperty.class;
		propertyEditor.setBeanClass(BooleanProperty.class);
		propertyEditor.showCreateMode();
		this.getChildren().add(propertyEditor);
//		comboBoxPropertyType.getSelectionModel().selectedItemProperty()
//				.addListener((observable, oldValue, newValue) -> {
//					Platform.runLater(() -> {
//						propertyUI = Main.getPropertyUIEntity(newValue);
//						propertyClass = (Class<? extends AbstractProperty<?, ?>>) newValue;
//						propertyEditor.showCreateMode();
//					});
//				});

		tableView = createTableView();
		this.getChildren().add(tableView);
	}

	private abstract class PropertySelectorItemEditor extends ItemEditor<AbstractProperty> {

		public PropertySelectorItemEditor() {
			super(AbstractProperty.class);
			// TODO Auto-generated constructor stub
		}

		public abstract void updateValueNode();

	}

	@SuppressWarnings("unchecked")
	private ItemEditor<AbstractProperty> createEditFactory() {
		final AnchorPane valuePane = new AnchorPane();
		ComboBox<PropertyField> comboBoxPropertyField = new ComboBox<>();

		PropertySelectorItemEditor editor = new PropertySelectorItemEditor() {
			IPropertyNode valuePropertyNode = null;

			@Override
			public void onCreatedServer(AbstractProperty item) {
				getPropertyItems().add(item);
			}

			@Override
			public void onShowingCreateMode(Class<? extends AbstractProperty> beanClass) {
//				propertyClass = (Class<? extends AbstractProperty<?, ?>>) beanClass;
				propertyUI = Main.getPropertyUIEntity(beanClass);
//						comboBoxPropertyType.setDisable(false);
				updateValueNode();
			}

			@Override
			public void onShowingEditMode(AbstractProperty item) {
				propertyUI = Main.getPropertyUIEntity(item.getClass());
				Class<? extends AbstractProperty> propertyClass = item.getClass();
				PropertyField propertyField = comboBoxPropertyField.getItems().stream()
						.filter(p -> p.getPropertyType().equals(propertyClass.getSimpleName()))
						.findFirst().get();
				comboBoxPropertyField.getSelectionModel().select(propertyField);

//						comboBoxPropertyType.getSelectionModel().select(propertyClass);
//						comboBoxPropertyType.setDisable(true);
				updateValueNode();
			}

			public void updateValueNode() {
				if (valuePropertyNode != null) getChildrenProperties().remove(valuePropertyNode);
				Node valueNode = propertyUI.createValueNode();
				valuePropertyNode = createNode(item -> item.getValue(),
						(item, value) -> item.setValue(value), item -> propertyUI.getDefaultValue(),
						valueNode, value -> propertyUI.setValueToNode(valueNode, value),
						() -> propertyUI.getValueFromNode(valueNode),
						() -> propertyUI.setValueToNode(valueNode, propertyUI.getDefaultValue()));
				valuePane.getChildren().clear();
				FxUtil.addToPane(valuePane, valueNode);
				getChildrenProperties().add(valuePropertyNode);
			}

		};
		PropertyGridPane propertyPane = new PropertyGridPane();

		ICrudConnector<PropertyField> connector = Connectors.get(PropertyField.class);
		ObservableList<PropertyField> items = connector.readAllAsList().stream()
				.filter(propertyField -> propertyField.getEntityType()
						.equals(parentItemClass.getSimpleName()))
				.collect(Collectors.toCollection(() -> FXCollections.observableArrayList()));
		comboBoxPropertyField.setItems(items);
		Pattern pattern = Pattern.compile("(?i)(.*)property");
		FxUtil.setComboBoxCellFactory(comboBoxPropertyField, item -> {
			Matcher matcher = pattern.matcher(item.getPropertyType());
			String type = "";
			if (matcher.find()) type = matcher.group(1);
			return String.format("%s (%s)", item.getName(), type);
		});
		comboBoxPropertyField.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<PropertyField>() {
//
					@Override
					public void changed(ObservableValue<? extends PropertyField> observable,
							PropertyField oldValue, PropertyField newValue) {
						PropertyField propertyField = newValue;
						if (propertyField != null) {
							Platform.runLater(() -> {
								String propertyType = propertyField.getPropertyType();
								Class<? extends AbstractProperty<?, ?>> propertyClass = Main
										.getPropertyclasses().stream()
										.filter(cls -> cls.getSimpleName().equals(propertyType))
										.map(cls -> (Class<? extends AbstractProperty<?, ?>>) cls)
										.findFirst().get();
								propertyEditor.setBeanClass(propertyClass);
								propertyUI = Main.getPropertyUIEntity(
										(Class<AbstractProperty>) propertyClass);
//								propertyEditor.showCreateMode();
								propertyEditor.updateValueNode();
							});
						}
					}
//					
				});

//		propertyPane.addEntry(new Label("Type"), comboBoxPropertyType);
		propertyPane.addEntry("Property Field", editor.create(comboBoxPropertyField,
				item -> item.getPropertyField(), (item, value) -> item.setPropertyField(value)));

		propertyPane.addEntry(new Label("Value"), valuePane);
		editor.add(propertyPane);

		Button applyButton = editor.getApplyButton();
		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(event -> {
//			PropertyField propertyField =
//					comboBoxPropertyField.getSelectionModel().getSelectedItem();
//			if (propertyField != null) {
//				String propertyType = propertyField.getPropertyType();
//				propertyClass = Main.getPropertyclasses().stream()
//						.filter(cls -> cls.getSimpleName().equals(propertyType))
//						.map(cls -> (Class<? extends AbstractProperty<?, ?>>) cls).findFirst()
//						.get();

//			propertyClass = (Class<AbstractProperty<?, ?>>) comboBoxPropertyType.getSelectionModel()
//					.getSelectedItem();
			propertyEditor.showCreateMode();
//			}
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
					@SuppressWarnings("unchecked")
					@Override
					public TableRow<AbstractProperty> call(TableView<AbstractProperty> tableView) {
						final TableRow<AbstractProperty> row = new TableRow<>();

						ContextMenu contextMenu = new ContextMenu();
						MenuItem menuItem;
						menuItem = new MenuItem("Edit");
						menuItem.setOnAction(event -> {
							propertyUI = Main.getPropertyUIEntity(row.getItem().getClass());
							propertyEditor.updateValueNode();
							propertyEditor.showEditMode(row.getItem());
							
						});
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

//	private AbstractProperty<?, ?> createInstance(Class<? extends AbstractProperty<?, ?>> cls) {
//		try {
//			return cls.newInstance();
//		} catch (InstantiationException | IllegalAccessException e) {
//			e.printStackTrace();
//		}
//		throw new IllegalArgumentException();
//	}

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
