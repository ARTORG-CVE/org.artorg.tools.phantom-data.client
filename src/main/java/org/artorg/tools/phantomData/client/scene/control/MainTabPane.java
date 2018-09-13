package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.controllers.ItemFormFactory;
import org.artorg.tools.phantomData.client.scene.control.table.AddableToAnchorPane;
import org.artorg.tools.phantomData.client.scene.control.table.FilterTable;
import org.artorg.tools.phantomData.client.scene.control.table.Table;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

public class MainTabPane extends TabPane implements AddableToAnchorPane {
	private TabPane tabPane;

	{
		tabPane = this;
	}

	public <TABLE extends Table<TABLE, ITEM, ID_TYPE>, ITEM extends DatabasePersistent<ID_TYPE>, ID_TYPE> void openTableTab(
			FilterTable<TABLE, ITEM, ID_TYPE> table, String name) {
		MainTable<TABLE, ITEM, ID_TYPE> mainTable = new MainTable<TABLE, ITEM, ID_TYPE>();
		mainTable.setTable(table);

		Tab tab = new Tab(name);
		tab.setContent(mainTable);
		super.getTabs().add(tab);
		super.getSelectionModel().select(tab);

		mainTable.setRowFactory(new Callback<TableView<ITEM>, TableRow<ITEM>>() {
			@Override
			public TableRow<ITEM> call(TableView<ITEM> tableView) {
				final TableRow<ITEM> row = new TableRow<ITEM>();
				final ContextMenu rowMenu = new ContextMenu();
				MenuItem editItem = new MenuItem("Add item");
				editItem.setOnAction(event -> {
					Node node = ItemFormFactory.createForm(table.getItemClass());
					Tab tab = new Tab("Add " + table.getItemName());
					tab.setContent(node);
					tabPane.getTabs().add(tab);
					tabPane.getSelectionModel().select(tab);
				});
				MenuItem removeItem = new MenuItem("Delete");
				removeItem.setOnAction(event -> {
					table.getItems().remove(row.getItem());
				});
				rowMenu.getItems().addAll(editItem, removeItem);

				// only display context menu for non-null items:
				row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty()))
						.then(rowMenu).otherwise((ContextMenu) null));
				return row;
			}
		});
	}

	@Override
	public void addTo(AnchorPane pane) {
		FxUtil.addToAnchorPane(pane, this);
	}

}
