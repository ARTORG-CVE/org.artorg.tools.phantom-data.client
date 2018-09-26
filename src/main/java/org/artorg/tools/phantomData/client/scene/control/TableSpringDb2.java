package org.artorg.tools.phantomData.client.scene.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring2;
import org.artorg.tools.phantomData.client.table.IColumn;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class TableSpringDb2<ITEM> {
	protected final ObservableList<ITEM> items;
	private final List<IColumn<ITEM>> columns;
	protected HttpConnectorSpring2<ITEM> connector;
	private boolean isIdColumnVisible;
	
	{
		items = FXCollections.observableArrayList();
		
		columns = new ArrayList<IColumn<ITEM>>();
		isIdColumnVisible = false;
	}
	
	public abstract List<IColumn<ITEM>> createColumns();
	
	public abstract String getTableName();
	
	public void setConnector(HttpConnectorSpring2<ITEM> connector) {
		this.connector = connector;
		this.columns.clear();
		readAllData();
		this.columns.addAll(createColumns());
	}
	
	public HttpConnectorSpring2<ITEM> getConnector() {
		return connector;
	}
	
	public void readAllData() {
		Set<ITEM> itemSet = new HashSet<ITEM>();
		itemSet.addAll(connector.readAllAsSet());
		items.clear();
		items.addAll(itemSet);
	}
	
	public Class<ITEM> getItemClass() {
		return connector.getModelClass();
	}
	
	public String getItemName() {
		return getItemClass().getSimpleName();
	}
	
	public void setItems(ObservableList<ITEM> items) {
		this.items.clear();
		this.items.addAll(items);
	}
	
	// exchange methods
	public String getValue(ITEM item, int col) {
		return columns.get(col).get(item);
	}
	
	public String getValue(int row, int col) {
		return columns.get(col).get(items.get(row));
	}
	
	// addiotional methods
	public List<String> getColumnNames() {
		return columns.stream().map(c -> c.getColumnName())
				.collect(Collectors.toList());
	}
	
	public int getNcols() {
		return columns.size();
	}
	
	public int getNrows() {
		return items.size();
	}
	
    @Override
	public String toString() {
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
	
	// Getters
	public ObservableList<ITEM> getItems() {
		return items;
	}
	
	public boolean isIdColumnVisible() {
		return isIdColumnVisible;
	}

	public void setIdColumnVisible(boolean isIdColumnVisible) {
		this.isIdColumnVisible = isIdColumnVisible;
	}
	
	protected List<IColumn<ITEM>> getColumns() {
		return columns;
	}

}
