package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.artorg.tools.phantomData.server.specification.DatabasePersistent;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellBase;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;

public class SpreadsheetViewCrud<TABLE extends Table<TABLE, ITEM, ID_TYPE>, ITEM extends DatabasePersistent<ITEM, ID_TYPE>, ID_TYPE>
		implements TableGui<TABLE, ITEM, ID_TYPE> {

	private SpreadsheetView spreadsheet;
	private FilterTable<TABLE, ITEM, ID_TYPE> table;
	private ListChangeListener<ITEM> changeListener;
	private ObservableList<ObservableList<SpreadsheetCell>> rows;
	private ObservableList<SpreadsheetCell> rowItemFilter;

	{
		rows = FXCollections.observableArrayList();
	
	}

	@Override
	public void setTable(FilterTable<TABLE, ITEM, ID_TYPE> table) {
//		changeListener = (item) -> refreshValues();
//
//		table.getItems().addListener(changeListener);
		this.table = table;

		
		int columnCount = table.getNcols();
		
		// Column header filterable
		rowItemFilter = FXCollections.observableArrayList();
				for (int col = 0; col < columnCount; col++) {
					String value = table.getColumnNames().get(col);
					SpreadsheetCellBase cell = new SpreadsheetCellBase(0, col, 1, 1);

					final int localCol = col;
					Supplier<List<String>> createGetters = () -> {
						
						List<String> getters = new ArrayList<String>();
						for (int row = 0; row < table.getNrows(); row++) {
							final int localRow = row;
							getters.add(table.getValue(localRow, localCol));
						}
						return getters;
					};
					
//					Comparator<? super String> comparator = (i1,i2) -> table.getValue(i1, localCol).compareTo(table.getValue(i2, localCol));
					

					MultiSelectComboBox filterBox = new SortFilterBox(value, createGetters);
					filterBox.addEventHandler(ComboBox.ON_HIDDEN, event -> {
						table.setColumnFilterValues(localCol, filterBox.getSelectedValues());
						if (filterBox.isSortComparatorSet()) {
							System.out.println("set table comparator in spreadsheet");
							table.setSortComparator(filterBox.getSortComparator(), item -> table.getValue(item,localCol));
						}
//						filterBox.setSortComparator(null);
					    refresh();
					});
					filterBox.addEventHandler(ComboBox.ON_SHOWING, event -> {
						filterBox.updateNodes();
					});
					
					cell.setGraphic(filterBox);
					rowItemFilter.add(cell);
				}
		
		
		
		reload();
		
		

	}

	@Override
	public void autoResizeColumns() {
		// TODO Auto-generated method stub

	}

	@Override
	public Control getGraphic() {
		return spreadsheet;
	}

	@Override
	public void refresh() {
		spreadsheet = new SpreadsheetView();
		int columnCount = table.getNcols();
		rows = FXCollections.observableArrayList();

		
		rows.add(rowItemFilter);

		spreadsheet.setStyle("-fx-focus-color: transparent;");
		refreshValues();
		

	}

	public void refreshValues() {
		System.out.println("refresh values");
		// create Grid
		int rowCount = table.getFilteredNrows() + 1;
		int columnCount = table.getNcols();
		GridBase grid = new GridBase(rowCount, columnCount);
		
		table.applyFilter();

		ObservableList<SpreadsheetCell> rowItemFilter = rows.get(0);
		
		rows  = FXCollections.observableArrayList();
		rows.add(rowItemFilter);


		// value cells
		for (int row = 0; row < table.getFilteredNrows(); row++) {
			final ObservableList<SpreadsheetCell> rowItem = FXCollections.observableArrayList();

			for (int col = 0; col < columnCount; col++) {
				String value = table.getFilteredValue(row, col);
				SpreadsheetCellBase cell = new SpreadsheetCellBase(row + 1, col, 1, 1);
				TextField label = new TextField();

				label.setText(value);
				final int localRow = row;
				final int localCol = col;

				// label.setEditable(false);

				label.focusedProperty().addListener(new ChangeListener<Boolean>() {
					@Override
					public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue,
							Boolean newPropertyValue) {
						if (!newPropertyValue)
							table.setFilteredValue(localRow, localCol, label.getText(), s -> label.setText(s),
									s -> label.setText(s));
					}
				});
				label.setOnAction((event) -> {
					table.setFilteredValue(localRow, localCol, label.getText(), s -> label.setText(s), s -> label.setText(s));
				});

				cell.setGraphic(label);
				rowItem.add(cell);
			}
			rows.add(rowItem);
		}
		grid.setRows(rows);

		spreadsheet.setGrid(grid);

	}

	@Override
	public void reload() {
//		table.getItems().removeListener(changeListener);
		table.readAllData();
		refresh();
//		table.getItems().addListener(changeListener);
	}

}
