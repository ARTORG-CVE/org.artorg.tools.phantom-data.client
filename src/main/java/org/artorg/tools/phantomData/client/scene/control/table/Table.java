package org.artorg.tools.phantomData.client.scene.control.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.artorg.tools.phantomData.client.commandPattern.UndoManager;
import org.artorg.tools.phantomData.client.commandPattern.UndoRedoNode;
import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class Table<TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
		ITEM extends DatabasePersistent<ID_TYPE>, 
		ID_TYPE> {
	protected final ObservableList<ITEM> items;
	private final List<IColumn<ITEM, ?>> columns;
	protected final UndoManager undoManager;
	protected HttpDatabaseCrud<ITEM, ID_TYPE> connector;
	private boolean isIdColumnVisible;
	
	{
		undoManager = new UndoManager();
		items = FXCollections.observableArrayList();
		
		columns = new ArrayList<IColumn<ITEM, ?>>();
		isIdColumnVisible = false;
	}
	
	public void setConnector(HttpDatabaseCrud<ITEM, ID_TYPE> connector) {
		this.connector = connector;
		this.columns.clear();
		readAllData();
		this.columns.addAll(createColumns());
	}
	
	public void readAllData() {
		Set<ITEM> itemSet = new HashSet<ITEM>();
		itemSet.addAll(connector.readAllAsSet());
		items.clear();
		items.addAll(itemSet);
	}
	
	public abstract List<IColumn<ITEM, ?>> createColumns();
	
	public void setItems(ObservableList<ITEM> items) {
		this.items.clear();
		this.items.addAll(items);
	}
	
	// exchange methods
	public String getValue(ITEM item, int col) {
		return columns.get(col).get(item);
	}
	
	private void setValue(ITEM item, int col, String value, Consumer<String> redo, Consumer<String> undo) {
		String currentValue = getValue(item, col);
		if (value.equals(currentValue))  return;
		
		UndoRedoNode node = new UndoRedoNode(() -> {
				columns.get(col).set(item, value);
				redo.accept(value);
			}, () -> {
				columns.get(col).set(item, currentValue);
				undo.accept(currentValue);
			}, () -> {
				columns.get(col).update(item);
				});
		undoManager.addAndRun(node);
	}
	
	public String getValue(int row, int col) {
		return columns.get(col).get(items.get(row));
	}
	
	public void setValue(int row, int col, String value, Consumer<String> redo, Consumer<String> undo) {
		setValue(items.get(row), col, value, redo, undo);
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
	
	public UndoManager getUndoManager() {
		return undoManager;
	}
	
	public boolean isIdColumnVisible() {
		return isIdColumnVisible;
	}

	public void setIdColumnVisible(boolean isIdColumnVisible) {
		this.isIdColumnVisible = isIdColumnVisible;
	}
	
	protected List<IColumn<ITEM, ?>> getColumns() {
		return columns;
	}

}
