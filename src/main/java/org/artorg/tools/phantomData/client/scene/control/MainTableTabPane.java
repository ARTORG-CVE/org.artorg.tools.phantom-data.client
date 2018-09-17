package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.controller.ItemFormFactory;
import org.artorg.tools.phantomData.client.scene.control.table.AddableToAnchorPane;
import org.artorg.tools.phantomData.client.scene.control.table.FilterTableSpringDb;
import org.artorg.tools.phantomData.client.scene.control.table.TableSpringDb;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
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

	public <ITEM extends DatabasePersistent<ID_TYPE>, ID_TYPE> void openTableTab(
			TableViewSpring<ITEM, ID_TYPE> tableViewSpring, String name) {
//		MainTable<ITEM, ID_TYPE> mainTable = new MainTable<ITEM, ID_TYPE>();
//		mainTable.setTable(table);

		Tab tab = new Tab(name);
		tab.setContent(tableViewSpring);
		tabPane.getTabs().add(tab);
		tabPane.getSelectionModel().select(tab);

		tableViewSpring.setRowFactory(new Callback<TableView<ITEM>, TableRow<ITEM>>() {
			@Override
			public TableRow<ITEM> call(TableView<ITEM> tableView) {
				final TableRow<ITEM> row = new TableRow<ITEM>();
				final ContextMenu rowMenu = new ContextMenu();
				MenuItem editItem = new MenuItem("Add item");
				editItem.setOnAction(event -> {
					AnchorPane node = ItemFormFactory.createForm(tableViewSpring.getFilterTable().getItemClass());
					
					
					tableViewSpring.getAddEditController();
					
					TabPane tabPane2 = new TabPane();
					Tab tab = new Tab("Add " + tableViewSpring.getFilterTable().getItemName());
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
					tableViewSpring.getItems().remove(row.getItem());
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
