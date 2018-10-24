package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.artorg.tools.phantomData.server.util.Collectors2;

import javafx.collections.ObservableList;

public interface ITable<ITEM> {
	
	List<AbstractColumn<ITEM>> getColumns();
	
	void updateColumns();
	
	Class<ITEM> getItemClass();
	
	String getTableName();
	
	void setTableName(String name);
	
	void setItemName(String name);
	
	ObservableList<ITEM> getItems();
	
	void setItems(ObservableList<ITEM> items);
	
	String getItemName();
	
	void refresh();
	
	default AbstractColumn<ITEM> getIdColumn() {
		return getColumns().stream().filter(c -> c.isIdColumn()).collect(Collectors2.toSingleton());
	}
	
	default  void setIdColumn(AbstractColumn<ITEM> column) {
		UnaryOperator<AbstractColumn<ITEM>> unaryOperator = c -> c.isIdColumn()? column: c;
		getColumns().replaceAll(unaryOperator);
	}
	
	default List<AbstractColumn<ITEM>> getVisibleColumns() {
		return getColumns().stream().filter(c -> c.isVisible()).collect(Collectors.toList());
	}
	
	default String getValue(int row, int col) {
		return getValue(getItems().get(row), col);
	}
	
	default String getValue(ITEM item, int col) {
		return getColumns().get(col).get(item);
	}
	
	default int getNrows() {
		return getItems().size();
	}
	
	default int getNcols() {
		return getColumns().size();
	}
	
	default List<String> getColumnNames() {
		return getColumns().stream().map(c -> c.getColumnName())
				.collect(Collectors.toList());
	}
	
	default String createString() {
		int nRows = this.getNrows();
		int nCols = this.getNcols();
		if (nRows == 0 || nCols == 0) return "";
		String[][] content = new String[nRows+1][nCols];
		
		for (int col=0; col<nCols; col++)
			content[0][col] = getColumnNames().get(col);
		
		for(int row=0; row<nRows; row++) 
			for(int col=0; col<nCols; col++)
				content[row+1][col] = this.getValue(row, col);
		
		int[] columnWidth = new int[nCols];
		for(int col=0; col<nCols; col++) {
			int maxLength = 0;
			for(int row=0; row<nRows; row++) {
				if (content[row+1][col].length() > maxLength)
					maxLength = content[row+1][col].length();
			}
			columnWidth[col] = maxLength;
		}
		
		List<String> columnStrings = new ArrayList<String>();
		
		for(int col=0; col<nCols; col++) {
			content[0][col] = content[0][col] +StringUtils
					.repeat(" ", columnWidth[col] - content[0][col].length());	
		}
		columnStrings.add(Arrays.stream(content[0]).collect(Collectors.joining("|")));
		columnStrings.add(StringUtils.repeat("-", columnStrings.get(0).length()));
		
		
		for(int row=1; row<nRows; row++) {
			for(int j=0; j<nCols; j++)
				content[row][j] = content[row+1][j] +StringUtils
					.repeat(" ", columnWidth[j] - content[row+1][j].length());
			columnStrings.add(Arrays.stream(content[row+1]).collect(Collectors.joining("|")));
		}
		
		return columnStrings.stream().collect(Collectors.joining("\n"));
	}

	

}
