package org.artorg.tools.phantomData.client.specification;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.server.specification.DatabasePersistent;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public abstract class Table<TABLE extends Table<TABLE, ITEM, ID_TYPE>, ITEM extends DatabasePersistent<ITEM, ID_TYPE>, ID_TYPE> {
	
	private final ObservableList<ITEM> items;
	
	{
		Set<ITEM> itemSet = new HashSet<ITEM>();
		itemSet.addAll(getConnector().readAllAsSet());
		items = FXCollections.observableArrayList(itemSet);
	}
	
	public abstract List<TableColumn<ITEM,?>> createColumns();
	
	public ObservableList<ITEM> getItems() {
		return items;
	}
	
	public abstract HttpDatabaseCrud<ITEM, ID_TYPE> getConnector();
	
	public TableView<ITEM> createTableView(TableView<ITEM> table) {
		table.getColumns().removeAll(table.getColumns());

	    List<TableColumn<ITEM,?>> columns = createColumns();
	    table.getColumns().addAll(columns);
	    
	    ObservableList<ITEM> data = FXCollections.observableArrayList(getItems());
	    table.setItems(data);
	    
	    this.setSortOrder(table);
	    this.autoResizeColumns(table);
	    
		return table;
	}
	
	public void setSortOrder(TableView<ITEM> table) {
		table.getSortOrder().addAll(table.getColumns());
	}
	
	public void autoResize(TableView<ITEM> table, Stage stage) {
		double width = table.getColumns().stream().mapToDouble(c -> c.getPrefWidth()).sum();
		stage.setWidth(width + 17.0d + 50.0d);
	}
	
	public void autoResizeColumns(TableView<ITEM> table) {
	    table.setColumnResizePolicy( TableView.UNCONSTRAINED_RESIZE_POLICY);
	    table.getColumns().stream().forEach( (column) -> {
	        Text t = new Text( column.getText() );
	        double max = t.getLayoutBounds().getWidth();
	        for ( int i = 0; i < table.getItems().size(); i++ ) {
	            if ( column.getCellData( i ) != null ) {
	                t = new Text( column.getCellData( i ).toString() );
	                double calcwidth = t.getLayoutBounds().getWidth();
	                if ( calcwidth > max )
	                    max = calcwidth;
	            }
	        }
	        column.setPrefWidth( max + 35.0d );
	    } );
	}
	
	public TableView<ITEM> createTableView() {
		return createTableView(new TableView<ITEM>());
	}
	
	public SpreadsheetView createSpreadsheetView() {
		return createSpreadsheetView(createTableView());
	}
	
	public SpreadsheetView createSpreadsheetView(TableView<ITEM> table) {
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
        
        ObservableList<ITEM> items = table.getItems();
        int row = 1;
        for (ITEM t: items) {
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
