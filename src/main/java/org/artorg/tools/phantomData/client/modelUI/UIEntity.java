package org.artorg.tools.phantomData.client.modelUI;

import java.util.List;

import org.artorg.tools.phantomData.client.beans.DbNode;
import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.editor.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.tableView.DbTableView;
import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.scene.control.treeTableView.DbTreeTableView;
import org.artorg.tools.phantomData.client.scene.control.treeTableView.ProTreeTableView;
import org.artorg.tools.phantomData.client.table.DbTable;
import org.artorg.tools.phantomData.client.table.Table;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public interface UIEntity<T> {

	Class<T> getItemClass();
	
	String getTableName();
	
	List<AbstractColumn<T, ? extends Object>> createColumns(List<T> items);

	ItemEditFactoryController<T> createEditFactory();

	default Table<T> createTableBase() {
		Table<T> table = new Table<T>(getItemClass()) {
			@Override
			public List<AbstractColumn<T, ? extends Object>> createColumns(List<T> items) {
				return UIEntity.this.createColumns(items);
			}			
		};
		table.setTableName(getTableName());
		return table;
	}
	
	default DbTable<T> createDbTableBase() {
		DbTable<T> table = new DbTable<T>(getItemClass()) {
			@Override
			public List<AbstractColumn<T, ? extends Object>> createColumns(List<T> items) {
				return UIEntity.this.createColumns(items);
			}			
		};
		table.setTableName(getTableName());
		return table;
	}
	
	default ProTableView<T> createProTableView() {
		ProTableView<T> tableView = new ProTableView<>(getItemClass());
		return tableView;
	}
	
	default DbTableView<T> createDbTableView() {
		DbTableView<T> tableView = new DbTableView<>(getItemClass());
		
		tableView.showFilterButtons();
		tableView.refresh();
		
		tableView.getFilterMenuButtons().stream().forEach(column -> {
			column.updateNodes();
			column.applyFilter();
		});
		
		
		return tableView;
	}
	
	@SuppressWarnings("unchecked")
	default ProTableView<T> createProTableView(List<TreeItem<DbNode>> treeItems) {
		ProTableView<T> tableView = new ProTableView<>(getItemClass());
		ObservableList<T> items = FXCollections.observableArrayList();
		for (int i = 0; i < treeItems.size(); i++)
			try {
				T item = (T) treeItems.get(i).getValue().getValue();
				items.add(item);
			} catch (Exception e) {}

//		tableView.setItems(items);
		tableView.getItems().clear();
		tableView.getItems().addAll(items);

		return tableView;
	}
	
	@SuppressWarnings("unchecked")
	default DbTableView<T> createDbTableView(List<TreeItem<DbNode>> treeItems) {
		DbTableView<T> tableView = new DbTableView<>(getItemClass());
		ObservableList<T> items = FXCollections.observableArrayList();
		for (int i = 0; i < treeItems.size(); i++)
			try {
				T item = (T) treeItems.get(i).getValue().getValue();
				items.add(item);
			} catch (Exception e) {}

//		tableView.setItems(items);
		tableView.getItems().clear();
		tableView.getItems().addAll(items);

		return tableView;
	}
	
	default ProTreeTableView<T> createProTreeTableView(List<T> items) {
		ProTreeTableView<T> treeTableView = new ProTreeTableView<>(getItemClass());
		treeTableView.setItems(items);
		return treeTableView;
	}
	
	default DbTreeTableView<T> createDbTreeTableView() {
		DbTreeTableView<T> treeTableView = new DbTreeTableView<>(getItemClass());
		treeTableView.reload();
		treeTableView.initTable();
		return treeTableView;
	}

}
