package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.artorg.tools.phantomData.client.commandPattern.UndoManager;
import org.artorg.tools.phantomData.client.commandPattern.UndoRedoNode;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.table.columns.AbstractColumn;
import org.artorg.tools.phantomData.client.util.CollectionUtil;
import org.artorg.tools.phantomData.client.util.CollectorsHuma;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public interface ITable<T, R> {

	List<AbstractColumn<T, ? extends R>> getColumns();

	void updateColumns();

	Class<T> getItemClass();

	String getTableName();

	void setTableName(String name);

	void setItemName(String name);

	ObservableList<T> getItems();

	

	void refresh();
	
	boolean isReloadable();

	boolean isEditable();
	
	boolean isFilterable();
	
	ICrudConnector<DbPersistent<T,?>> getConnector();

//	boolean isUndoRedoable();
	
//	UndoManager getUndoManager();

//	default void setValue(int row, int col, R value, Consumer<R> redo, Consumer<R> undo) {
//	setValue(getItems().get(row), col, value, redo, undo);
//}
	
//	default void setValue(T item, int col, R value, Consumer<R> redo, Consumer<R> undo) {
//		R currentValue = getValue(item, col);
//		if (value.equals(currentValue))  return;
//		
//		UndoRedoNode node = new UndoRedoNode(() -> {
//				getColumns().get(col).set(item, value);
//				redo.accept(value);
//			}, () -> {
//				getColumns().get(col).set(item, currentValue);
//				undo.accept(currentValue);
//			}, () -> {
//				getColumns().get(col).update(item);
//				});
//		getUndoManager().addAndRun(node);
//	}
	
	@SuppressWarnings("unchecked")
	default void readAllData() {
		if (!isReloadable()) return;
		Set<DbPersistent<T,?>> itemSet = new HashSet<>();
		Class<DbPersistent<T,?>> itemClass = (Class<DbPersistent<T, ?>>) getItemClass();
		if (itemClass == null)
			throw new NullPointerException();
		ICrudConnector<DbPersistent<T,?>> connector = getConnector();
		if (connector == null)
			throw new NullPointerException();
		itemSet.addAll(connector.readAllAsSet());
		
		ObservableList<DbPersistent<T,?>> items = FXCollections.observableArrayList();
		items.addAll(itemSet);
		
		CollectionUtil.syncLists(items, (List<DbPersistent<T,?>>)getItems());
		
		getColumns().stream().forEach(column -> {
			column.setItems(getItems());
//			column.getItems().clear();
//			column.getItems().addAll(getItems());
			
			});
	}
	
	
	@SuppressWarnings("unchecked")
	default void createItem(DbPersistent<T,?> item) {
		getConnector().create(item);
		getItems().add((T) item);
	}
	
	default String getItemName() {
		return getItemClass().getSimpleName();
	}
	
	default void setValue(T item, int col, R value) {
		if (!isEditable()) return;

		getColumns().get(col).set(item, value);

	}

	default void setValue(int row, int col, R value) {
		if (!isEditable()) return;
		setValue(getItems().get(row), col, value);
	}

	default AbstractColumn<T, ? extends R> getIdColumn() {
		return getColumns().stream().filter(c -> c.isIdColumn())
			.collect(CollectorsHuma.toSingleton());
	}

	default void setIdColumn(AbstractColumn<T, ? extends R> column) {
		UnaryOperator<AbstractColumn<T, ? extends R>> unaryOperator =
			c -> c.isIdColumn() ? column : c;
		getColumns().replaceAll(unaryOperator);
	}

	default List<AbstractColumn<T, ? extends R>> getVisibleColumns() {
		return getColumns().stream().filter(c -> c.isVisible())
			.collect(Collectors.toList());
	}

	default R getValue(int row, int col) {
		return getValue(getItems().get(row), col);
	}

	default R getValue(T item, int col) {
		return getColumns().get(col).get(item);
	}

	default int getNrows() {
		return getItems().size();
	}

	default int getNcols() {
		return getColumns().size();
	}

	default List<String> getColumnNames() {
		return getColumns().stream().map(c -> c.getName()).collect(Collectors.toList());
	}

	default String createString() {
		int nRows = this.getNrows();
		int nCols = this.getNcols();
		if (nRows == 0 || nCols == 0) return "";
		Object[][] content = new Object[nRows + 1][nCols];

		for (int col = 0; col < nCols; col++)
			content[0][col] = getColumnNames().get(col);

		for (int row = 0; row < nRows; row++)
			for (int col = 0; col < nCols; col++)
				content[row + 1][col] = this.getValue(row, col);

		int[] columnWidth = new int[nCols];
		for (int col = 0; col < nCols; col++) {
			int maxLength = 0;
			for (int row = 0; row < nRows; row++) {
				if (content[row + 1][col].toString().length() > maxLength)
					maxLength = content[row + 1][col].toString().length();
			}
			columnWidth[col] = maxLength;
		}

		List<String> columnStrings = new ArrayList<String>();

		for (int col = 0; col < nCols; col++) {
			content[0][col] = content[0][col] + StringUtils.repeat(" ",
				columnWidth[col] - content[0][col].toString().length());
		}
		columnStrings.add(Arrays.stream(content[0]).map(o -> o.toString())
			.collect(Collectors.joining("|")));
		columnStrings.add(StringUtils.repeat("-", columnStrings.get(0).length()));

		for (int row = 1; row < nRows; row++) {
			for (int j = 0; j < nCols; j++)
				content[row][j] = content[row + 1][j] + StringUtils.repeat(" ",
					columnWidth[j] - content[row + 1][j].toString().length());
			columnStrings.add(Arrays.stream(content[row + 1]).map(o -> o.toString())
				.collect(Collectors.joining("|")));
		}

		return columnStrings.stream().collect(Collectors.joining("\n"));
	}

}
