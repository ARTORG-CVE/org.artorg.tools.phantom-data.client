package org.artorg.tools.phantomData.client.scene.control;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.scene.layout.AddableToAnchorPane;
import org.artorg.tools.phantomData.client.table.TableBase;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.model.Phantom;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.AnchorPane;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;

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
		
		
		TreeTableColumn<Object,String> column = new TreeTableColumn<>("Column");
		
		
		TreeItem<Object> childNode1 = new TreeItem<>("Child Node 1");
        TreeItem<Object> childNode2 = new TreeItem<>("Child Node 2");
        TreeItem<Object> childNode3 = new TreeItem<>("Child Node 3");
        
        TreeItem<Object> childNode4 = new TreeItem<>("Child Node 4");
        TreeItem<Object> childNode5 = new TreeItem<>("Child Node 5");
        TreeItem<Object> childNode6 = new TreeItem<>("Child Node 6");
        
        childNode1.getChildren().add(childNode4);
        childNode2.getChildren().add(childNode5);
        childNode3.getChildren().add(childNode6);
        
        
        
		
		root.getChildren().addAll(childNode1, childNode2, childNode3);  
		root.setExpanded(true);
		
		column.setPrefWidth(150);   
		column.setCellValueFactory((CellDataFeatures<Object, String> p) -> 
        new ReadOnlyStringWrapper(p.getValue().getValue().toString()));  
		getColumns().add(column);
		
		
		
		TreeTableColumn<Object,String> column2 = new TreeTableColumn<>("Column2");
        column2.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Object, String> param) -> 
                new ReadOnlyStringWrapper(param.getValue().getValue().toString())
            );
        getColumns().add(column2);
        
        TreeTableColumn<Object,String> column3 = new TreeTableColumn<>("Column2");
        column3.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Object, String> param) -> 
                new ReadOnlyStringWrapper(param.getValue().getValue().getClass().getSimpleName())
            );
        getColumns().add(column3);

		
		super.setRoot(root);
		setPrefWidth(152);
        setShowRoot(false);  
		
		super.refresh();
		
	}
	
	public void setItems(List<Phantom> items) {
		List<TreeItem<Object>> treeItems = new ArrayList<TreeItem<Object>>();
		items.forEach(item -> {
			TreeItem<Object> node = new TreeItem<>(item);
			node.getChildren().addAll(createTreeItems(item));
			treeItems.add(node);
			
//			TreeItem<Object> childNode1 = new TreeItem<>(item.getProductId());
//			
//			TreeItem<Object> childNode2 = new TreeItem<>(item.getAnnulusDiameter());
//			childNode1.getChildren().add(childNode2);
//			
//			treeItems.add(childNode1);
		});
		root.getChildren().clear();
		root.getChildren().addAll(treeItems);
	}
	
	private List<TreeItem<Object>> createTreeItems(Object bean) {
		List<Object> entities = Main.getBeaninfos().getEntities(bean);
		List<TreeItem<Object>> entityTreeItems = entities.stream()
				.map(entity -> {
					TreeItem<Object> node = new TreeItem<>(entity);
					node.getChildren().addAll(createTreeItems(entity));
					return node;
				}).collect(Collectors.toList());
		List<Object> properties = Main.getBeaninfos().getProperties(bean);
		List<TreeItem<Object>> propertyTreeItems = properties.stream().map(o -> new TreeItem<>(o)).collect(Collectors.toList());
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
