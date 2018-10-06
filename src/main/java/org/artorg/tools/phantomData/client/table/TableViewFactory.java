package org.artorg.tools.phantomData.client.table;

import java.lang.reflect.InvocationTargetException;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.scene.control.ProTableView;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class TableViewFactory {

	
//	public static <T extends DbPersistent<T, ?>, TABLE extends Table<T>, TABLE_VIEW extends ProTableView<T>> ProTableView<T> createTable(
//			Class<?> itemClass, Class<TABLE> tableClass, Class<TABLE_VIEW> tableViewClass) {
//		Table<T> table = Reflect.createInstanceByGenericAndSuperClass(tableClass, itemClass, Main.getReflections());
//
//		ProTableView<T> tableView = null;
//		try {
//			tableView = tableViewClass.getConstructor(Class.class).newInstance(itemClass);
//		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
//				| NoSuchMethodException | SecurityException e) {
//			e.printStackTrace();
//		}
//		tableView.setTable(table);
//
//		return tableView;
//	}
	
	
	public static <T extends DbPersistent<T, ?>, TABLE extends Table<T>, TABLE_VIEW extends ProTableView<T>> ProTableView<T> createTable(
			Class<?> itemClass, Class<TABLE> tableClass, Class<TABLE_VIEW> tableViewClass) {
		Table<T> table = Reflect.createInstanceByGenericAndSuperClass(tableClass, itemClass, Main.getReflections());

		ProTableView<T> tableView = null;
		try {
			tableView = tableViewClass.getConstructor(Class.class).newInstance(itemClass);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		tableView.setTable(table);

		return tableView;
	}

}
