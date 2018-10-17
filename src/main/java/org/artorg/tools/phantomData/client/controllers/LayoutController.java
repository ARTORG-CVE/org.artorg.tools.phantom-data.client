package org.artorg.tools.phantomData.client.controllers;

import org.artorg.tools.phantomData.client.scene.control.tableView.DbUndoRedoAddEditControlFilterTableView;
import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.scene.control.treeTableView.DbTreeTableView;
import org.artorg.tools.phantomData.client.scene.control.treeTableView.ProTreeTableView;
import org.artorg.tools.phantomData.client.scene.layout.AddableToAnchorPane;
import org.artorg.tools.phantomData.client.table.DbTable;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.TableViewFactory;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;

public class LayoutController extends SplitPane implements AddableToAnchorPane {
	private SplitPane splitPane;
	private ObservableList<SplitTabView> splitTabViews;

	{
		this.splitPane = this;
		splitPane.setOrientation(Orientation.VERTICAL);
		splitTabViews = FXCollections.<SplitTabView>observableArrayList();
		splitTabViews.addListener(new ListChangeListener<SplitTabView>() {
			@Override
			public void onChanged(Change<? extends SplitTabView> c) {
				if (c.next())
					do {
						if (c.wasAdded())
							splitPane.getItems().addAll(c.getAddedSubList());
						if (c.wasRemoved())
							splitPane.getItems().removeAll(c.getRemoved());
					} while (c.next());
			}
		});
		
		splitTabViews.add(new SplitTabView());
		splitTabViews.add(new SplitTabView());
	}

	public <T extends DbPersistent<T, ?>> void openTableTab(
		Class<T> itemClass) {
		ProTableView<T> table = createTable(itemClass);
		openTab(table, table.getTable().getTableName());
	}

	public <T extends DbPersistent<T, ?>> void openTreeTableTab(
		Class<T> itemClass) {
		ProTreeTableView<T> table = createTreeTable(itemClass);
		openTab(table, table.getTable().getTableName());
	}

	public <T extends DbPersistent<T, ?>> void openBottomTreeTableTab(
		Class<T> itemClass) {
		ProTreeTableView<T> table = createTreeTable(itemClass);
		openTab(1, table, table.getTable().getTableName());
	}

	@SuppressWarnings("unchecked")
	private <T extends DbPersistent<T, ?>> ProTableView<T> createTable(
		Class<T> itemClass) {
		return TableViewFactory.createInitializedTable(itemClass,
			DbUndoRedoFactoryEditFilterTable.class,
			DbUndoRedoAddEditControlFilterTableView.class);
	}

	@SuppressWarnings("unchecked")
	private <T extends DbPersistent<T, ?>> ProTreeTableView<T> createTreeTable(
		Class<T> itemClass) {
		return TableViewFactory.createInitializedTreeTable(itemClass,
			DbTable.class, DbTreeTableView.class);
	}
	
	public <T extends DbPersistent<T, ?>> void openTab(ProTableView<T> table,
		String name) {
		openTab(0, table, name);
	}
	
	public <T extends DbPersistent<T, ?>> void openTab(int row, ProTableView<T> table,
		String name) {
		getOrCreate(row).openTab(table, name);
	}

	public <T extends DbPersistent<T, ?>> void openTab(
		ProTreeTableView<T> table, String name) {
		openTab(0, table, name);
	}

	public <T extends DbPersistent<T, ?>> void openTab(int row,
		ProTreeTableView<T> table, String name) {
		getOrCreate(row).openTab(table, name);
	}

	private SplitTabView getOrCreate(int row) {
		if (splitTabViews.size() - 1 < row)
			for (int i = splitTabViews.size() - 1; i < row; i++)
				splitTabViews.add(new SplitTabView());
		return splitTabViews.get(row);
	}

}
