package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.controllers.ItemFormFactory;
import org.artorg.tools.phantomData.client.scene.control.table.AddableToAnchorPane;
import org.artorg.tools.phantomData.client.scene.control.table.FilterTable;
import org.artorg.tools.phantomData.client.scene.control.table.Table;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.beans.binding.Bindings;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class MainTableTabPane extends SplitPane implements AddableToAnchorPane {
	private TabPane tabPane;
	private SplitPane splitPane;

	{
		tabPane = new TabPane();
		splitPane = this;
		splitPane.setOrientation(Orientation.HORIZONTAL);
		splitPane.getItems().add(tabPane);
	}

	public <TABLE extends Table<TABLE, ITEM, ID_TYPE>, ITEM extends DatabasePersistent<ID_TYPE>, ID_TYPE> void openTableTab(
			FilterTable<TABLE, ITEM, ID_TYPE> table, String name) {
		MainTable<TABLE, ITEM, ID_TYPE> mainTable = new MainTable<TABLE, ITEM, ID_TYPE>();
		mainTable.setTable(table);

		Tab tab = new Tab(name);
		tab.setContent(mainTable);
		tabPane.getTabs().add(tab);
		tabPane.getSelectionModel().select(tab);

		mainTable.setRowFactory(new Callback<TableView<ITEM>, TableRow<ITEM>>() {
			@Override
			public TableRow<ITEM> call(TableView<ITEM> tableView) {
				final TableRow<ITEM> row = new TableRow<ITEM>();
				final ContextMenu rowMenu = new ContextMenu();
				MenuItem editItem = new MenuItem("Add item");
				editItem.setOnAction(event -> {
					AnchorPane node = ItemFormFactory.createForm(table.getItemClass());
					
					
					
					
					TabPane tabPane2 = new TabPane();
					Tab tab = new Tab("Add " + table.getItemName());
					tab.setContent(node);
					tabPane2.getTabs().add(tab);
					tabPane2.getSelectionModel().select(tab);
					tab.setOnClosed(closeEvent -> {
						if (tabPane2.getTabs().size()==0)
							splitPane.getItems().remove(tabPane2);
					});
					
					
//					tabPane2.setNodeOrientation(NodeOrientation.INHERIT);
					splitPane.getItems().remove(tabPane);
					splitPane.getItems().addAll(tabPane, tabPane2);
					
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
