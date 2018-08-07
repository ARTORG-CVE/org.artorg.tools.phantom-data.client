package specification;

import java.util.List;
import java.util.stream.Collectors;

import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;

public interface Table<T> {
	
	TableView<T> createTableView(TableView<T> table);
	
	default TableView<T> createTableView() {
		return createTableView(new TableView<T>());
	}
	
	default SpreadsheetView createSpreadsheetView() {
		return createSpreadsheetView(createTableView());
	}
	
	default SpreadsheetView createSpreadsheetView(TableView<T> table) {
		// create Grid
		int rowCount = table.getItems().size()+1;
        int columnCount = table.getColumns().size();
        GridBase grid = new GridBase(rowCount, columnCount);
        ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
        
	        List<String> columnNames = table.getColumns().stream()
	    			.map(tc -> tc.getText()).collect(Collectors.toList());
	        final ObservableList<SpreadsheetCell> list1 = FXCollections.observableArrayList();
	        for (int col = 0; col < columnCount; col++)
	        	list1.add(SpreadsheetCellType.STRING.createCell(0, col, 1, 1,columnNames.get(col)));
	        rows.add(list1);
        
        ObservableList<T> items = table.getItems();
        int row = 1;
        for (T t: items) {
        	final ObservableList<SpreadsheetCell> list2 = FXCollections.observableArrayList();
            for (int col = 0; col < columnCount; col++) {
            	Object value = table.getColumns().get(col).getCellData(t);
            	if ( value instanceof String)
            		list2.add(SpreadsheetCellType.STRING.createCell(row, col, 1, 1, (String)value));
            	else if ( value instanceof Boolean) {
            		SpreadsheetCellBase cell = new SpreadsheetCellBase(row, col, 1, 1);
            		CheckBox checkBox = new CheckBox();
            		checkBox.setSelected((boolean)value);
            		cell.setGraphic(checkBox);
            		list2.add(cell);
            	}
            	else {
            		list2.add(SpreadsheetCellType.STRING.createCell(row, col, 1, 1, String.valueOf(value)));
                
                
            	}
            }
            rows.add(list2);
            row++;
        }
        grid.setRows(rows);
		        
        // create SpreadsheetView
        SpreadsheetView spreadsheet = new SpreadsheetView();
        
        spreadsheet.setGrid(grid);
        
        return spreadsheet; 
	}

}
