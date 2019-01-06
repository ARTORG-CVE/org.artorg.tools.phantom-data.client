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
	private final PropertySelectorItemEditor editor;
	private final Pane valuePane = new AnchorPane();
	private final ComboBox<PropertyField> comboBoxCreate = new ComboBox<>();
	private IPropertyNode valuePropertyNode = null;

	public DbPropertySelector(Class<T> parentItemClass) {
		this.parentItemClass = parentItemClass;

		ICrudConnector<PropertyField> connector = Connectors.get(PropertyField.class);
		this.propertyFields =
				connector.readAllAsList().stream()
						.filter(propertyField -> propertyField.getEntityType()
								.equals(parentItemClass.getSimpleName()))
						.collect(Collectors.toList());

		editor = createEditFactory();
		addPropertyNodes();
		initComboBox(comboBoxCreate, propertyFields);
//		addChangeListener(comboBox);
		editor.setBeanClass(BooleanProperty.class);
		editor.showCreateMode();
		tableView = createTableView();
		this.setSpacing(10.0);
		this.getChildren().add(editor);
		this.getChildren().add(tableView);
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

	@SuppressWarnings("unchecked")
	private void addChangeListener(ComboBox<PropertyField> comboBoxPropertyField) {
		comboBoxPropertyField.getSelectionModel().selectedItemProperty()
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
//						propertyEditor.showCreateMode();
						editor.updateValueNode(propertyClass);
					});
				});
	}

	private void addPropertyNodes() {
		PropertyGridPane propertyPane = new PropertyGridPane();
		propertyPane.addEntry("Property Field", editor.create(comboBoxCreate,
				item -> item.getPropertyField(), (item, value) -> item.setPropertyField(value)));

		propertyPane.addEntry(new Label("Value"), valuePane);
		editor.add(propertyPane);

		Button applyButton = editor.getApplyButton();
		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(event -> editor.showCreateMode());
		Pane buttonPane = ItemEditor.createButtonPane(applyButton, cancelButton);
		propertyPane.addEntry(buttonPane);
	}

	private static void initComboBox(ComboBox<PropertyField> comboBox,
			Collection<PropertyField> propertyFields) {
		comboBox.setItems(FXCollections.observableArrayList(propertyFields));
		Pattern pattern = Pattern.compile("(?i)(.*)property");
		FxUtil.setComboBoxCellFactory(comboBox, item -> {
			Matcher matcher = pattern.matcher(item.getPropertyType());
			String type = "";
			if (matcher.find()) type = matcher.group(1);
			return String.format("%s (%s)", item.getName(), type);
		});
	}

	@SuppressWarnings("unchecked")
	private PropertySelectorItemEditor createEditFactory() {
		return new PropertySelectorItemEditor() {

			@Override
			public void onCreatedServer(AbstractProperty item) {
				getPropertyItems().add(item);
			}

			@Override
			public void onShowingCreateMode(Class<? extends AbstractProperty> beanClass) {
				updateValueNode(beanClass);
			}

			@Override
			public void onShowingEditMode(AbstractProperty item) {
				comboBoxCreate.getSelectionModel().select(item.getPropertyField());
				updateValueNode(item.getClass());
			}

			public void updateValueNode(Class<? extends AbstractProperty> propertyClass) {
				if (valuePropertyNode == null) Logger.info.println("valuePropertyNode == null");
				if (valuePropertyNode != null) Logger.info.println("valuePropertyNode != null");

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

		};
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
				menuItem.setOnAction(event -> {
					editor.showEditMode(row.getItem());
				});
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
		public PropertySelectorItemEditor() {
			super(AbstractProperty.class);
		}

		public abstract void updateValueNode(Class<? extends AbstractProperty> propertyClass);

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
