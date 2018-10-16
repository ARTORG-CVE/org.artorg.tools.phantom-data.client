package org.artorg.tools.phantomData.client.controllers;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.control.tableView.DbUndoRedoAddEditControlFilterTableView;
import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.scene.control.treeTableView.DbTreeTableView;
import org.artorg.tools.phantomData.client.scene.control.treeTableView.ProTreeTableView;
import org.artorg.tools.phantomData.client.scene.layout.AddableToAnchorPane;
import org.artorg.tools.phantomData.client.table.DbTable;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.TableViewFactory;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;

public class LayoutController extends SplitPane implements AddableToAnchorPane {
	private SplitPane splitPane;
	private List<SplitTabView> splitTabViews;
	private SplitTabView mainSplitPane;
	private SplitTabView bottomSplitPane;

	{
		this.splitPane = this;
		mainSplitPane = new SplitTabView();
		bottomSplitPane = new SplitTabView();
		splitTabViews = new ArrayList<SplitTabView>();
		splitTabViews.add(mainSplitPane);
		splitTabViews.add(bottomSplitPane);
		
		splitPane.setOrientation(Orientation.VERTICAL);
		splitPane.getItems().addAll(splitTabViews.get(0), splitTabViews.get(1));
	}

	public void init() {
		
	}

	public <T extends DbPersistent<T,?>> void openTableTab(Class<T> itemClass) {
		ProTableView<T> table = createTable(itemClass);
		openTab(table, table.getTable().getTableName());
	}
	
	public <T extends DbPersistent<T,?>> void openTreeTableTab(Class<T> itemClass) {
		ProTreeTableView<T> table = createTreeTable(itemClass);
		openTab(table, table.getTable().getTableName());
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

	private <T extends DbPersistent<T, ?>> void openTab(ProTableView<T> table,
			String name) {
		splitTabViews.get(0).openTab(table, name);
	}
	
	private <T extends DbPersistent<T, ?>> void openTab(ProTreeTableView<T> table, String name) {
		splitTabViews.get(0).openTab(table, name);
	}

	private <T extends DbPersistent<T, ?>> void setSecondTable(ProTreeTableView<T> table, String name) {
		splitTabViews.get(1).openTab(table, name);
	}

}
