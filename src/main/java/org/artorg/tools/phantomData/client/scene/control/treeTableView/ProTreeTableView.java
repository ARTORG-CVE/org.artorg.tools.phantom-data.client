package org.artorg.tools.phantomData.client.scene.control.treeTableView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

public class ProTreeTableView<ITEM extends DbPersistent<ITEM, ?>>
	extends TreeTableView<DbNode> implements AddableToAnchorPane {
	private Class<?> itemClass;
	private TableBase<ITEM> table;
	private TreeItem<DbNode> root = new TreeItem<DbNode>(new DbNode("Root node", "Root node"));
	private List<DbTreeTableColumn> treeTableColumns;

	{
		treeTableColumns = new ArrayList<DbTreeTableColumn>();
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
		column.setCellValueFactory(
			(TreeTableColumn.CellDataFeatures<DbNode, String> param) -> {
				Object item = param.getValue().getValue();
				if (item == null) return new ReadOnlyStringWrapper("null");

				if (item instanceof DbNode) {
					String name = ((DbNode) item).getName();
					return new ReadOnlyStringWrapper(name);
				}

				if (item instanceof List) {
					List<?> list = ((List<?>) item);
					if (list.isEmpty()) throw new IllegalArgumentException();
					return new ReadOnlyStringWrapper(
						list.get(0).getClass().getSimpleName());
				}

				return new ReadOnlyStringWrapper(item.getClass().getSimpleName());
			});
		column.setPrefAutosizeWidth(180.0);
		column.setMaxAutosizeWidth(300.0);
		treeTableColumns.add(column);

		column = new DbTreeTableColumn("Value", item -> item.toName(),
			item -> item.toString());
		column.setPrefAutosizeWidth(300.0);
		column.setMaxAutosizeWidth(600.0);
		column.setCellValueFactory(
			(TreeTableColumn.CellDataFeatures<DbNode, String> param) -> {
				Object item = param.getValue().getValue();
				if (item instanceof DbNode) {
					Object value = ((DbNode) item).getValue();

					
					
					if (value instanceof AbstractBaseEntity) {
						try {
							return new ReadOnlyStringWrapper(
								((AbstractBaseEntity<?>) value).toName());
						} catch (NullPointerException e) {}
						return new ReadOnlyStringWrapper("null");

					}
					return new ReadOnlyStringWrapper(value.toString());
				}

				return new ReadOnlyStringWrapper("");
			});
		treeTableColumns.add(column);

		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		column = new DbTreeTableColumn("Last modified",
			item -> format.format(item.getDateLastModified()));
		treeTableColumns.add(column);

		column = new DbTreeTableColumn("Changed by",
			item -> item.getChanger().getSimpleAcademicName());
		treeTableColumns.add(column);

		column =
			new DbTreeTableColumn("Added", item -> format.format(item.getDateAdded()));
		treeTableColumns.add(column);

		column = new DbTreeTableColumn("Creator",
			item -> item.getCreator().getSimpleAcademicName());
		treeTableColumns.add(column);

		getColumns().addAll(treeTableColumns);

		root.setExpanded(true);
		super.setRoot(root);
		setShowRoot(false);
		autoResizeColumns();

		super.setSortMode(TreeSortMode.ONLY_FIRST_LEVEL);
		super.refresh();

		super.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

	}

	public void setItems(List<ITEM> items) {
		List<TreeItem<DbNode>> treeItems = new ArrayList<TreeItem<DbNode>>();
		items.forEach(item -> {
			TreeItem<DbNode> node = new TreeItem<DbNode>(new DbNode(item, "test"));
			node.getChildren().addAll(createTreeItems(item, 0));
			treeItems.add(node);
		});

		treeItems.stream().forEach(treeItem -> addResizeColumnsExpandListener(treeItem));

		root.getChildren().clear();
		root.getChildren().addAll(treeItems);
	}

	private void addResizeColumnsExpandListener(TreeItem<?> treeItem) {
		treeItem.expandedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
				Boolean oldValue, Boolean newValue) {
				if (newValue) autoResizeColumns();
			}
		});
		if (!treeItem.isLeaf()) treeItem.getChildren().stream()
			.forEach(subTreeItem -> addResizeColumnsExpandListener(subTreeItem));
	}
	
	
	
	
	
	private TreeItem<DbNode> createTreeItem(Object o, String name) {
		TreeItem<DbNode> root = new TreeItem<DbNode>(new DbNode(o,name));
		if (!isEntity(o)) {
			if (isCollection(o)) {
				List<Object> list = ((Collection<?>)o).stream().collect(Collectors.toList());
				for (int i=0; i<list.size(); i++) {
					root.getChildren().add(createTreeItem(list.get(i), "[" +i +"]"));
				}
			}
		} else {
			List<TreeItem<DbNode>> children = createTreeItems(o, 0);
//			root.getChildren().addAll(children);
			
		}
		
		return root;
		
	}
	
	private boolean isEntity(Object o) {
		return !o.getClass().isAnnotationPresent(Entity.class);
	}
	
	private boolean isCollection(Object o) {
		return o instanceof Collection;
	}

	private List<TreeItem<DbNode>> createTreeItems(Object bean, int level) {
		if (level > 20) return new ArrayList<TreeItem<DbNode>>();

		if (bean == null)
			throw new NullPointerException();
		
		List<TreeItem<DbNode>> treeItems = new ArrayList<TreeItem<DbNode>>();
		EntityBeanInfo beanInfo = Main.getBeaninfos().getEntityBeanInfo(bean.getClass());

		// adds entities - every entity has children if they are not null or empty
		List<DbNode> entities = beanInfo.getNamedEntityValues(bean);
		entities = entities.stream().filter(entity -> entity.getValue() != null)
			.collect(Collectors.toList());
		List<TreeItem<DbNode>> entityTreeItems =
			entities.stream().filter(entity -> entity.getValue() != null).map(entity -> {
				TreeItem<DbNode> node = new TreeItem<>(entity);
				node.getChildren().addAll(createTreeItems(entity.getValue(), level + 1));
				return node;
			}).collect(Collectors.toList());
		treeItems.addAll(entityTreeItems);
		
		
		
//		List<DbProperty> entityCollections = beanInfo.getNamedCollectionValues(bean);
//			Main.getBeaninfos().getEntityCollections(bean);
//			List<TreeItem<Object>> entityCollectionTreeItems =
//				entityCollections.stream().filter(entity -> entity.getValue() != null).map(entity -> {
//					TreeItem<Object> node = new TreeItem<>(entity);
//					node.getChildren().addAll(createCollectionTreeItems((Collection<?>) entity.getValue(), level + 1));
//					return node;
//				}).collect(Collectors.toList());
//			treeItems.addAll(entityCollectionTreeItems);
			
			
//		List<TreeItem<Object>> entityCollectionTreeItems =
//			entityCollections.stream().filter(entity -> entity != null).map(entity -> {
//				TreeItem<Object> node = new TreeItem<>(entity);
//				List<TreeItem<Object>> childrenNodes = entity.stream()
//					.flatMap(subEntity -> createTreeItems(subEntity, level + 1).stream())
//					.collect(Collectors.toList());
//				node.getChildren().addAll(childrenNodes);
//				return node;
//			}).collect(Collectors.toList());
//		treeItems.addAll(entityCollectionTreeItems);
		
		
//		List<Collection<Object>> entityCollections =
//			Main.getBeaninfos().getEntityCollections(bean);
//		entityCollections = entityCollections.stream()
//			.collect(Collectors.toList());
//		List<TreeItem<Object>> entityCollectionTreeItems =
//			entityCollections.stream().filter(entity -> entity != null).map(entity -> {
//				TreeItem<Object> node = new TreeItem<>(entity);
//				List<TreeItem<Object>> childrenNodes = entity.stream()
//					.flatMap(subEntity -> createTreeItems(subEntity, level + 1).stream())
//					.collect(Collectors.toList());
//				node.getChildren().addAll(childrenNodes);
//				return node;
//			}).collect(Collectors.toList());
//		treeItems.addAll(entityCollectionTreeItems);

		List<DbNode> properties = beanInfo.getNamedPropertiesValues(bean);
		properties = properties.stream()
			.filter(
				namedValue -> namedValue.getValue().getClass() != Class.class)
			.collect(Collectors.toList());
		List<TreeItem<DbNode>> propertyTreeItems = properties.stream()
			.map(o -> new TreeItem<>((DbNode) o)).collect(Collectors.toList());
		treeItems.addAll(propertyTreeItems);

		return treeItems;
	}
	
//	private List<TreeItem<Object>> createCollectionTreeItems(Collection<?> collection, int level) {
//		if (level > 20) return new ArrayList<TreeItem<Object>>();
//		
//		if (collection == null)
//			throw new NullPointerException();
//		
//		List<TreeItem<Object>> treeItems = new ArrayList<TreeItem<Object>>();
//		
//		
//		
//		collection.stream().(e -> {
//			if (EntityBeanInfo.isEntity(e))
//				
//			});
//		
//		
//	}

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
