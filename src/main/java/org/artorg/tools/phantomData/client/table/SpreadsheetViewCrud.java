package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class SpreadsheetViewCrud {
	
	
	
	public SpreadsheetView createSpreadsheetView(TableView<ITEM> table) {
		// create Grid
		int rowCount = table.getItems().size()+1;
        int columnCount = table.getColumns().size();
        GridBase grid = new GridBase(rowCount, columnCount);
        
        
        List<String> columnNames = table.getColumns().stream()
    			.map(tc -> tc.getText()).collect(Collectors.toList());
        final ObservableList<SpreadsheetCell> list1 = FXCollections.observableArrayList();
        
        List<Object> testList = new ArrayList<Object>();
        ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
        
        for (int col = 0; col < columnCount; col++) {
        	list1.add(SpreadsheetCellType.STRING.createCell(0, col, 1, 1,columnNames.get(col)));
        	testList.add(columnNames.get(col));
        }
        rows.add(list1);
        data.add(testList);
        
        
        
        Integer row=0;
        while (row<items.size()) {
        	final ObservableList<SpreadsheetCell> list2 = FXCollections.observableArrayList();
        	List<Object> testList2 = new ArrayList<Object>();
        	
            for (int col = 0; col < columnCount; col++) {
            	Object value = table.getColumns().get(col).getCellData(items.get(row));
            	if ( value instanceof String) {
            		SpreadsheetCellBase cell = new SpreadsheetCellBase(row, col, 1, 1);
            		TextField label = new TextField();
            		
            		label.setText((String)value);
            		testList2.add((String)value);
            		
            		final int tempRow = row;
            		final int tempCol = col;
            		label.setOnAction((event) -> {
            			data.get(tempRow+1).set(tempCol, label.getText());
            			this.setValue(items.get(tempRow), tempCol, label.getText());
            			data.stream().flatMap(l -> l.stream()).forEach(System.out::println);
            			properties.get(tempCol).set(items.get(tempRow), label.getText());
            			
            			System.out.println("--//--//--//--// items in Table start");
            			items.stream().forEach(System.out::println);
            			System.out.println("--//--//--//--// items in Table end");
            			
            			System.out.println("--//--//--//--// data in Table start");
            			data.stream().forEach(l -> l.stream().forEach(System.out::println));
            			System.out.println("--//--//--//--// data in Table end");
            		});
            		cell.setGraphic(label);
            		list2.add(cell);
            	}
//            	else if ( value instanceof Boolean) {
//            		SpreadsheetCellBase cell = new SpreadsheetCellBase(row, col, 1, 1);
//            		CheckBox checkBox = new CheckBox();
//            		checkBox.setSelected((boolean)value);
//            		cell.setGraphic(checkBox);
//            		list2.add(cell);
//            	}
//            	else {
//            		list2.add(SpreadsheetCellType.STRING.createCell(row, col, 1, 1, String.valueOf(value)));
//                
//                
//            	}
            }
            rows.add(list2);
            data.add(testList2);
            row++;
        }
        grid.setRows(rows);
		        
        // create SpreadsheetView
        SpreadsheetView spreadsheet = new SpreadsheetView();
        
        spreadsheet.setGrid(grid);
        
        return spreadsheet; 
	}
	
	public SpreadsheetView createSpreadsheetView() {
		return createSpreadsheetView(createTableView());
	}
	
	public void createData() { 
        int nCols = getNumOfColumns();
        data.add(createColumnNames().stream().map(s -> (Object)s)
        		.collect(Collectors.toList()));
        
        List<ITEM> items = new ArrayList<ITEM>();
		items.addAll(getConnector().readAllAsList());
        
        for (int row=1; row<items.size(); row++) {
        	List<Object> testList2 = new ArrayList<Object>();
            for (int col = 0; col < nCols; col++) {
            	Object value = getValue(items.get(row), col);
            	if ( value instanceof String) 
            		testList2.add((String)value);
            }
            data.add(testList2);
        }

	}

}
