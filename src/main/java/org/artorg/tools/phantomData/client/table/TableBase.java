package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.util.CollectionUtil;
import org.artorg.tools.phantomData.client.util.Reflect;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

@SuppressWarnings("unchecked")
public class TableBase<T> implements ITable<T,Object> {
	private ObservableList<T> items;
	private List<AbstractColumn<T,? extends Object>> columns;
	private boolean isIdColumnVisible;
	private String tableName;
	private String itemName;
	private final Class<T> itemClass;
	private Function<List<T>,List<AbstractColumn<T,? extends Object>>> columnCreator;
	private final ListChangeListener<T> itemListChangeListener;

	public ListChangeListener<T> getItemListChangeListener() {
		return itemListChangeListener;
	}

	{
		items = FXCollections.observableArrayList();
		columns = new ArrayList<AbstractColumn<T,? extends Object>>();
		isIdColumnVisible = true;
		columnCreator = items -> new ArrayList<AbstractColumn<T,? extends Object>>();
		
		itemListChangeListener = new ListChangeListener<T>() {
			@Override
			public void onChanged(Change<? extends T> c) {
				updateColumns();
			}
		}; 
		items.addListener(itemListChangeListener);
	}
	
	@Override
	public void refresh() {
		updateColumns();
	}
	
	public TableBase() {
		itemClass = (Class<T>) Reflect.findGenericClasstype(this);
		this.itemName = itemClass.getSimpleName();
	}
	
	public TableBase(Class<T> itemClass) {
		this.itemClass = itemClass;
		this.itemName = itemClass.getSimpleName();
	}
	
//	public void setItems(ObservableList<T> items) {
//		this.items = items;
//		updateColumns();
//	}
	
	public boolean isIdColumnVisible() {
		return isIdColumnVisible;
	}

	public void setIdColumnVisible(boolean isIdColumnVisible) {
		this.isIdColumnVisible = isIdColumnVisible;
	}
	
	@Override
	public void updateColumns() {
		CollectionUtil.syncLists(this.columns, columnCreator.apply(getItems()),
				(column, newColumn) -> column.getName().equals(newColumn.getName()));
		getColumns().stream().forEach(column -> {
			column.setItems(getItems());
//			column.getItems().clear();
//			column.getItems().addAll(getItems());
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
	public List<AbstractColumn<T,? extends Object>> getColumns() {
		return this.columns;
	}

	@Override
	public ObservableList<T> getItems() {
		return this.items;
	}

	@Override
	public final Class<T> getItemClass() {
		return this.itemClass;
	}
	
	public Function<List<T>, List<AbstractColumn<T,? extends Object>>> getColumnCreator() {
		return columnCreator;
	}

	public void
		setColumnCreator(Function<List<T>, List<AbstractColumn<T,? extends Object>>> columnCreator) {
		this.columnCreator = columnCreator;
		updateColumns();
	}

	

}
