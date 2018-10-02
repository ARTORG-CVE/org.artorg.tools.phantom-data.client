package org.artorg.tools.phantomData.client.scene.control;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.ITable;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@SuppressWarnings("unchecked")
public class Table<ITEM extends DbPersistent<ITEM,?>> implements ITable<ITEM> {
	private ObservableList<ITEM> items;
	private List<AbstractColumn<ITEM>> columns;
	private boolean isIdColumnVisible;
	private String tableName;
	private String itemName;
	private final Class<ITEM> itemClass;
	
	
	{
		items = FXCollections.observableArrayList();
		columns = new ArrayList<AbstractColumn<ITEM>>();
		isIdColumnVisible = true;
		
		itemClass = (Class<ITEM>) Reflect.findGenericClasstype(this);
	}
	
	public void setItems(ObservableList<ITEM> items) {
		this.items = items;
		getColumns().stream().forEach(column -> column.setItems(items));
	}
	
	public boolean isIdColumnVisible() {
		return isIdColumnVisible;
	}

	public void setIdColumnVisible(boolean isIdColumnVisible) {
		this.isIdColumnVisible = isIdColumnVisible;
	}
	
	@Override
	public void setColumns(List<AbstractColumn<ITEM>> columns) {
		this.columns = columns;
		getColumns().stream().forEach(column -> {
			
			column.setItems(getItems());	
		});
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
	public List<AbstractColumn<ITEM>> getColumns() {
		return this.columns;
	}

	@Override
	public ObservableList<ITEM> getItems() {
		return this.items;
	}

	@Override
	public final Class<ITEM> getItemClass() {
		return this.itemClass;
	}

}
