package org.artorg.tools.phantomData.client.scene.control.treeTableView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.Entity;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.beans.NamedTreeItem;
import org.artorg.tools.phantomData.client.beans.EntityBeanInfo;
import org.artorg.tools.phantomData.client.scene.control.DbEntityView;
import org.artorg.tools.phantomData.client.scene.layout.AddableToPane;
import org.artorg.tools.phantomData.client.table.DbTable;
import org.artorg.tools.phantomData.server.model.AbstractPersonifiedEntity;
import org.artorg.tools.phantomData.server.model.DbPersistent;
import org.artorg.tools.phantomData.server.model.NameGeneratable;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeSortMode;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.util.Callback;

public class DbTreeTableView<T> extends TreeTableView<NamedTreeItem>
		implements AddableToPane, DbEntityView {
	private List<DbTreeTableColumn> treeTableColumns;
	private final DbTable<T> table;
	private TreeItem<NamedTreeItem> root;
	private Class<T> itemClass;
	private static final int dephtLevelMax = 8;

	{
		treeTableColumns = new ArrayList<DbTreeTableColumn>();
		root = new TreeItem<NamedTreeItem>(
				new NamedTreeItem("Root value", "Root name", "Root type"));
	}

	public DbTreeTableView(Class<T> itemClass) {
		this(itemClass, Main.getUIEntity(itemClass).createDbTableBase());
	}

	protected DbTreeTableView(Class<T> itemClass, DbTable<T> table) {
		this.itemClass = itemClass;
		this.table = table;

		super.getColumns().clear();
		treeTableColumns = new ArrayList<DbTreeTableColumn>();
		DbTreeTableColumn column;

		column = new DbTreeTableColumn("Name");
		column.setPrefWidth(150);
		column.setCellValueFactory(param -> {
			return new ReadOnlyStringWrapper(
					((NamedTreeItem) param.getValue().getValue()).getName());
		});
		column.setPrefAutosizeWidth(180.0);
		column.setMaxAutosizeWidth(300.0);
		treeTableColumns.add(column);

		column = new DbTreeTableColumn("Value");
		column.setPrefAutosizeWidth(300.0);
		column.setMaxAutosizeWidth(600.0);
		column.setCellValueFactory(param -> {
			NamedTreeItem item = param.getValue().getValue();
			Object value = ((NamedTreeItem) item).getValue();
			if (value instanceof NameGeneratable)
				return new ReadOnlyStringWrapper(((NameGeneratable) value).toName());
			if (value instanceof List) {
				List<?> list = (List<?>) value;
				if (list.isEmpty()) return new ReadOnlyStringWrapper("");
				if (list.get(0) == null) return new ReadOnlyStringWrapper("null");
				return new ReadOnlyStringWrapper("size: " + list.size());
			}

			return new ReadOnlyStringWrapper(value.toString());
		});
		treeTableColumns.add(column);

		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		addPersonifiedColumn("Last modified", item -> format.format(item.getDateLastModified()));
		addPersonifiedColumn("Changed by", item -> item.getChanger().getSimpleAcademicName());
		addPersonifiedColumn("Added", item -> format.format(item.getDateAdded()));
		addPersonifiedColumn("Created by", item -> item.getCreator().getSimpleAcademicName());

		getColumns().addAll(treeTableColumns);
		root.setExpanded(true);
		super.setRoot(root);
		setShowRoot(false);
		autoResizeColumns();
		super.setSortMode(TreeSortMode.ONLY_FIRST_LEVEL);
		super.refresh();
		super.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		setItems(getTable().getItems());
	}

	public void reload() {
		getTable().reload();
		setItems(getTable().getItems());
		refresh();
	}

	private void addPersonifiedColumn(String name,
			Function<AbstractPersonifiedEntity<?>, String> mapper) {
		treeTableColumns.add(createBaseColumn(name, mapper));
	}

	private DbTreeTableColumn createBaseColumn(String name,
			Function<AbstractPersonifiedEntity<?>, String> mapper) {
		DbTreeTableColumn column = new DbTreeTableColumn(name);
		column.setCellValueFactory(createCellValueFactory(mapper));
		return column;
	}

	private Callback<CellDataFeatures<NamedTreeItem, String>, ObservableValue<String>>
			createCellValueFactory(Function<AbstractPersonifiedEntity<?>, String> mapper) {
		return param -> {
			Object entity = ((NamedTreeItem) param.getValue().getValue()).getValue();
			if (entity instanceof AbstractPersonifiedEntity) return new ReadOnlyStringWrapper(
					mapper.apply(((AbstractPersonifiedEntity<?>) entity)));
			return new ReadOnlyStringWrapper("");
		};
	}

	public void setItem(T item) {
		List<TreeItem<NamedTreeItem>> treeItems = new ArrayList<TreeItem<NamedTreeItem>>();
		TreeItem<NamedTreeItem> treeItem = createTreeItem(new NamedTreeItem(item,
				((DbPersistent<?, ?>) item).getItemClass().getSimpleName(), "Items"), 0);
		if (treeItem != null) treeItems.addAll(treeItem.getChildren());

		root.getChildren().clear();
		root.getChildren().addAll(treeItems);
	}

	public void setItems(Collection<T> items) {
		List<TreeItem<NamedTreeItem>> treeItems = new ArrayList<TreeItem<NamedTreeItem>>();
		items.forEach(item -> {
			TreeItem<NamedTreeItem> treeItem = createTreeItem(
					new NamedTreeItem(item,
							((DbPersistent<?, ?>) item).getItemClass().getSimpleName(), "Items"),
					0);
			if (treeItem != null) {
				treeItems.add(treeItem);
				addResizeColumnsExpandListener(treeItem);
			}
		});
		root.getChildren().clear();
		root.getChildren().addAll(treeItems);
	}

	private TreeItem<NamedTreeItem> createTreeItem(NamedTreeItem dbNode, int level) {
		if (level > dephtLevelMax) return null;
		TreeItem<NamedTreeItem> rootItem = new TreeItem<>(dbNode);
		rootItem.getChildren().addAll(createTreeItems(dbNode, level));
		return rootItem;
	}

	@SuppressWarnings("unchecked")
	private List<TreeItem<NamedTreeItem>> createTreeItems(NamedTreeItem dbNode, int level) {
		if (level > dephtLevelMax) return new ArrayList<>();

		Object value = dbNode.getValue();
		if (isEntity(value)) return createBeanTreeItems(value, level);
		if (value instanceof List) {
			List<TreeItem<NamedTreeItem>> treeItems = new ArrayList<>();
			List<Object> list = (List<Object>) value;
			for (int i = 0; i < list.size(); i++) {
				TreeItem<NamedTreeItem> treeItem = createTreeItem(
						new NamedTreeItem(list.get(i), "[" + i + "]", "Collection"), level + 1);
				if (treeItem != null) treeItems.add(treeItem);
			}
			return treeItems;
		}
		return new ArrayList<>();
	}

	private boolean isEntity(Object o) {
		return o.getClass().isAnnotationPresent(Entity.class);
	}

	@SuppressWarnings("unchecked")
	private <U> List<TreeItem<NamedTreeItem>> createBeanTreeItems(U bean, int level) {
		if (level > dephtLevelMax) return new ArrayList<>();

		if (bean == null) throw new NullPointerException();

		List<TreeItem<NamedTreeItem>> treeItems = new ArrayList<>();
		EntityBeanInfo<U> beanInfo = null;
		try {
			beanInfo = Main.getUIEntity((Class<U>) bean.getClass()).getEntityBeanInfo();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		treeItems.addAll(createEntityTreeItem(bean, beanInfo, level));
		treeItems.addAll(createCollectionTreeItem(bean, beanInfo, level));
		if (bean instanceof AbstractPersonifiedEntity) {
			NamedTreeItem dbNode =
					new NamedTreeItem(((AbstractPersonifiedEntity<?>) bean).getId(), "id", "ID");
			TreeItem<NamedTreeItem> treeItem = createTreeItem(dbNode, level + 1);
			if (treeItem != null) treeItems.add(treeItem);
		}
		treeItems.addAll(createPropertiesTreeItems(bean, beanInfo, level));

		return treeItems;
	}

	private <U> List<TreeItem<NamedTreeItem>> createEntityTreeItem(U bean,
			EntityBeanInfo<U> beanInfo, int level) {
		if (level > dephtLevelMax) return new ArrayList<>();
		return beanInfo.getNamedEntityValuesAsStream(bean).map(dbNode -> {
			TreeItem<NamedTreeItem> node = new TreeItem<>(dbNode);
			node.getChildren().addAll(createBeanTreeItems(dbNode.getValue(), level + 1));
			return node;
		}).collect(Collectors.toList());
	}

	private <U> List<TreeItem<NamedTreeItem>> createCollectionTreeItem(U bean,
			EntityBeanInfo<U> beanInfo, int level) {
		if (level > dephtLevelMax) return new ArrayList<>();
		return beanInfo.getNamedEntityCollectionValuesAsStream(bean).map(dbNode -> {
			return createTreeItem(dbNode, level + 1);
		}).filter(treeItem -> treeItem != null).collect(Collectors.toList());
	}

	private <U> List<TreeItem<NamedTreeItem>> createPropertiesTreeItems(U bean,
			EntityBeanInfo<U> beanInfo, int level) {
		if (level > dephtLevelMax) return new ArrayList<>();
		return beanInfo.getNamedPropertiesValueAsStream(bean)
				.filter(namedValue -> namedValue.getValue().getClass() != Class.class)
				.map(o -> new TreeItem<>((NamedTreeItem) o)).collect(Collectors.toList());
	}

	private void addResizeColumnsExpandListener(TreeItem<?> treeItem) {
		treeItem.expandedProperty()
				.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
					if (newValue) autoResizeColumns();
				});
		if (!treeItem.isLeaf()) treeItem.getChildren().stream()
				.forEach(subTreeItem -> addResizeColumnsExpandListener(subTreeItem));
	}

	public void autoResizeColumns() {
		super.setColumnResizePolicy(javafx.scene.control.TreeTableView.UNCONSTRAINED_RESIZE_POLICY);
		treeTableColumns.stream()
				.forEach(column -> column.autoResizeWidth(getRoot().getChildren()));
	}

	public DbTable<T> getTable() {
		return table;
	}

	public Class<T> getItemClass() {
		return itemClass;
	}

	@Override
	public Collection<Object> getSelectedItems() {
		return getSelectionModel().getSelectedItems().stream().map(treeItem -> {
			if (treeItem == null) return null;
			NamedTreeItem namedTreeItem = treeItem.getValue();
			if (namedTreeItem == null) return null;
			return namedTreeItem.getValue();
		}).filter(value -> value != null)
				.collect(Collectors.toCollection(() -> new ArrayList<Object>()));
	}

	@Override
	public Node getNode() {
		return this;
	}

}
