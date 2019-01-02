package org.artorg.tools.phantomData.client.editor;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.modelUI.PropertyUI;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.AbstractPropertifiedEntity;
import org.artorg.tools.phantomData.server.model.AbstractProperty;
import org.artorg.tools.phantomData.server.models.base.property.BooleanProperty;
import org.artorg.tools.phantomData.server.models.base.property.PropertyField;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class DbPropertySelector<T> extends VBox {
	private final Class<? extends AbstractPropertifiedEntity<?>> parentItemClass;
	@SuppressWarnings("rawtypes")
	private ProTableView<AbstractProperty> tableView;
	private AnchorPane editorPane;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DbPropertySelector(Class<T> parentItemClass) {
		this.parentItemClass = (Class<? extends AbstractPropertifiedEntity<?>>) parentItemClass;

		ItemEditor<BooleanProperty> propertyEditor = createEditFactory(BooleanProperty.class);
		propertyEditor.showCreateMode();

		
		editorPane = new AnchorPane();
		FxUtil.addToPane(editorPane, propertyEditor);
		this.getChildren().add(editorPane);

		tableView = createPropertyUI().createProTableView();

		tableView.setRowFactory(
				new Callback<TableView<AbstractProperty>, TableRow<AbstractProperty>>() {
					@Override
					public TableRow<AbstractProperty> call(TableView<AbstractProperty> tableView) {
						final TableRow<AbstractProperty> row = new TableRow<>();

						ContextMenu contextMenu = new ContextMenu();
						MenuItem menuItem;
						menuItem = new MenuItem("Edit");
						menuItem.setOnAction(event -> {
							ItemEditor<AbstractProperty> editor = createEditFactory((Class<AbstractProperty>) row.getItem().getClass());
//									Main.getPropertyUIEntity(row.getItem().getClass())
//											.createEditFactory();
							editorPane.getChildren().clear();
							FxUtil.addToPane(editorPane, editor);
							editor.showEditMode(row.getItem());
						});
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

		this.getChildren().add(tableView);

	}
	
	private UIEntity<AbstractProperty> createPropertyUI() {
		return new UIEntity<AbstractProperty>() {

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
				List<AbstractColumn<AbstractProperty, ?>> columns = new ArrayList<>();
				ColumnCreator<AbstractProperty, AbstractProperty> creator = new ColumnCreator<>(table);
				columns.add(
						creator.createFilterColumn("Name", path -> path.getPropertyField().getName()));
				columns.add(creator.createFilterColumn("Value", path -> String.valueOf(path.getValue()),
						(path, value) -> path
								.setValue(Main.getPropertyUIEntity(path.getClass()).fromString(value))));
				createPersonifiedColumns(table, columns);
				return columns;
			}

			@Override
			public ItemEditor<AbstractProperty> createEditFactory() {
				throw new UnsupportedOperationException();
			}
			
		};
	}
	
	private <T extends AbstractProperty> ItemEditor<T> createEditFactory(Class<T> propertyClass) {
		ItemEditor<T> editor = new ItemEditor<T>(propertyClass);
		PropertyGridPane propertyPane = new PropertyGridPane();
		propertyPane
				.addEntry("Property Field",
						editor.createComboBox(PropertyField.class, item -> item.getPropertyField(),
								(item, value) -> item.setPropertyField(value))
								.setMapper(p -> p.getName()));
		PropertyUI propertyUI = Main.getPropertyUIEntity(propertyClass);
		Node node = propertyUI.createValueNode();
		propertyPane.addEntry("Value",
				editor.createNode(item -> item.getValue(), (item, value) -> item.setValue(value),
						item -> propertyUI.getDefaultValue(), node, value -> propertyUI.setValueToNode(node, value),
						() -> propertyUI.getValueFromNode(node),
						() -> propertyUI.setValueToNode(node, propertyUI.getDefaultValue())));
		editor.add(propertyPane);
		
		editor.addApplyButton();
		return editor;
	}
	
	
	@SuppressWarnings("rawtypes")
	public List<AbstractProperty> getPropertyItems() {
		return tableView.getTable().getItems();
	}

	public void setPropertyItems(List<AbstractProperty> items) {
//		if (item == null) {
//			tableView.getTable().getItems().clear();
//			return;
//		}
//		List<AbstractProperty<?, ?>> items = new ArrayList<>();
//		items.addAll(item.getBooleanProperties());
//		items.addAll(item.getIntegerProperties());
//		items.addAll(item.getDoubleProperties());
//		items.addAll(item.getStringProperties());
//		items.addAll(item.getDateProperties());
		tableView.getTable().getItems().clear();
		tableView.getTable().getItems().addAll(items);
	}

	@SuppressWarnings("unchecked")
	public Class<T> getParentItemClass() {
		return (Class<T>) parentItemClass;
	}

}
