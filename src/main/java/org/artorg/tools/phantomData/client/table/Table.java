package org.artorg.tools.phantomData.client.table;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
	private final ObservableList<ITEM> items;
	private final List<IColumn<ITEM, ?, ?>> columns;
	private final UndoManager undoManager;
	private HttpDatabaseCrud<ITEM, ID_TYPE> connector;
	
	{
		undoManager = new UndoManager();
		columns = createColumns();
		items = FXCollections.observableArrayList();
	}
	
	public void setConnector(HttpDatabaseCrud<ITEM, ID_TYPE> connector) {
		this.connector = connector;
	}
	
	public void readAllData() {
		Set<ITEM> itemSet = new HashSet<ITEM>();
		itemSet.addAll(connector.readAllAsSet());
		items.clear();
		items.addAll(itemSet);
	}
	
	public abstract List<IColumn<ITEM, ?, ?>> createColumns();
	
	public void setItems(ObservableList<ITEM> items) {
		this.items.clear();
		this.items.addAll(items);
	}
	
	// exchange methods
	public final Object getValue(ITEM item, int col) {
		return columns.get(col).get(item);
	}
	
	public final void setValue(ITEM item, int col, Object value) {
		columns.get(col).set(item, value);
	}
	
	public Object getValue(int row, int col) {
		return columns.get(col).get(items.get(row));
	}
	
	public void setValue(int row, int col, Object value) {
		columns.get(col).set(items.get(row), value);
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
	
//	public boolean set(ITEM item, U value) {
//		U currentValue = getter.apply(item);
//		UndoRedoNode node = new UndoRedoNode(() -> setter.accept(item, value), 
//				() -> setter.accept(item, currentValue),
//				() -> {connector.update(item); System.out.println("   --in item block--  ");
//				
//					System.out.println("   /--" +item.toString());
//				});
//		undoManager.addAndRun(node);
//		return true;
//	}

}
