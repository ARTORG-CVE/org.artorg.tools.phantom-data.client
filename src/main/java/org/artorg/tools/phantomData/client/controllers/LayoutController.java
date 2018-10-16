package org.artorg.tools.phantomData.client.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.artorg.tools.phantomData.client.scene.control.tableView.DbUndoRedoAddEditControlFilterTableView;
import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.scene.control.treeTableView.DbTreeTableView;
import org.artorg.tools.phantomData.client.scene.control.treeTableView.ProTreeTableView;
import org.artorg.tools.phantomData.client.table.DbTable;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.TableViewFactory;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class LayoutController {
	private SplitTabView mainSplitPane;
	private SplitTabView bottomSplitPane;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private AnchorPane parentPane, mainTablePane, bottomTablePane;

	@FXML
	void initialize() {
		assert parentPane != null : "fx:id=\"parentPane\" was not injected: check your FXML file 'PhantomLayout.fxml'.";
		assert mainTablePane != null : "fx:id=\"mainTablePane\" was not injected: check your FXML file 'PhantomLayout.fxml'.";
		assert bottomTablePane != null : "fx:id=\"tableBottomPane\" was not injected: check your FXML file 'PhantomLayout.fxml'.";
	}

	public void init() {
		mainSplitPane = new SplitTabView();
		mainSplitPane.addTo(mainTablePane);
		
		bottomSplitPane = new SplitTabView();
		bottomSplitPane.addTo(bottomTablePane);
	}

	public <T extends DbPersistent<T,?>> void openMainTableTab(Class<T> itemClass) {
		ProTableView<T> table = createTable(itemClass);
		openMainTableTab(table, table.getTable().getTableName());
	}

	public <T extends DbPersistent<T,?>> void openBottomTreeTableTab(Class<T> itemClass) {
		ProTreeTableView<T> table = createTreeTable(itemClass);
		setSecondTable(table, table.getTable().getTableName());
	}

	@SuppressWarnings("unchecked")
	private <T extends DbPersistent<T,?>> ProTableView<T> createTable(
			Class<T> itemClass) {
		return TableViewFactory.createInitializedTable(itemClass, DbUndoRedoFactoryEditFilterTable.class, DbUndoRedoAddEditControlFilterTableView.class);
	}
	
	@SuppressWarnings("unchecked")
	private <T extends DbPersistent<T,?>> ProTreeTableView<T> createTreeTable(
			Class<T> itemClass) {
		return TableViewFactory.createInitializedTreeTable(itemClass, DbTable.class, DbTreeTableView.class);
	}

	private <T extends DbPersistent<T, ?>> void openMainTableTab(ProTableView<T> table,
			String name) {
		mainSplitPane.openTab(table, name);
	}

	private <T extends DbPersistent<T, ?>> void setSecondTable(ProTreeTableView<T> table, String name) {
		bottomSplitPane.openTab(table, name);
	}

}
