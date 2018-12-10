package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.util.CollectionUtil;
import org.artorg.tools.phantomData.client.util.CollectorsHuma;
import org.artorg.tools.phantomData.client.util.Reflect;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

@SuppressWarnings("unchecked")
public class TableBase<T> {
	private ObservableList<T> items;
	private List<AbstractColumn<T,? extends Object>> columns;
	private boolean isIdColumnVisible;
	private String tableName;
	private String itemName;
	private final Class<T> itemClass;
	private Function<List<T>,List<AbstractColumn<T,? extends Object>>> columnCreator;
	private final ListChangeListener<T> itemListChangeListener;
	private boolean editable = true;
	
	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

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
	
	public void updateColumns() {
		CollectionUtil.syncLists(this.columns, columnCreator.apply(getItems()),
				(column, newColumn) -> column.getName().equals(newColumn.getName()));
		getColumns().stream().forEach(column -> {
			column.setItems(getItems());
//			column.getItems().clear();
//			column.getItems().addAll(getItems());
			});
	}	
	
	
	public void setValue(T item, int col, Object value) {
		getColumns().get(col).set(item, value);
	}
	
	public void setValue(int row, int col, Object value) {
		setValue(getItems().get(row), col, value);
	}
	
	public AbstractColumn<T,? extends Object> getIdColumn() {
		return getColumns().stream().filter(c -> c.isIdColumn()).collect(CollectorsHuma.toSingleton());
	}
	
	public  void setIdColumn(AbstractColumn<T,? extends Object> column) {
		UnaryOperator<AbstractColumn<T,? extends Object>> unaryOperator = c -> c.isIdColumn()? column: c;
		getColumns().replaceAll(unaryOperator);
	}
	
	public List<AbstractColumn<T,? extends Object>> getVisibleColumns() {
		return getColumns().stream().filter(c -> c.isVisible()).collect(Collectors.toList());
	}
	
	public Object getValue(int row, int col) {
		return getValue(getItems().get(row), col);
	}
	
	public Object getValue(T item, int col) {
		return getColumns().get(col).get(item);
	}
	
	public int getNrows() {
		return getItems().size();
	}
	
	public int getNcols() {
		return getColumns().size();
	}
	
	public List<String> getColumnNames() {
		return getColumns().stream().map(c -> c.getName())
				.collect(Collectors.toList());
	}
	
	public String createString() {
		int nRows = this.getNrows();
		int nCols = this.getNcols();
		if (nRows == 0 || nCols == 0) return "";
		Object[][] content = new Object[nRows+1][nCols];
		
		for (int col=0; col<nCols; col++)
			content[0][col] = getColumnNames().get(col);
		
		for(int row=0; row<nRows; row++) 
			for(int col=0; col<nCols; col++)
				content[row+1][col] = this.getValue(row, col);
		
		int[] columnWidth = new int[nCols];
		for(int col=0; col<nCols; col++) {
			int maxLength = 0;
			for(int row=0; row<nRows; row++) {
				if (content[row+1][col].toString().length() > maxLength)
					maxLength = content[row+1][col].toString().length();
			}
			columnWidth[col] = maxLength;
		}
		
		List<String> columnStrings = new ArrayList<String>();
		
		for(int col=0; col<nCols; col++) {
			content[0][col] = content[0][col] +StringUtils
					.repeat(" ", columnWidth[col] - content[0][col].toString().length());	
		}
		columnStrings.add(Arrays.stream(content[0]).map(o -> o.toString()).collect(Collectors.joining("|")));
		columnStrings.add(StringUtils.repeat("-", columnStrings.get(0).length()));
		
		
		for(int row=1; row<nRows; row++) {
			for(int j=0; j<nCols; j++)
				content[row][j] = content[row+1][j] +StringUtils
					.repeat(" ", columnWidth[j] - content[row+1][j].toString().length());
			columnStrings.add(Arrays.stream(content[row+1]).map(o -> o.toString()).collect(Collectors.joining("|")));
		}
		
		return columnStrings.stream().collect(Collectors.joining("\n"));
	}
	
	
	
    @Override
	public String toString() {
		return this.createString();
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public void setTableName(String name) {
		this.tableName = name;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public void setItemName(String name) {
		this.itemName = name;
	}
	
	public List<AbstractColumn<T,? extends Object>> getColumns() {
		return this.columns;
	}
	
	public ObservableList<T> getItems() {
		return this.items;
	}
	
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
