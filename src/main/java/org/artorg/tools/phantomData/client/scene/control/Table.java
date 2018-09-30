package org.artorg.tools.phantomData.client.scene.control;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.ITable;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Table<ITEM extends DbPersistent<ITEM,?>> implements ITable<ITEM> {
	private final ObservableList<ITEM> items;
	private List<Column<ITEM>> columns;
	private boolean isIdColumnVisible;
	private String tableName;
	private String itemName;
	private Class<ITEM> itemClass;
	
	{
		items = FXCollections.observableArrayList();
		columns = new ArrayList<Column<ITEM>>();
		isIdColumnVisible = true;
	}
	
	public void setItems(ObservableList<ITEM> items) {
		this.items.clear();
		this.items.addAll(items);
	}
	
	public boolean isIdColumnVisible() {
		return isIdColumnVisible;
	}

	public void setIdColumnVisible(boolean isIdColumnVisible) {
		this.isIdColumnVisible = isIdColumnVisible;
	}
	
    @Override
	public String toString() {
		return this.createString();
	}
	
    @Override
	public String getTableName() {
		return tableName;
	}
	
	@Override
	public void setTableName(String name) {
		this.tableName = name;
	}
	
	@Override
	public String getItemName() {
		return itemName;
	}
	
	@Override
	public void setItemName(String name) {
		this.itemName = name;
	}

	@Override
	public List<Column<ITEM>> getColumns() {
		return this.columns;
	}

	@Override
	public void setItemClass(Class<ITEM> itemClass) {
		this.itemClass = itemClass;
	}

	@Override
	public ObservableList<ITEM> getItems() {
		return this.items;
	}

	@Override
	public Class<ITEM> getItemClass() {
		return this.itemClass;
	}

	@Override
	public void setColumns(List<Column<ITEM>> columns) {
		this.columns = columns;
	}	

}
