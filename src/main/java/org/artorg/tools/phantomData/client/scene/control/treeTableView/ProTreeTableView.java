package org.artorg.tools.phantomData.client.scene.control.treeTableView;

import java.beans.PropertyDescriptor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.scene.layout.AddableToAnchorPane;
import org.artorg.tools.phantomData.client.table.TableBase;
import org.artorg.tools.phantomData.server.beans.EntityBeanInfo;
import org.artorg.tools.phantomData.server.beans.DbProperty;
import org.artorg.tools.phantomData.server.model.person.Person;
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
	extends TreeTableView<Object> implements AddableToAnchorPane {
	private Class<?> itemClass;
	private TableBase<ITEM> table;
	private TreeItem<Object> root = new TreeItem<Object>("Root node");
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
			(TreeTableColumn.CellDataFeatures<Object, String> param) -> {
				Object item = param.getValue().getValue();
				if (item == null) return new ReadOnlyStringWrapper("null");

				if (item instanceof PropertyDescriptor) {
					String name = ((PropertyDescriptor) item).getName();
					return new ReadOnlyStringWrapper(name);
				}

				if (item instanceof DbProperty) {
					String name = ((DbProperty) item).getName();
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

		column =
			new DbTreeTableColumn("Value", item -> item.toName(), item -> item.toString());
		column.setPrefAutosizeWidth(300.0);
		column.setMaxAutosizeWidth(600.0);
		column.setCellValueFactory(
			(TreeTableColumn.CellDataFeatures<Object, String> param) -> {
				Object item = param.getValue().getValue();
				if (item instanceof DbProperty) {
					Object value = ((DbProperty) item).getValue();

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
		List<TreeItem<Object>> treeItems = new ArrayList<TreeItem<Object>>();
		items.forEach(item -> {
			TreeItem<Object> node = new TreeItem<>(item);
			node.getChildren().addAll(createTreeItems(item, 0));
			treeItems.add(node);
		});

		treeItems.stream().forEach(treeItem -> addResizeColumnsExpandListener(treeItem));

		root.getChildren().clear();
		root.getChildren().addAll(treeItems);
	}

	private void addResizeColumnsExpandListener(TreeItem<Object> treeItem) {
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
	
	private List<TreeItem<Object>> createTreeItems(Object bean, int level) {
		if (level > 20) return new ArrayList<TreeItem<Object>>();

		List<Object> entities = Main.getBeaninfos().getEntities(bean);
		entities = entities.stream().filter(entity -> entity != bean)
			.filter(entity -> !(entity instanceof Person)).collect(Collectors.toList());
		List<TreeItem<Object>> entityTreeItems =
			entities.stream().filter(entity -> entity != null).map(entity -> {
				TreeItem<Object> node = new TreeItem<>(entity);
				node.getChildren().addAll(createTreeItems(entity, level + 1));
				return node;
			}).collect(Collectors.toList());

		List<Collection<Object>> entityCollections =
			Main.getBeaninfos().getEntityCollections(bean);
		entityCollections = entityCollections.stream().filter(entity -> entity != bean)
			.filter(entity -> !(entity instanceof Person)).collect(Collectors.toList());
		List<TreeItem<Object>> entityCollectionTreeItems =
			entityCollections.stream().filter(entity -> entity != null).map(entity -> {
				TreeItem<Object> node = new TreeItem<>(entity);
				List<TreeItem<Object>> childrenNodes = entity.stream()
					.flatMap(subEntity -> createTreeItems(subEntity, level + 1).stream())
					.collect(Collectors.toList());
				node.getChildren().addAll(childrenNodes);
				return node;
			}).collect(Collectors.toList());

		EntityBeanInfo beanInfo = Main.getBeaninfos().getEntityBeanInfo(bean.getClass());
		List<DbProperty> namedValues = beanInfo.getNamedPropertiesValues(bean);

		namedValues = namedValues.stream()
			.filter(
				namedValue -> namedValue.getDescriptor().getPropertyType() != Class.class)
			.collect(Collectors.toList());

//		
//		List<PropertyDescriptor> descriptors = beanInfo.getNotBasePropertyDescriptors();
//		
//		
//		descriptors = descriptors.stream()
//		
//		.filter(p -> p.getPropertyType() != Class.class)
////		.filter(p -> {
////		if (p instanceof Collection) {
////			Collection<?> coll = (Collection) p;
////			if (coll.isEmpty())
////				return false;
////		}
////		return true;
////		})
//		.collect(Collectors.toList());
//		
//		
////		List<Object> properties = Main.getBeaninfos().getProperties(bean);
//		List<NamedValue> namedValues = new ArrayList<NamedValue>();
//		
//		for (int i=0; i<descriptors.size(); i++) {
//			try {
//			NamedValue namedValue = new NamedValue(descriptors.get(i), bean); 
//			namedValues.add(namedValue);
//			} catch (Exception e) {
////				e.printStackTrace();
//			}
//		}

		List<TreeItem<Object>> propertyTreeItems =
			namedValues.stream().map(namedValue -> (Object) namedValue)
				.map(o -> new TreeItem<>(o)).collect(Collectors.toList());

//		descriptors.get(0).getPropertyType()

//		List<Object> properties = descriptors.stream()

//			.filter(p -> p.getPropertyType() != Class.class)
//			.filter(p -> {
//			if (p instanceof Collection) {
//				Collection<?> coll = (Collection) p;
//				if (coll.isEmpty())
//					return false;
//			}
//			return true;
//			})
//			.collect(Collectors.toList());
//		List<TreeItem<Object>> propertyTreeItems = properties.stream()
//			.map(o -> new TreeItem<>(o))
//			.collect(Collectors.toList());

//		List<Object> properties = Main.getBeaninfos().getProperties(bean);
//			properties = properties.stream().filter(p -> !(p instanceof Class))
//			.filter(p -> {
//				if (p instanceof Collection) {
//					Collection<?> coll = (Collection) p;
//					if (coll.isEmpty())
//						return false;
//				}
//				return true;
//			}).sorted((p1, p2) -> {
//				if (UUID.class.isAssignableFrom(p1.getClass()))
//					return -1;
//				return 0;
//			}).collect(Collectors.toList());
//		List<TreeItem<Object>> propertyTreeItems = properties.stream()
//			.map(o -> new TreeItem<>(o))
//			.collect(Collectors.toList());

		entityTreeItems.addAll(entityCollectionTreeItems);
		entityTreeItems.addAll(propertyTreeItems);

		return entityTreeItems;
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
