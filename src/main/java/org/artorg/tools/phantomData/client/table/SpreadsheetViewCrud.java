package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.server.specification.DatabasePersistent;
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
import javafx.scene.control.Control;
import javafx.scene.control.TextField;

public class SpreadsheetViewCrud<TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
		ITEM extends DatabasePersistent<ITEM, ID_TYPE>, 
		ID_TYPE>
			implements TableGui<TABLE, ITEM, ID_TYPE> {

	private SpreadsheetView spreadsheet;
	private Table<TABLE, ITEM, ID_TYPE> table;
	private ListChangeListener<ITEM> changeListener;
		
	@Override
	public void setTable(Table<TABLE, ITEM, ID_TYPE> table) {
//		changeListener = new ListChangeListener<ITEM>() {
//			@Override
//			public void onChanged(Change<? extends ITEM> c) {
//				refresh();
//			}
//		};
		
		changeListener = (item) -> refresh();
		
		
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
		spreadsheet = new SpreadsheetView();
		
		// create Grid
		int rowCount = table.getNrows() + 1 ;
		int columnCount = table.getNcols();
		GridBase grid = new GridBase(rowCount, columnCount);

		List<String> columnNames = table.getColumnNames();

		ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
		final ObservableList<SpreadsheetCell> rowColumnNames = FXCollections.observableArrayList();
		for (int col = 0; col < columnCount; col++) {
			SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(0, col, 1, 1, columnNames.get(col));
			cell.setEditable(false);
			rowColumnNames.add(cell);
		}
		rows.add(rowColumnNames);
		
		
		
		for (int row=0; row < table.getNrows(); row++) {
			final ObservableList<SpreadsheetCell> rowItem = FXCollections.observableArrayList();

			for (int col=0; col < columnCount; col++) {
				String value = table.getValue(row, col);
				SpreadsheetCellBase cell = new SpreadsheetCellBase(row+1, col, 1, 1);
				TextField label = new TextField();
				label.setText(value);
				final int localRow = row;
				final int localCol = col;
				
//				label.setEditable(false);
				
//				cell.setItem(value);
				
				label.focusedProperty().addListener(new ChangeListener<Boolean>() {
				    @Override
				    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
				        if (!newPropertyValue)
				        	table.setValue(localRow, localCol, label.getText(),
									s -> {
										label.setText(s);
//										rows.get(localRow).get(localCol).setItem(s);
//										cell.setItem(s);
									}, s -> {
										label.setText(s);
//										rows.get(localRow).get(localCol).setItem(s);
//										cell.setItem(s);
									});
				    }
				});
				label.setOnAction((event) -> {
					table.setValue(localRow, localCol, label.getText(), 
							s -> {
								label.setText(s);
//								rows.get(localRow).get(localCol).setItem(s);
							},
							s -> {
								label.setText(s);
//								rows.get(localRow).get(localCol).setItem(s);
							});
				});
				
				cell.setGraphic(label);
				rowItem.add(cell);
			}
			rows.add(rowItem);
		}
		grid.setRows(rows);

		spreadsheet.setGrid(grid);
		spreadsheet.setStyle("-fx-focus-color: transparent;");
		
		spreadsheet.setFilteredRow(0);
		for (int col=0; col < columnCount; col++) {
			Filter filter = new FilterBase(spreadsheet, col);
			spreadsheet.getColumns().get(col).setFilter(filter);
		}

        
		System.out.println("test");
        
		
	}

	@Override
	public void reload() {
		table.getItems().removeListener(changeListener);
		table.readAllData();
		refresh();
		table.getItems().addListener(changeListener);
	}

}
