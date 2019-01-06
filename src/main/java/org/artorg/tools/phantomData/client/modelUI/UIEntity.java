package org.artorg.tools.phantomData.client.modelUI;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.admin.UserAdmin;
import org.artorg.tools.phantomData.client.beans.EntityBeanInfo;
import org.artorg.tools.phantomData.client.beans.NamedTreeItem;
import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.AbstractFilterColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.column.OptionalColumnCreator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.logging.Logger;
import org.artorg.tools.phantomData.client.scene.control.tableView.DbTableView;
import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.scene.control.treeTableView.DbTreeTableView;
import org.artorg.tools.phantomData.client.table.DbTable;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.model.AbstractPersonifiedEntity;
import org.artorg.tools.phantomData.server.model.AbstractPropertifiedEntity;
import org.artorg.tools.phantomData.server.model.AbstractProperty;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public abstract class UIEntity<T> {
	private final EntityBeanInfo<T> entityBeanInfo;

	{
		entityBeanInfo = new EntityBeanInfo<>(getItemClass());
	}

	public abstract Class<T> getItemClass();

	public abstract String getTableName();

	public abstract List<AbstractColumn<T, ? extends Object>> createColumns(Table<T> table,
			List<T> items);

	public abstract ItemEditor<T> createEditFactory();

	public Table<T> createTableBase() {
		long startTime = System.currentTimeMillis();
		Table<T> table = new Table<T>(getItemClass()) {
			@Override
			public List<AbstractColumn<T, ? extends Object>> createColumns(List<T> items) {
				return UIEntity.this.createColumns(this, items);
			}
		};
		table.setTableName(getTableName());
		Logger.debug.println(String.format("%s - TableBase created in %d ms",
				getItemClass().getSimpleName(), System.currentTimeMillis() - startTime));
		return table;
	}

	public DbTable<T> createDbTableBase() {
		long startTime = System.currentTimeMillis();
		DbTable<T> table = new DbTable<T>(getItemClass()) {
			@Override
			public List<AbstractColumn<T, ? extends Object>> createColumns(List<T> items) {
				return UIEntity.this.createColumns(this, items);
			}
		};
		table.setTableName(getTableName());
		Logger.debug.println(String.format("%s - DbTable created in %d ms",
				getItemClass().getSimpleName(), System.currentTimeMillis() - startTime));
		return table;
	}

	public ProTableView<T> createProTableView() {
		ProTableView<T> tableView = new ProTableView<>(getItemClass(), createTableBase());
		Platform.runLater(() -> {
			if (tableView.isFilterable()) {
				tableView.getFilterMenuButtons().forEach(filterMenuButton -> {
					filterMenuButton.refreshImage();
				});
			}
		});
		return tableView;
	}

	@SuppressWarnings("unchecked")
	public DbTableView<T> createDbTableView(List<?> items) {
		long startTime = System.currentTimeMillis();
		DbTableView<T> tableView = new DbTableView<>(getItemClass(), createDbTableBase());
		Logger.info.println(String.format("%s - Table created with %d items in %d ms",
				getItemClass().getSimpleName(), tableView.getTable().getItems().size(),
				System.currentTimeMillis() - startTime));

		List<T> castedItems = items.stream().map(item -> (T)item).collect(Collectors.toList());
		tableView.getTable().getItems().clear();
		tableView.getTable().getItems().addAll(castedItems);
		
		return tableView;
	}
	
	public DbTableView<T> createDbTableView() {
		long startTime = System.currentTimeMillis();
		DbTableView<T> tableView = new DbTableView<>(getItemClass(), createDbTableBase());
		Logger.info.println(String.format("%s - Table created with %d items in %d ms",
				getItemClass().getSimpleName(), tableView.getTable().getItems().size(),
				System.currentTimeMillis() - startTime));

		return tableView;
	}

	@SuppressWarnings("unchecked")
	public ProTableView<T> createProTableView(List<TreeItem<NamedTreeItem>> treeItems) {
		ProTableView<T> tableView = new ProTableView<>(getItemClass(), createTableBase());
		ObservableList<T> items = FXCollections.observableArrayList();
		for (int i = 0; i < treeItems.size(); i++)
			try {
				T item = (T) treeItems.get(i).getValue().getValue();
				items.add(item);
			} catch (Exception e) {}

		tableView.getTable().getItems().clear();
		tableView.getTable().getItems().addAll(items);

		return tableView;
	}

//	@SuppressWarnings("unchecked")
//	public DbTableView<T> createDbTableView(List<TreeItem<NamedTreeItem>> treeItems) {
//		DbTableView<T> tableView = new DbTableView<>(getItemClass(), createDbTableBase());
//		ObservableList<T> items = FXCollections.observableArrayList();
//		for (int i = 0; i < treeItems.size(); i++)
//			try {
//				T item = (T) treeItems.get(i).getValue().getValue();
//				items.add(item);
//			} catch (Exception e) {}
//
//		tableView.getTable().getItems().clear();
//		tableView.getTable().getItems().addAll(items);
//
//		return tableView;
//	}

	public DbTreeTableView<T> createProTreeTableView() {
		DbTreeTableView<T> treeTableView = new DbTreeTableView<>(getItemClass());
		return treeTableView;
	}

	public DbTreeTableView<T> createProTreeTableView(Collection<T> items) {
		DbTreeTableView<T> treeTableView = new DbTreeTableView<>(getItemClass());
		treeTableView.setItems(items);
		return treeTableView;
	}

	public static <T, R> void createCountingColumn(Table<T> table, String name,
			Collection<AbstractColumn<T, ?>> columns,
			Function<T, ? extends Collection<R>> listGetter) {
		ColumnCreator<T, T> creator = new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn(name,
				path -> String.valueOf(listGetter.apply(path).size())));
	}

	private static SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");

	public static <T extends AbstractPersonifiedEntity<T>> void
			createPersonifiedColumns(Table<T> table, List<AbstractColumn<T, ?>> columns) {

		ColumnCreator<T, T> creator = new ColumnCreator<>(table);
		AbstractFilterColumn<T, ?> column;
		column = creator.createFilterColumn("Last modified",
				path -> format.format(path.getDateLastModified()));
		column.setItemsFilter(false);
		columns.add(column);
		columns.add(creator.createFilterColumn("Changed By", path -> {
			if (path.getChanger().equalsId(UserAdmin.getAdmin())) return "admin";
			else
				return path.getChanger().getSimpleAcademicName();
		}));
		column = creator.createFilterColumn("Added", path -> format.format(path.getDateAdded()));
		column.setItemsFilter(false);
		columns.add(column);
		columns.add(creator.createFilterColumn("Created By", path -> {
			if (path.getCreator().equalsId(UserAdmin.getAdmin())) return "admin";
			else
				return path.getCreator().getSimpleAcademicName();
		}));
	}

	public static <T extends AbstractPropertifiedEntity<T>> void createPropertyColumns(
			Table<T> table, Collection<AbstractColumn<T, ?>> columns, Collection<T> items) {
		createPropertyColumns(table, columns, items, container -> container.getBooleanProperties(),
				bool -> String.valueOf(bool), s -> Boolean.valueOf(s));
		createPropertyColumns(table, columns, items, container -> container.getDoubleProperties(),
				bool -> String.valueOf(bool), s -> Double.valueOf(s));
		createPropertyColumns(table, columns, items, container -> container.getIntegerProperties(),
				bool -> String.valueOf(bool), s -> Integer.valueOf(s));
		createPropertyColumns(table, columns, items, container -> container.getStringProperties(),
				s -> s, s -> s);

//		DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
		Function<String, Date> stringDateFunc = s -> {
			try {
				format.parse(s);
			} catch (ParseException e) {
				e.printStackTrace();
			} ;
			throw new IllegalArgumentException();
		};
		createPropertyColumns(table, columns, items, container -> container.getDateProperties(),
				date -> String.valueOf(date), stringDateFunc);
	}

	private static <T, P extends AbstractProperty<P, R>, R> void createPropertyColumns(
			Table<T> table, Collection<AbstractColumn<T, ?>> columns, Collection<T> items,
			Function<T, Collection<P>> propsGetter, Function<R, String> toStringFun,
			Function<String, R> fromStringFun) {
		Map<UUID, String> map = new HashMap<UUID, String>();
		Set<P> set = items.stream().flatMap(s -> propsGetter.apply(s).stream())
				.collect(Collectors.toSet());
		set.stream().sorted(
				(p1, p2) -> p1.getPropertyField().getId().compareTo(p2.getPropertyField().getId()))
				.forEach(
						p -> map.put(p.getPropertyField().getId(), p.getPropertyField().getName()));

		map.entrySet().stream().forEach(entry -> {
			OptionalColumnCreator<T, P> creator = new OptionalColumnCreator<>(table,
					item -> propsGetter.apply(item).stream()
							.filter(p -> p.getPropertyField().getId().equals(entry.getKey()))
							.findFirst());
			columns.add(creator.createFilterColumn(entry.getValue(),
					path -> toStringFun.apply(path.getValue()),
					(path, value) -> path.setValue(fromStringFun.apply((String) value)), ""));
		});

	}

	public EntityBeanInfo<T> getEntityBeanInfo() {
		return entityBeanInfo;
	}

}
