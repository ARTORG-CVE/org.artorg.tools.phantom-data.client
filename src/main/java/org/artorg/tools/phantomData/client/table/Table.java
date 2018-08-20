package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.commandPattern.UndoManager;
import org.artorg.tools.phantomData.client.commandPattern.UndoRedoNode;
import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class Table<TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
		ITEM extends DatabasePersistent<ITEM, ID_TYPE>, 
		ID_TYPE> {
	protected final ObservableList<ITEM> items;
	
	protected final List<IColumn<ITEM, ?>> columns;
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
			}, () -> columns.get(col).update(item));
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
		return items.stream().map(item -> item.toString())
				.collect(Collectors.joining("\n"));
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

}
