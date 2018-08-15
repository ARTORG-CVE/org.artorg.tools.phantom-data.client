package org.artorg.tools.phantomData.client.table;

import java.util.List;

import org.artorg.tools.phantomData.server.specification.DatabasePersistent;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;

public class SpreadsheetViewCrud<TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
		ITEM extends DatabasePersistent<ITEM, ID_TYPE>, 
		ID_TYPE>
			implements TableGui {

	private final SpreadsheetView spreadsheet;

	private Table<TABLE, ITEM, ID_TYPE> table;

	{
		spreadsheet = new SpreadsheetView();
	}

	public void setTable(Table<TABLE, ITEM, ID_TYPE> table) {
		this.table = table;
		
		// create Grid
		int rowCount = table.getNrows() + 1;
		int columnCount = table.getNcols();
		GridBase grid = new GridBase(rowCount, columnCount);

		List<String> columnNames = this.table.getColumnNames();

		ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
		final ObservableList<SpreadsheetCell> rowColumnNames = FXCollections.observableArrayList();
		for (int col = 0; col < columnCount; col++)
			rowColumnNames.add(SpreadsheetCellType.STRING.createCell(0, col, 1, 1, columnNames.get(col)));
		rows.add(rowColumnNames);

		
		for (int row=0; row < table.getNrows(); row++) {
			final ObservableList<SpreadsheetCell> rowItem = FXCollections.observableArrayList();

			for (int col = 0; col < columnCount; col++) {
				// Object value =
				// table.getColumns().get(col).getCellData(table.getItems().get(row));
				Object value = table.getValue(row, col);
				if (value instanceof String) {
					SpreadsheetCellBase cell = new SpreadsheetCellBase(row, col, 1, 1);
					TextField label = new TextField();
					label.setText((String) value);
					final int localRow = row;
					final int localCol = row;
					label.setOnAction((event) -> {
						table.setValue(localRow, localCol, label.getText());
						System.out.println("--//--//--//--// items in Table start");
						table.toString();
						System.out.println("--//--//--//--// items in Table end");
					});
					cell.setGraphic(label);
					rowItem.add(cell);
				}
			}
			rows.add(rowItem);
		}
		grid.setRows(rows);

		spreadsheet.setGrid(grid);
	}

	@Override
	public void autoResizeColumns() {
		// TODO Auto-generated method stub

	}

	@Override
	public Control getGraphic() {
		return spreadsheet;
	}

}
