package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.table.AddableToAnchorPane;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.beans.binding.Bindings;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

public class MainTableTabPane extends TabPane implements AddableToAnchorPane {
	private TabPane tabPane;
	private MainSplitPane mainSplitPane;

	{
		tabPane = this;
	}
	
	public MainTableTabPane(MainSplitPane splitPane) {
		this.mainSplitPane = splitPane;
	}	

	public <ITEM extends DatabasePersistent & Comparable<ITEM>> void openTableTab(
			TableViewSpring<ITEM> tableViewSpring, String name) {
		Tab tab = new Tab(name);
		tab.setContent(tableViewSpring);
		tabPane.getTabs().add(tab);
		tabPane.getSelectionModel().select(tab);

		tableViewSpring.setRowFactory(new Callback<TableView<ITEM>, TableRow<ITEM>>() {
			@Override
			public TableRow<ITEM> call(TableView<ITEM> tableView) {
				final TableRow<ITEM> row = new TableRow<ITEM>();
				
				final ContextMenu rowMenu = new ContextMenu();
				MenuItem refreshItem = new MenuItem("Refresh");
				refreshItem.setOnAction(event -> {
					tableViewSpring.refresh();
				});
				MenuItem editItem = new MenuItem("Edit item");
				editItem.setOnAction(event -> {
					ItemEditFactoryController<ITEM> controller = tableViewSpring.createAddEditController();
					AnchorPane node = controller.edit(row.getItem());
					mainSplitPane.addNewItemTab(node, "Edit " + tableViewSpring.getFilterTable().getItemName());
				});
				MenuItem addItem = new MenuItem("Add item");
				addItem.setOnAction(event -> {
					ItemEditFactoryController<ITEM> controller = tableViewSpring.createAddEditController();
					AnchorPane node = controller.create(row.getItem());
					mainSplitPane.addNewItemTab(node, "Add " + tableViewSpring.getFilterTable().getItemName());
				});
				MenuItem removeItem = new MenuItem("Delete");
				removeItem.setOnAction(event -> {
					tableViewSpring.getItems().remove(row.getItem());
				});
				rowMenu.getItems().addAll(refreshItem, editItem, addItem, removeItem);

				// only display context menu for non-null items:
				row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty()))
						.then(rowMenu).otherwise((ContextMenu) null));
				return row;
			}
		});
		
		ContextMenu contextMenu = new ContextMenu();
		MenuItem editItem = new MenuItem("Add item");
		editItem.setOnAction(event -> {
			ItemEditFactoryController<ITEM> controller = tableViewSpring.createAddEditController();
			AnchorPane node = controller.create();
			mainSplitPane.addNewItemTab(node, "Add " + tableViewSpring.getFilterTable().getItemName());
		});
		contextMenu.getItems().addAll(editItem);
		
		tableViewSpring.setContextMenu(contextMenu);
		
		
	}

	@Override
	public void addTo(AnchorPane pane) {
		FxUtil.addToAnchorPane(pane, this);
	}

}
