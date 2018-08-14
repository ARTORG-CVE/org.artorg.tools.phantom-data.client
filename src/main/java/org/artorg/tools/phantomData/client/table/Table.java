package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.commandPattern.PropertyUndoable;
import org.artorg.tools.phantomData.client.commandPattern.UndoManager;
import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public abstract class Table<TABLE extends Table<TABLE, ITEM, ID_TYPE>, ITEM extends DatabasePersistent<ITEM, ID_TYPE>, ID_TYPE> {
	private final ObservableList<ITEM> items;
	private final UndoManager undoManager;
	private final List<IColumn<ITEM, ?, ?>> columns;
	private final Function<ITEM, List<Object>> getters;
	
	
	
	{
		Set<ITEM> itemSet = new HashSet<ITEM>();
		itemSet.addAll(getConnector().readAllAsSet());
		items = FXCollections.observableArrayList(itemSet);
		undoManager = new UndoManager();
		columns = createColumns();
		getters = createGetters();
	}
	
	public abstract HttpDatabaseCrud<ITEM, ID_TYPE> getConnector();
	
	public abstract List<IColumn<ITEM, ?, ?>> createColumns();
	

	public ObservableList<ITEM> getItems() {
		return items;
	}
	
	public UndoManager getUndoManager() {
		return undoManager;
	}
	
	
	
	public Function<ITEM, List<Object>> createGetters() {
		Function<ITEM,List<Object>> results = item -> columns.stream()
				.map(c -> c.get(item)).collect(Collectors.toList());
		return results;
	}
	
	
	
	public final List<Object> getValues(ITEM item) {
		return getters.apply(item);
	}
	
	
	public final Object getValue(ITEM item, int col) {
		return getValues(item).get(col);
	}
	
	public final void setValue(ITEM item, int col, Object value) {
		columns.get(col).set(item, value);
		
		
		getSetters().get(col).accept(item, value);
	}
	
	public Object getValue(int row, int col) {
//		return getValue(items.get(row),col);
	}
	
	public void setValue(int row, int col, Object value) {
//		setValue(items.get(row), col, value);
	}

}
