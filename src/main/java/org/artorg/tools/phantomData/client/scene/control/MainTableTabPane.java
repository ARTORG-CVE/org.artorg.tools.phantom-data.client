package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.controller.ItemFormFactory;
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

	public <ITEM extends DatabasePersistent<ID_TYPE>, ID_TYPE> void openTableTab(
			TableViewSpring<ITEM, ID_TYPE> tableViewSpring, String name) {
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
					mainSplitPane.addNewItemTab(node, "Add " + tableViewSpring.getFilterTable().getItemName());
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
