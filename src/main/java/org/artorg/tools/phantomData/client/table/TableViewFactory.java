package org.artorg.tools.phantomData.client.table;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.beans.DbNode;
import org.artorg.tools.phantomData.client.scene.control.tableView.DbTableView;
import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.scene.control.treeTableView.DbTreeTableView;
import org.artorg.tools.phantomData.client.scene.control.treeTableView.ProTreeTableView;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class TableViewFactory {

	public static <T extends DbPersistent<T, ?>, TABLE extends TableBase<T>,
		TABLE_VIEW extends ProTreeTableView<T>> ProTreeTableView<T>
		createInitializedTreeTableView(Class<?> itemClass, Class<TABLE> tableClass,
			Class<TABLE_VIEW> tableViewClass) {
		ProTreeTableView<T> tableView =
			createTreeTableView(itemClass, tableClass, tableViewClass);

		if (tableView instanceof DbTreeTableView)
			((DbTreeTableView<T>) tableView).reload();

		tableView.initTable();

		return tableView;
	}

	public static <T extends DbPersistent<T, ?>, TABLE extends TableBase<T>,
		TABLE_VIEW extends ProTreeTableView<T>> ProTreeTableView<T>
		createTreeTableView(Class<?> itemClass, Class<TABLE> tableClass,
			Class<TABLE_VIEW> tableViewClass, List<T> items) {
		ProTreeTableView<T> treeTableView =
			createTreeTableView(itemClass, tableClass, tableViewClass);
		treeTableView.setItems(items);
		return treeTableView;
	}

	public static <T extends DbPersistent<T, ?>, TABLE extends TableBase<T>,
		TABLE_VIEW extends ProTreeTableView<T>> ProTreeTableView<T>
		createTreeTableView(Class<?> itemClass, Class<TABLE> tableClass,
			Class<TABLE_VIEW> tableViewClass) {

		TableBase<T> table = createTableBase(itemClass, tableClass);

		ProTreeTableView<T> tableView = null;
		try {
			tableView = tableViewClass.getConstructor(Class.class).newInstance(itemClass);
		} catch (InstantiationException | IllegalAccessException
			| IllegalArgumentException | InvocationTargetException | NoSuchMethodException
			| SecurityException e) {
			e.printStackTrace();
		}
		tableView.setTable(table);

		return tableView;
	}

	public static <T, TABLE extends TableBase<T>,
		TABLE_VIEW extends ProTableView<T>> ProTableView<T>
		createInitializedTableView(Class<?> itemClass, Class<TABLE> tableClass,
			Class<TABLE_VIEW> tableViewClass) {
		ProTableView<T> tableView = createTableView(itemClass, tableClass, tableViewClass);

		if (tableView instanceof DbTableView) ((DbTableView<?>) tableView).reload();

		tableView.initTable();

		return tableView;
	}

	@SuppressWarnings("unchecked")
	public static <T, TABLE extends TableBase<T>,
		TABLE_VIEW extends ProTableView<T>> ProTableView<T>
		createTableView(Class<?> itemClass, Class<TABLE> tableClass,
			Class<TABLE_VIEW> tableViewClass, List<TreeItem<DbNode>> treeItems) {
		ProTableView<T> tableView = createTableView(itemClass, tableClass, tableViewClass);
		ObservableList<T> items = FXCollections.observableArrayList();
		for (int i = 0; i < treeItems.size(); i++)
			try {
				T item = (T) treeItems.get(i).getValue().getValue();
				items.add(item);
			} catch (Exception e) {}

		tableView.setItems(items);
		return tableView;
	}
	
	public static <T, TABLE extends TableBase<T>,
		TABLE_VIEW extends ProTableView<T>> ProTableView<T>
		createTableView(Class<?> itemClass, Class<TABLE> tableClass,
			Class<TABLE_VIEW> tableViewClass) {

		TableBase<T> table = createTableBase(itemClass, tableClass);
		ProTableView<T> tableView = null;
		try {
			tableView = tableViewClass.getConstructor(Class.class).newInstance(itemClass);
		} catch (InstantiationException | IllegalAccessException
			| IllegalArgumentException | InvocationTargetException | NoSuchMethodException
			| SecurityException e) {
			e.printStackTrace();
		}
		tableView.setTable(table);

		return tableView;
	}

	public static <T> TableBase<T>
		createTableBase(Class<?> itemClass, Class<? extends TableBase<T>> tableClass) {
		return Reflect.createInstanceByGenericAndSuperClass(tableClass, itemClass,
			Main.getReflections());
	}

}
