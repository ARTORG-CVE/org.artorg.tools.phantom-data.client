package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.artorg.tools.phantomData.client.table.multiSelectCombo.FilterBox;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.spreadsheet.Filter;
import org.controlsfx.control.spreadsheet.FilterBase;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class SpreadsheetViewCrud<TABLE extends Table<TABLE, ITEM, ID_TYPE>, ITEM extends DatabasePersistent<ITEM, ID_TYPE>, ID_TYPE>
		implements TableGui<TABLE, ITEM, ID_TYPE> {

	private SpreadsheetView spreadsheet;
	private FilterTable<TABLE, ITEM, ID_TYPE> table;
	private ListChangeListener<ITEM> changeListener;
	private ObservableList<ObservableList<SpreadsheetCell>> rows;

	{
		rows = FXCollections.observableArrayList();
	}

	@Override
	public void setTable(FilterTable<TABLE, ITEM, ID_TYPE> table) {
		// changeListener = new ListChangeListener<ITEM>() {
		// @Override
		// public void onChanged(Change<? extends ITEM> c) {
		// refresh();
		// }
		// };

		changeListener = (item) -> refreshValues();

		table.getItems().addListener(changeListener);
		this.table = table;

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
		System.out.println("refresh in spreadsheet");
		
		spreadsheet = new SpreadsheetView();
		int columnCount = table.getNcols();
		rows = FXCollections.observableArrayList();

		// Column header filterable
		final ObservableList<SpreadsheetCell> rowItemFilter = FXCollections.observableArrayList();
		for (int col = 0; col < columnCount; col++) {
			String value = table.getColumnNames().get(col);
			SpreadsheetCellBase cell = new SpreadsheetCellBase(0, col, 1, 1);

			final int localCol = col;
			List<Callable<String>> getters = new ArrayList<Callable<String>>();
			for (int row = 0; row < table.getNrows(); row++) {
				final int localRow = row;
				getters.add(() -> table.getValue(localRow, localCol));
			}

			FilterBox filterBox = new FilterBox(value, getters);
			filterBox.addFilterChangedListener(new FilterItemListener() {

				@Override
				public void changed(List<String> newValues) {
					System.out.println("Filter changed listener called");
					table.setColumnFilterValues(localCol, newValues);
					table.applyFilter();
					refreshValues();
				}
				
			});
			cell.setGraphic(filterBox);
			rowItemFilter.add(cell);
		}
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

		rows.remove(1, rows.size());

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
							table.setValue(localRow, localCol, label.getText(), s -> label.setText(s),
									s -> label.setText(s));
					}
				});
				label.setOnAction((event) -> {
					table.setValue(localRow, localCol, label.getText(), s -> label.setText(s), s -> label.setText(s));
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
		table.getItems().removeListener(changeListener);
		table.readAllData();
		refresh();
		table.getItems().addListener(changeListener);
	}

}
