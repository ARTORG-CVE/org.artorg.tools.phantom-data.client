package org.artorg.tools.phantomData.client.scene.control.treeTableView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.scene.layout.AddableToAnchorPane;
import org.artorg.tools.phantomData.client.table.TableBase;
import org.artorg.tools.phantomData.server.model.Person;
import org.artorg.tools.phantomData.server.model.specification.AbstractBaseEntity;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeSortMode;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

public class ProTreeTableView<ITEM extends DbPersistent<ITEM,?>> extends TreeTableView<Object>
	implements AddableToAnchorPane {
	private Class<?> itemClass;
	private TableBase<ITEM> table;
	private TreeItem<Object> root = new TreeItem<Object>("Root node");

	public ProTreeTableView(Class<?> itemClass) {
		this.itemClass = itemClass;
	}

	public void initTable() {
		TreeTableColumn<Object, String> column;

		column = new TreeTableColumn<>("Item type");
		column.setPrefWidth(150);
		column.setCellValueFactory(
			(TreeTableColumn.CellDataFeatures<Object, String> param) -> {
				Object item = param.getValue().getValue();
				if (item == null)
					return new ReadOnlyStringWrapper("null");
				return new ReadOnlyStringWrapper(
					param.getValue().getValue().getClass()
						.getSimpleName());
			});
		getColumns().add(column);

		column = createColumn("Name", item -> item.createName(),
			item -> item.toString());
		getColumns().add(column);

		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		column = createColumn("Last modified",
			item -> format.format(item.getDateLastModified()));
		getColumns().add(column);

		column = createColumn("Changed by",
			item -> item.getChanger().getSimpleAcademicName());
		getColumns().add(column);

		column = createColumn("Added",
			item -> format.format(item.getDateAdded()));
		getColumns().add(column);

		column = createColumn("Creator",
			item -> item.getCreator().getSimpleAcademicName());
		getColumns().add(column);

		root.setExpanded(true);
		super.setRoot(root);
		setPrefWidth(152);
		setShowRoot(false);

		super.setSortMode(TreeSortMode.ONLY_FIRST_LEVEL);
		super.refresh();
		super.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

	}

	private TreeTableColumn<Object, String> createColumn(String columnName,
		Function<AbstractBaseEntity<?>, String> mapper) {
		return createColumn(columnName, mapper, item -> "");
	}

	private TreeTableColumn<Object, String> createColumn(String columnName,
		Function<AbstractBaseEntity<?>, String> mapper,
		Function<Object, String> orMapper) {
		TreeTableColumn<Object, String> column = new TreeTableColumn<>(
			columnName);
		column.setPrefWidth(400);
		column.setCellValueFactory(
			(TreeTableColumn.CellDataFeatures<Object, String> param) -> {
				Object item = param.getValue().getValue();
				if (item == null)
					return new ReadOnlyStringWrapper("null");
				if (item instanceof AbstractBaseEntity) {
					try {
						return new ReadOnlyStringWrapper(mapper
							.apply(((AbstractBaseEntity<?>) item)));
					} catch (NullPointerException e) {
					}
					return new ReadOnlyStringWrapper("null");
				} else
					return new ReadOnlyStringWrapper(orMapper.apply(item));
			});
		return column;
	}

	public void setItems(List<ITEM> items) {
		List<TreeItem<Object>> treeItems = new ArrayList<TreeItem<Object>>();
		items.forEach(item -> {
			TreeItem<Object> node = new TreeItem<>(item);
			node.getChildren().addAll(createTreeItems(item, 0));
			treeItems.add(node);
		});
		root.getChildren().clear();
		root.getChildren().addAll(treeItems);
	}

	@SuppressWarnings("rawtypes")
	private List<TreeItem<Object>> createTreeItems(Object bean, int level) {
		if (level > 20)
			return new ArrayList<TreeItem<Object>>();
		
		List<Object> entities = Main.getBeaninfos().getEntities(bean);
		entities = entities.stream()
			.filter(entity -> entity != bean)
			.filter(entity -> !(entity instanceof Person))
			.collect(Collectors.toList());
		List<TreeItem<Object>> entityTreeItems = entities.stream()
			.filter(entity -> entity != null)
			.map(entity -> {
				TreeItem<Object> node = new TreeItem<>(entity);
				node.getChildren()
					.addAll(createTreeItems(entity, level + 1));
				return node;
			}).collect(Collectors.toList());

		List<Collection<Object>> entityCollections = Main.getBeaninfos()
			.getEntityCollections(bean);
		entityCollections = entityCollections.stream()
			.filter(entity -> entity != bean)
			.filter(entity -> !(entity instanceof Person))
			.collect(Collectors.toList());
		List<TreeItem<Object>> entityCollectionTreeItems = entityCollections
			.stream()
			.filter(entity -> entity != null)
			.map(entity -> {
				TreeItem<Object> node = new TreeItem<>(entity);
				List<TreeItem<Object>> childrenNodes = entity.stream()
					.flatMap(subEntity -> createTreeItems(subEntity, level + 1)
						.stream())
					.collect(Collectors.toList());
				node.getChildren()
					.addAll(childrenNodes);
				return node;
			}).collect(Collectors.toList());

		List<Object> properties = Main.getBeaninfos().getProperties(bean);
		properties = properties.stream().filter(p -> !(p instanceof Class))
			.filter(p -> {
				if (p instanceof Collection) {
					Collection<?> coll = (Collection) p;
					if (coll.isEmpty())
						return false;
				}
				return true;
			}).sorted((p1,p2) -> {
				if (UUID.class.isAssignableFrom(p1.getClass()))
					return -1;
				return 0;
			}).collect(Collectors.toList());
		
		List<TreeItem<Object>> propertyTreeItems = properties.stream()
			.map(o -> new TreeItem<>(o))
			.collect(Collectors.toList());
		
		entityTreeItems.addAll(entityCollectionTreeItems);
		entityTreeItems.addAll(propertyTreeItems);
		return entityTreeItems;
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
