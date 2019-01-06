package org.artorg.tools.phantomData.client.editor;

import java.util.ArrayList;
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
import org.artorg.tools.phantomData.client.modelUI.PropertyUI;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.AbstractProperty;
import org.artorg.tools.phantomData.server.models.base.property.BooleanProperty;
import org.artorg.tools.phantomData.server.models.base.property.PropertyField;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

@SuppressWarnings("rawtypes")
public class DbPropertySelector<T> extends VBox {
	private final Class<T> parentItemClass;
	private final List<PropertyField> propertyFields;
	private final ProTableView<AbstractProperty> tableView;
	private final PropertySelectorItemEditor creatingEditor;
	private final PropertySelectorItemEditor editingEditor;
	private final Pane editPane = new AnchorPane();

	public DbPropertySelector(Class<T> parentItemClass) {
		this.parentItemClass = parentItemClass;

		ICrudConnector<PropertyField> connector = Connectors.get(PropertyField.class);
		this.propertyFields =
				connector.readAllAsList().stream()
						.filter(propertyField -> propertyField.getEntityType()
								.equals(parentItemClass.getSimpleName()))
						.collect(Collectors.toList());

		tableView = createTableView();

		creatingEditor = createCreator();
		creatingEditor.setBeanClass(BooleanProperty.class);
		creatingEditor.showCreateMode();

		editingEditor = createEditFactory();
//		editor = null;

		this.setSpacing(10.0);

		this.getChildren().add(editPane);
		this.getChildren().add(tableView);
		showCreateMode();
	}

	private void showCreateMode() {
		editPane.getChildren().clear();
		FxUtil.addToPane(editPane, creatingEditor);
		creatingEditor.showCreateMode();
	}

	private void showEditMode(AbstractProperty item) {
		editPane.getChildren().clear();
		FxUtil.addToPane(editPane, editingEditor);
		editingEditor.showEditMode(item);
	}

	@SuppressWarnings("unchecked")
	private PropertySelectorItemEditor createCreator() {
		ComboBox<PropertyField> comboBoxCreate = new ComboBox<>();
		comboBoxCreate.setItems(FXCollections.observableArrayList(propertyFields));
		Pattern pattern = Pattern.compile("(?i)(.*)property");
		FxUtil.setComboBoxCellFactory(comboBoxCreate, item -> {
			Matcher matcher = pattern.matcher(item.getPropertyType());
			String type = "";
			if (matcher.find()) type = matcher.group(1);
			return String.format("%s (%s)", item.getName(), type);
		});

		PropertySelectorItemEditor editor = new PropertySelectorItemEditor() {
			@Override
			public void onCreatedServer(AbstractProperty item) {
				getPropertyItems().add(item);
			}

			@Override
			public void onShowingCreateMode(Class<? extends AbstractProperty> beanClass) {
				updateValueNode(beanClass);
				updateComboBox();
			}

			@Override
			public void onShowingEditMode(AbstractProperty item) {
				throw new UnsupportedOperationException();
			}

			@Override
			protected void updateComboBox() {
				List<PropertyField> filteredPropertyFields = propertyFields.stream()
						.filter(propertyField -> !getPropertyItems().stream()
								.map(property -> property.getPropertyField())
								.filter(propertyField2 -> propertyField2.equalsId(propertyField))
								.findFirst().isPresent())
						.collect(Collectors.toList());
				comboBoxCreate.getItems().clear();
				comboBoxCreate.getItems().addAll(filteredPropertyFields);
			}

		};

		comboBoxCreate.setOnShowing(event -> editor.updateComboBox());
		comboBoxCreate.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> {
					PropertyField propertyField = newValue;
					if (propertyField != null) Platform.runLater(() -> {
						String propertyType = propertyField.getPropertyType();
						Class<? extends AbstractProperty<?, ?>> propertyClass =
								Main.getPropertyclasses().stream()
										.filter(cls -> cls.getSimpleName().equals(propertyType))
										.map(cls -> (Class<? extends AbstractProperty<?, ?>>) cls)
										.findFirst().get();
						editor.setBeanClass(propertyClass);
						editor.updateValueNode(propertyClass);
					});
				});

		PropertyGridPane propertyPane = new PropertyGridPane();
		propertyPane.addEntry("Property Field", editor.create(comboBoxCreate,
				item -> item.getPropertyField(), (item, value) -> item.setPropertyField(value)));

		propertyPane.addEntry(new Label("Value"), editor.getValuePane());

		Pane buttonPane = ItemEditor.createButtonPane(editor.getApplyButton());
		propertyPane.addEntry(buttonPane);

		editor.add(propertyPane);

