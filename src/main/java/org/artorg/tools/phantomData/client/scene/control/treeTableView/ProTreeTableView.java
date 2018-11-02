package org.artorg.tools.phantomData.client.scene.control.treeTableView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.Entity;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.scene.layout.AddableToAnchorPane;
import org.artorg.tools.phantomData.client.table.TableBase;
import org.artorg.tools.phantomData.server.beans.DbNode;
import org.artorg.tools.phantomData.server.beans.EntityBeanInfo;
import org.artorg.tools.phantomData.server.model.specification.AbstractBaseEntity;
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
	extends TreeTableView<DbNode> implements AddableToAnchorPane {
	private List<DbTreeTableColumn> treeTableColumns;
	private TableBase<ITEM> table;
	private TreeItem<DbNode> root;
	private Class<?> itemClass;

	{
		treeTableColumns = new ArrayList<DbTreeTableColumn>();
		root = new TreeItem<DbNode>(new DbNode("Root value", "Root name"));
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
			if (value instanceof AbstractBaseEntity) return new ReadOnlyStringWrapper(
				((AbstractBaseEntity<?>) value).toName());
			if (value instanceof List) {
				List<?> list = (List<?>) value; 
				if (list.isEmpty()) return new ReadOnlyStringWrapper("");
				if (list.get(0) == null) return new ReadOnlyStringWrapper("null");
				return new ReadOnlyStringWrapper("size: " +list.size());
			}
			
			return new ReadOnlyStringWrapper(value.toString());
		});
		treeTableColumns.add(column);

		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		addBaseColumn("Last modified", item -> format.format(item.getDateLastModified()));
		addBaseColumn("Changed by", item -> item.getChanger().getSimpleAcademicName());
		addBaseColumn("Added", item -> format.format(item.getDateAdded()));
		addBaseColumn("Created by", item -> item.getCreator().getSimpleAcademicName());

		getColumns().addAll(treeTableColumns);
		root.setExpanded(true);
		super.setRoot(root);
		setShowRoot(false);
		autoResizeColumns();
		super.setSortMode(TreeSortMode.ONLY_FIRST_LEVEL);
		super.refresh();
		super.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	private void addBaseColumn(String name,
		Function<AbstractBaseEntity<?>, String> mapper) {
		treeTableColumns.add(createBaseColumn(name, mapper));
	}

	private DbTreeTableColumn createBaseColumn(String name,
		Function<AbstractBaseEntity<?>, String> mapper) {
		DbTreeTableColumn column = new DbTreeTableColumn(name);
		column.setCellValueFactory(createCellValueFactory(mapper));
		return column;
	}

	private Callback<CellDataFeatures<DbNode, String>, ObservableValue<String>>
		createCellValueFactory(Function<AbstractBaseEntity<?>, String> mapper) {
		return param -> {
			Object entity = ((DbNode) param.getValue().getValue()).getValue();
			if (entity instanceof AbstractBaseEntity) return new ReadOnlyStringWrapper(
				mapper.apply(((AbstractBaseEntity<?>) entity)));
			return new ReadOnlyStringWrapper("");
		};
	}

	public void setItems(List<ITEM> items) {
		List<TreeItem<DbNode>> treeItems = new ArrayList<TreeItem<DbNode>>();
		items.forEach(item -> {
			TreeItem<DbNode> treeItem =
				createTreeItem(item, item.getItemClass().getSimpleName());
			treeItems.add(treeItem);
			addResizeColumnsExpandListener(treeItem);
		});
		root.getChildren().clear();
		root.getChildren().addAll(treeItems);
	}
	
	private TreeItem<DbNode> createTreeItem(Object o, String name) {
		DbNode dbNode = new DbNode(o,name);
		TreeItem<DbNode> rootItem = new TreeItem<>(dbNode);
		rootItem.getChildren().addAll(createTreeItems(o,name));
		return rootItem;
	}
	
	@SuppressWarnings("unchecked")
	private List<TreeItem<DbNode>> createTreeItems(Object o, String name) {
		if (isEntity(o)) return createBeanTreeItems(o, 0);
		if (o instanceof List) {
			List<TreeItem<DbNode>> treeItems = new ArrayList<>();
			List<Object> list = (List<Object>)o;
			for (int i = 0; i < list.size(); i++)
				treeItems.add(createTreeItem(list.get(i), "[" + i + "]"));
			return treeItems;
		}
		return new ArrayList<>();
	}

	private boolean isEntity(Object o) {
		return o.getClass().isAnnotationPresent(Entity.class);
	}

	private List<TreeItem<DbNode>> createBeanTreeItems(Object bean, int level) {
		if (level > 20) return new ArrayList<>();
		if (bean == null) throw new NullPointerException();

		List<TreeItem<DbNode>> treeItems = new ArrayList<>();
		EntityBeanInfo beanInfo = Main.getBeaninfos().getEntityBeanInfo(bean.getClass());

		treeItems.addAll(createEntityTreeItem(bean, beanInfo, level+1));
		treeItems.addAll(createCollectionTreeItem(bean, beanInfo, level+1));
		if (bean instanceof AbstractBaseEntity)
			treeItems.add(createTreeItem(((AbstractBaseEntity<?>) bean).getId(), "id"));
		treeItems.addAll(createPropertiesTreeItems(bean, beanInfo));

		return treeItems;
	}

	private List<TreeItem<DbNode>> createEntityTreeItem(Object bean,
		EntityBeanInfo beanInfo, int level) {
		return beanInfo.getNamedEntityValuesAsStream(bean).map(dbNode -> {
			TreeItem<DbNode> node = new TreeItem<>(dbNode);
			node.getChildren().addAll(createBeanTreeItems(dbNode.getValue(), level + 1));
			return node;
		}).collect(Collectors.toList());
	}

	private List<TreeItem<DbNode>> createCollectionTreeItem(Object bean,
		EntityBeanInfo beanInfo, int level) {
		return beanInfo.getNamedCollectionValuesAsStream(bean).map(dbNode -> {
			return createTreeItem(dbNode.getValue(), dbNode.getName());
		}).collect(Collectors.toList());
	}

	private List<TreeItem<DbNode>> createPropertiesTreeItems(Object bean,
		EntityBeanInfo beanInfo) {
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
