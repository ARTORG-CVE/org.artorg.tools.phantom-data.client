package org.artorg.tools.phantomData.client.scene.control.treeTableView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.scene.layout.AddableToAnchorPane;
import org.artorg.tools.phantomData.client.table.TableBase;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.model.Phantom;
import org.artorg.tools.phantomData.server.model.specification.AbstractBaseEntity;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeSortMode;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.AnchorPane;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.Person;

public class ProTreeTableView extends TreeTableView<Object> implements AddableToAnchorPane {
	private Class<Phantom> itemClass;
	private TableBase<Phantom> table;
	private TreeItem<Object> root = new TreeItem<Object>("Root node");

//	@SuppressWarnings("unchecked")
	public ProTreeTableView() {
//		itemClass = (Class<ITEM>) Reflect.findSubClassParameterType(this, ProTableView.class, 0);
	}

	public ProTreeTableView(Class<Phantom> itemClass) {
//		this.itemClass = itemClass;
	}

	public void initTable() {
		TreeTableColumn<Object, String> column;

		column = new TreeTableColumn<>("Item type");
		column.setPrefWidth(150);
		column.setCellValueFactory((TreeTableColumn.CellDataFeatures<Object, String> param) -> {
			Object item = param.getValue().getValue();
			if (item == null)
				return new ReadOnlyStringWrapper("null");
			return new ReadOnlyStringWrapper(param.getValue().getValue().getClass().getSimpleName());
		});
		getColumns().add(column);

		column = createColumn("Name", item -> item.createName(), item -> item.toString());
		getColumns().add(column);

		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		column = createColumn("Last modified", item -> format.format(item.getDateLastModified()));
		getColumns().add(column);

		column = createColumn("Changed by", item -> item.getChanger().getSimpleAcademicName());
		getColumns().add(column);
		
		column = createColumn("Added", item -> format.format(item.getDateAdded()));
		getColumns().add(column);

		column = createColumn("Creator", item -> item.getCreator().getSimpleAcademicName());
		getColumns().add(column);

		root.setExpanded(true);
		super.setRoot(root);
		setPrefWidth(152);
		setShowRoot(false);

		super.setSortMode(TreeSortMode.ONLY_FIRST_LEVEL);
		super.refresh();

	}

	private TreeTableColumn<Object, String> createColumn(String columnName,
			Function<AbstractBaseEntity<?>, String> mapper) {
		return createColumn(columnName, mapper, item -> "");
	}

	private TreeTableColumn<Object, String> createColumn(String columnName,
			Function<AbstractBaseEntity<?>, String> mapper, Function<Object, String> orMapper) {
		TreeTableColumn<Object, String> column = new TreeTableColumn<>(columnName);
		column.setPrefWidth(400);
		column.setCellValueFactory((TreeTableColumn.CellDataFeatures<Object, String> param) -> {
			Object item = param.getValue().getValue();
			if (item == null)
				return new ReadOnlyStringWrapper("null");
			if (item instanceof AbstractBaseEntity) {
				try {
					return new ReadOnlyStringWrapper(mapper.apply(((AbstractBaseEntity<?>) item)));
				} catch (NullPointerException e) {}
				return new ReadOnlyStringWrapper("null");
			}
			else
				return new ReadOnlyStringWrapper(orMapper.apply(item));
		});
		return column;
	}

	public void setItems(List<Phantom> items) {
		List<TreeItem<Object>> treeItems = new ArrayList<TreeItem<Object>>();
		items.forEach(item -> {
			TreeItem<Object> node = new TreeItem<>(item);
			node.getChildren().addAll(createTreeItems(item, 0));
			treeItems.add(node);
		});
		root.getChildren().clear();
		root.getChildren().addAll(treeItems);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<TreeItem<Object>> createTreeItems(Object bean, int level) {
		if (level>20) return new ArrayList<TreeItem<Object>>();
		List<Object> entities = Main.getBeaninfos().getEntities(bean);
		entities = entities.stream().filter(entity -> entity != bean).filter(entity -> !(entity instanceof Person)).collect(Collectors.toList());
		List<TreeItem<Object>> entityTreeItems = entities.stream().filter(entity -> entity != null).map(entity -> {
			TreeItem<Object> node = new TreeItem<>(entity);
			node.getChildren().addAll(createTreeItems(entity, level +1));
			return node;
		}).collect(Collectors.toList());
		List<Object> properties = Main.getBeaninfos().getProperties(bean);
		Function<Object,Stream<Object>> flatMapper = p -> {
			Stream<Object> stream;
			if (p instanceof Collection)
				stream = ((Collection)p).stream();
			stream = Arrays.asList(p).stream();
			return stream;
		}; 
		properties = properties.stream()
				.filter(p -> !(p instanceof Class))
				.filter(p -> {
					if (p instanceof Collection) {
						Collection<?> coll = (Collection)p;
						if (coll.isEmpty())
							return false;
					}
					return true;
				})
//				.flatMap(flatMapper)
				.collect(Collectors.toList());
		List<TreeItem<Object>> propertyTreeItems = properties.stream().map(o -> new TreeItem<>(o))
				.collect(Collectors.toList());
		entityTreeItems.addAll(propertyTreeItems);
		return entityTreeItems;
	}

	public void setTable(TableBase<Phantom> table) {
		this.table = table;
		initTable();
	}

	public TableBase<Phantom> getTable() {
		return table;
	}

	public javafx.scene.control.TreeTableView<?> getGraphic() {
		return this;
	}

	public Class<Phantom> getItemClass() {
		return itemClass;
	}

	public void addTo(AnchorPane pane) {
		FxUtil.addToAnchorPane(pane, this);
	}

}
