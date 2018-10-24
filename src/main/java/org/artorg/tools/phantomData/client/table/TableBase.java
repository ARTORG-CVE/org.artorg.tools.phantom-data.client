package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.artorg.tools.phantomData.server.util.Reflect;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

@SuppressWarnings("unchecked")
public class TableBase<ITEM> implements ITable<ITEM> {
	private ObservableList<ITEM> items;
	private List<AbstractColumn<ITEM>> columns;
	private boolean isIdColumnVisible;
	private String tableName;
	private String itemName;
	private final Class<ITEM> itemClass;
	private Function<List<ITEM>,List<AbstractColumn<ITEM>>> columnCreator;

	{
		items = FXCollections.observableArrayList();
		columns = new ArrayList<AbstractColumn<ITEM>>();
		isIdColumnVisible = true;
		columnCreator = items -> new ArrayList<AbstractColumn<ITEM>>();
		items.addListener(new ListChangeListener<ITEM>() {
			@Override
			public void onChanged(Change<? extends ITEM> c) {
				updateColumns();
			}
		});
	}
	
	@Override
	public void refresh() {
		updateColumns();
	}
	
	public TableBase() {
		itemClass = (Class<ITEM>) Reflect.findGenericClasstype(this);
		this.itemName = itemClass.getSimpleName();
	}
	
	public TableBase(Class<ITEM> itemClass) {
		this.itemClass = itemClass;
		this.itemName = itemClass.getSimpleName();
	}
	
	public void setItems(ObservableList<ITEM> items) {
		this.items = items;
		updateColumns();
	}
	
	public boolean isIdColumnVisible() {
		return isIdColumnVisible;
	}

	public void setIdColumnVisible(boolean isIdColumnVisible) {
		this.isIdColumnVisible = isIdColumnVisible;
	}
	
	@Override
	public void updateColumns() {
		this.columns = columnCreator.apply(getItems());
		getColumns().stream().forEach(column ->column.setItems(getItems()));
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
	
	public Function<List<ITEM>, List<AbstractColumn<ITEM>>> getColumnCreator() {
		return columnCreator;
	}

	public void
		setColumnCreator(Function<List<ITEM>, List<AbstractColumn<ITEM>>> columnCreator) {
		this.columnCreator = columnCreator;
		updateColumns();
	}

	

}