		return editor;
	}

	private PropertySelectorItemEditor createEditFactory() {
		ComboBox<PropertyField> comboBoxPropertyField = new ComboBox<>();
		comboBoxPropertyField.setItems(FXCollections.observableArrayList(propertyFields));
		Pattern pattern = Pattern.compile("(?i)(.*)property");
		FxUtil.setComboBoxCellFactory(comboBoxPropertyField, item -> {
			Matcher matcher = pattern.matcher(item.getPropertyType());
			String type = "";
			if (matcher.find()) type = matcher.group(1);
			return String.format("%s (%s)", item.getName(), type);
		});

		PropertySelectorItemEditor editor = new PropertySelectorItemEditor() {
			@Override
			public void onShowingCreateMode(Class<? extends AbstractProperty> beanClass) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void onShowingEditMode(AbstractProperty item) {
				updateComboBox();
				updateValueNode(item.getClass());
			}

			@Override
			public void onUpdatedServer(AbstractProperty item) {
				tableView.refresh();
			}

			@Override
			protected void updateComboBox() {
				String type = getItem().getPropertyField().getPropertyType();
				List<PropertyField> filteredPropertyFields = propertyFields.stream()
						.filter(propertyField -> !getPropertyItems().stream()
								.map(property -> property.getPropertyField())
								.filter(propertyField2 -> propertyField2.equalsId(propertyField))
								.findFirst().isPresent())
						.collect(Collectors.toList());
				List<PropertyField> propertyTypedPropertyFields = filteredPropertyFields.stream()
						.filter(propertyField -> propertyField.getPropertyType().equals(type))
						.collect(Collectors.toList());
				if (!propertyTypedPropertyFields.contains(getItem().getPropertyField()))
					propertyTypedPropertyFields.add(getItem().getPropertyField());
				comboBoxPropertyField
						.setItems(FXCollections.observableArrayList(propertyTypedPropertyFields));
			}

		};
		PropertyGridPane propertyPane = new PropertyGridPane();
		propertyPane.addEntry("Property Field", editor.create(comboBoxPropertyField,
				item -> item.getPropertyField(), (item, value) -> item.setPropertyField(value)));

		propertyPane.addEntry(new Label("Value"), editor.getValuePane());

		Button applyButton = editor.getApplyButton();
		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(event -> showCreateMode());
		Pane buttonPane = ItemEditor.createButtonPane(applyButton, cancelButton);
		propertyPane.addEntry(buttonPane);

		editor.add(propertyPane);

		return editor;
	}

	@SuppressWarnings("unchecked")
	private static <T extends AbstractProperty> List<AbstractColumn<T, ? extends Object>>
			createColumns(Table<T> table, List<T> items) {
		List<AbstractColumn<T, ?>> columns = new ArrayList<>();
		ColumnCreator<T, T> creator = new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Name", path -> path.getPropertyField().getName()));
		AbstractFilterColumn<T, ?> column = creator.createFilterColumn("Value",
				path -> String.valueOf(path.getValue()), (path, value) -> path
						.setValue(Main.getPropertyUIEntity(path.getClass()).fromString(value)));
		column.setItemsFilter(false);
		columns.add(column);
		UIEntity.createPersonifiedColumns(table, columns);
		return columns;
	}

	private <U extends AbstractProperty> Callback<TableView<U>, TableRow<U>>
			createRowFactory(ProTableView<U> tableView) {
		return new Callback<TableView<U>, TableRow<U>>() {
			@Override
			public TableRow<U> call(TableView<U> tableView) {
				final TableRow<U> row = new TableRow<>();

				ContextMenu contextMenu = new ContextMenu();
				MenuItem menuItem;
				menuItem = new MenuItem("Edit");
				menuItem.setOnAction(event -> showEditMode(row.getItem()));
				contextMenu.getItems().add(menuItem);

				menuItem = new MenuItem("Remove");
				menuItem.setOnAction(event -> getPropertyItems().remove(row.getItem()));
				contextMenu.getItems().add(menuItem);

				menuItem = new MenuItem("Refresh");
				menuItem.setOnAction(event -> {});
				contextMenu.getItems().add(menuItem);

				row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty()))
						.then(contextMenu).otherwise((ContextMenu) null));
				return row;
			};
		};
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

			@Override
			public List<AbstractColumn<AbstractProperty, ? extends Object>>
					createColumns(Table<AbstractProperty> table, List<AbstractProperty> items) {
				return DbPropertySelector.createColumns(table, items);
			}

			@Override
			public ItemEditor<AbstractProperty> createEditFactory() {
				throw new UnsupportedOperationException();
			}

		}.createProTableView();
		tableView.setRowFactory(createRowFactory(tableView));
		return tableView;
	}

	private static abstract class PropertySelectorItemEditor extends ItemEditor<AbstractProperty> {
		private final Pane valuePane = new AnchorPane();
		private IPropertyNode valuePropertyNode;

		public PropertySelectorItemEditor() {
			super(AbstractProperty.class);
		}

		protected abstract void updateComboBox();

		@SuppressWarnings("unchecked")
		public void updateValueNode(Class<? extends AbstractProperty> propertyClass) {
			if (valuePropertyNode != null) getChildrenProperties().remove(valuePropertyNode);
			PropertyUI propertyUI = Main.getPropertyUIEntity(propertyClass);

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

		public Pane getValuePane() {
			return valuePane;
		}

	}

	public List<AbstractProperty> getPropertyItems() {
		return tableView.getTable().getItems();
	}

	public void setPropertyItems(List<AbstractProperty> items) {
		tableView.getTable().getItems().clear();
		tableView.getTable().getItems().addAll(items);
	}

	public final Class<T> getParentItemClass() {
		return parentItemClass;
	}

}
