package org.artorg.tools.phantomData.client.scene.control.treeTableView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.Entity;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.beans.DbNode;
import org.artorg.tools.phantomData.client.beans.EntityBeanInfo;
import org.artorg.tools.phantomData.client.scene.layout.AddableToPane;
import org.artorg.tools.phantomData.client.table.TableBase;
import org.artorg.tools.phantomData.server.model.specification.AbstractPersonifiedEntity;
import org.artorg.tools.phantomData.server.model.specification.NameGeneratable;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeSortMode;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.util.Callback;

public class ProTreeTableView<ITEM extends DbPersistent<ITEM, ?>>
	extends TreeTableView<DbNode> implements AddableToPane {
	private List<DbTreeTableColumn> treeTableColumns;
	private TableBase<ITEM> table;
	private TreeItem<DbNode> root;
	private Class<?> itemClass;
	private static final int dephtLevelMax = 8;

	{
		treeTableColumns = new ArrayList<DbTreeTableColumn>();
		root = new TreeItem<DbNode>(new DbNode("Root value", "Root name", "Root type"));
	}

	public ProTreeTableView(Class<?> itemClass) {
		this.itemClass = itemClass;
	}

	public void initTable() {
		super.getColumns().clear();
		treeTableColumns = new ArrayList<DbTreeTableColumn>();
		DbTreeTableColumn column;

		column = new DbTreeTableColumn("Name");
		column.setPrefWidth(150);
		column.setCellValueFactory(param -> {
			return new ReadOnlyStringWrapper(
				((DbNode) param.getValue().getValue()).getName());
		});
		column.setPrefAutosizeWidth(180.0);
		column.setMaxAutosizeWidth(300.0);
		treeTableColumns.add(column);

		column = new DbTreeTableColumn("Value");
		column.setPrefAutosizeWidth(300.0);
		column.setMaxAutosizeWidth(600.0);
		column.setCellValueFactory(param -> {
			DbNode item = param.getValue().getValue();
			Object value = ((DbNode) item).getValue();
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
		addPersonifiedColumn("Last modified",
			item -> format.format(item.getDateLastModified()));
		addPersonifiedColumn("Changed by",
			item -> item.getChanger().getSimpleAcademicName());
		addPersonifiedColumn("Added", item -> format.format(item.getDateAdded()));
		addPersonifiedColumn("Created by",
			item -> item.getCreator().getSimpleAcademicName());

		getColumns().addAll(treeTableColumns);
		root.setExpanded(true);
		super.setRoot(root);
		setShowRoot(false);
		autoResizeColumns();
		super.setSortMode(TreeSortMode.ONLY_FIRST_LEVEL);
		super.refresh();
		super.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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

	private Callback<CellDataFeatures<DbNode, String>, ObservableValue<String>>
		createCellValueFactory(Function<AbstractPersonifiedEntity<?>, String> mapper) {
		return param -> {
			Object entity = ((DbNode) param.getValue().getValue()).getValue();
			if (entity instanceof AbstractPersonifiedEntity)
				return new ReadOnlyStringWrapper(
					mapper.apply(((AbstractPersonifiedEntity<?>) entity)));
			return new ReadOnlyStringWrapper("");
		};
	}

	public void setItems(List<ITEM> items) {
		List<TreeItem<DbNode>> treeItems = new ArrayList<TreeItem<DbNode>>();
		items.forEach(item -> {
			TreeItem<DbNode> treeItem = createTreeItem(
				new DbNode(item, item.getItemClass().getSimpleName(), "Items"), 0);
			if (treeItem != null) {
				treeItems.add(treeItem);
				addResizeColumnsExpandListener(treeItem);
			}
		});
		root.getChildren().clear();
		root.getChildren().addAll(treeItems);
	}

	private TreeItem<DbNode> createTreeItem(DbNode dbNode, int level) {
		if (level > dephtLevelMax) return null;
		TreeItem<DbNode> rootItem = new TreeItem<>(dbNode);
		rootItem.getChildren().addAll(createTreeItems(dbNode, level));
		return rootItem;
	}

	@SuppressWarnings("unchecked")
	private List<TreeItem<DbNode>> createTreeItems(DbNode dbNode, int level) {
		if (level > dephtLevelMax) return new ArrayList<>();

		Object value = dbNode.getValue();
		if (isEntity(value)) return createBeanTreeItems(value, level);
		if (value instanceof List) {
			List<TreeItem<DbNode>> treeItems = new ArrayList<>();
			List<Object> list = (List<Object>) value;
			for (int i = 0; i < list.size(); i++) {
				TreeItem<DbNode> treeItem = createTreeItem(
					new DbNode(list.get(i), "[" + i + "]", "Collection"), level + 1);
				if (treeItem != null) treeItems.add(treeItem);
			}
			return treeItems;
		}
		return new ArrayList<>();
	}

	private boolean isEntity(Object o) {
		return o.getClass().isAnnotationPresent(Entity.class);
	}

	private List<TreeItem<DbNode>> createBeanTreeItems(Object bean, int level) {
		if (level > dephtLevelMax) return new ArrayList<>();

		if (bean == null) throw new NullPointerException();

		List<TreeItem<DbNode>> treeItems = new ArrayList<>();
		EntityBeanInfo beanInfo = Main.getBeaninfos().getEntityBeanInfo(bean.getClass());

		treeItems.addAll(createEntityTreeItem(bean, beanInfo, level));
		treeItems.addAll(createCollectionTreeItem(bean, beanInfo, level));
		if (bean instanceof AbstractPersonifiedEntity) {
			DbNode dbNode =
				new DbNode(((AbstractPersonifiedEntity<?>) bean).getId(), "id", "ID");
			TreeItem<DbNode> treeItem = createTreeItem(dbNode, level + 1);
			if (treeItem != null) treeItems.add(treeItem);
		}
		treeItems.addAll(createPropertiesTreeItems(bean, beanInfo, level));

		return treeItems;
	}

	private List<TreeItem<DbNode>> createEntityTreeItem(Object bean,
		EntityBeanInfo beanInfo, int level) {
		if (level > dephtLevelMax) return new ArrayList<>();
		return beanInfo.getNamedEntityValuesAsStream(bean).map(dbNode -> {
			TreeItem<DbNode> node = new TreeItem<>(dbNode);
			node.getChildren().addAll(createBeanTreeItems(dbNode.getValue(), level + 1));
			return node;
		}).collect(Collectors.toList());
	}

	private List<TreeItem<DbNode>> createCollectionTreeItem(Object bean,
		EntityBeanInfo beanInfo, int level) {
		if (level > dephtLevelMax) return new ArrayList<>();
		return beanInfo.getNamedCollectionValuesAsStream(bean).map(dbNode -> {
			return createTreeItem(dbNode, level + 1);
		}).filter(treeItem -> treeItem != null).collect(Collectors.toList());
	}

	private List<TreeItem<DbNode>> createPropertiesTreeItems(Object bean,
		EntityBeanInfo beanInfo, int level) {
		if (level > dephtLevelMax) return new ArrayList<>();
		return beanInfo.getNamedPropertiesValueAsStream(bean)
			.filter(namedValue -> namedValue.getValue().getClass() != Class.class)
			.map(o -> new TreeItem<>((DbNode) o)).collect(Collectors.toList());
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
		super.setColumnResizePolicy(
			javafx.scene.control.TreeTableView.UNCONSTRAINED_RESIZE_POLICY);
		treeTableColumns.stream()
			.forEach(column -> column.autoResizeWidth(getRoot().getChildren()));
	}

	public void setTable(TableBase<ITEM> table) {
		this.table = table;
		initTable();
		setItems(table.getItems());
	}

	public TableBase<ITEM> getTable() {
		return table;
	}

	public javafx.scene.control.TreeTableView<?> getGraphic() {
		return this;
	}

	public Class<?> getItemClass() {
		return itemClass;
	}

}
