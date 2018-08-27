package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.artorg.tools.phantomData.client.table.control.FilterMenuButton;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellBase;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import ch.qos.logback.core.net.SyslogOutputStream;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;

public class SpreadsheetViewCrud<TABLE extends Table<TABLE, ITEM, ID_TYPE>, ITEM extends DatabasePersistent<ITEM, ID_TYPE>, ID_TYPE>
		extends TableGui<TABLE, ITEM, ID_TYPE> {

	private SpreadsheetView spreadsheet;
	private FilterTable<TABLE, ITEM, ID_TYPE> table;
	private ObservableList<ObservableList<SpreadsheetCell>> rows;
	private ObservableList<SpreadsheetCell> rowItemFilter;

	{
		rows = FXCollections.observableArrayList();
		
	}

	@Override
	public void setTable(FilterTable<TABLE, ITEM, ID_TYPE> table) {
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
			FilterMenuButton filterMenuButton = new FilterMenuButton();
			filterMenuButton.setGetters(createGetters);
			filterMenuButton.setText(value);
			filterMenuButton.addEventHandler(ComboBox.ON_HIDDEN, event -> {
				table.setColumnItemFilterValues(localCol, filterMenuButton.getSelectedValues());
				if (filterMenuButton.isSortComparatorSet())
					table.setSortComparator(filterMenuButton.getSortComparator(), 
							item -> table.getValue(item,localCol));
			    refresh();
			});
			filterMenuButton.addEventHandler(ComboBox.ON_SHOWING, event -> {
				filterMenuButton.updateNodes();
			});
			
			cell.setGraphic(filterMenuButton);
			rowItemFilter.add(cell);
		}
		
		reload();
	}
	
	@Override
	public void reload() {
		table.readAllData();
		refresh();
	}

	@Override
	public void autoResizeColumns() {
		// TODO Auto-generated method stub

	}

	

	@Override
	public void refresh() {
		spreadsheet = new SpreadsheetView();
		rows = FXCollections.observableArrayList();

		rows.add(rowItemFilter);

		spreadsheet.setStyle("-fx-focus-color: transparent;");
		refreshValues();
		
		super.refresh();

	}

	public void refreshValues() {
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
	public Control getGraphic() {
		return spreadsheet;
	}
	
	@Override
	public String toString() {
		ObservableList<ObservableList<SpreadsheetCell>> items = spreadsheet.getItems();
		int nRows = items.size();
		if (nRows == 0) return "";
		int nCols = items.get(0).size();
		String[][] content = new String[nRows][nCols];
		
		for(int i=0; i<nRows; i++) 
			for(int j=0; j<nCols; j++) {
				Object item = items.get(i).get(j).getGraphic();
				if (item instanceof TextField)
					content[i][j] = ((TextField)item).getText();
				else if (item instanceof MenuButton)
					content[i][j] = ((MenuButton)item).getText();
				else
					content[i][j] = "?? Class<?> " +item.getClass().getSimpleName();
			}
		
		int[] columnWidth = new int[nCols];
		for(int j=0; j<nCols; j++) {
			int maxLength = 0;
			for(int i=0; i<nRows; i++) {
				if (content[i][j].length() > maxLength)
					maxLength = content[i][j].length();
			}
			columnWidth[j] = maxLength;
		}
		
		List<String> columnStrings = new ArrayList<String>();
		
		for(int j=0; j<nCols; j++) {
			content[0][j] = content[0][j] +StringUtils
					.repeat(" ", columnWidth[j] - content[0][j].length());	
		}
		columnStrings.add(Arrays.stream(content[0]).collect(Collectors.joining("|")));
		columnStrings.add(StringUtils.repeat("-", columnStrings.get(0).length()));
		
		
		for(int i=1; i<nRows; i++) {
			for(int j=0; j<nCols; j++)
				content[i][j] = content[i][j] +StringUtils
					.repeat(" ", columnWidth[j] - content[i][j].length());
			columnStrings.add(Arrays.stream(content[i]).collect(Collectors.joining("|")));
		}
		
		return columnStrings.stream().collect(Collectors.joining("\n"));
	}

	

}
