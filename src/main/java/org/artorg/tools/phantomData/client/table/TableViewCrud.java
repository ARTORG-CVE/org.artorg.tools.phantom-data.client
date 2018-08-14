package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TableViewCrud<ITEM> {
	
	
	public List<TableColumn<ITEM,?>> createColumns() {
		List<TableColumn<ITEM,?>> columns = new ArrayList<TableColumn<ITEM,?>>();
		List<String> columnNames = createColumnNames();
		
		int nCols = getNumOfColumns();
		for ( int i=0; i<nCols; i++) {
			TableColumn<ITEM, String> column = new TableColumn<ITEM, String>(columnNames.get(i));
			int j = i;
		    column.setCellValueFactory(cellData -> new SimpleStringProperty(
		    		String.valueOf(getValue(cellData.getValue(), j))));
		    columns.add(column);
		}
		
	    return columns;
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
	
	public TableView<ITEM> createTableView(TableView<ITEM> table) {
		table.getColumns().removeAll(table.getColumns());

	    List<TableColumn<ITEM,?>> columns = createColumns();
	    table.getColumns().addAll(columns);
	    
	    Set<ITEM> itemSet = new HashSet<ITEM>();
		itemSet.addAll(getConnector().readAllAsSet());
	    ObservableList<ITEM> items = FXCollections.observableArrayList(itemSet);
	    ObservableList<ITEM> data = FXCollections.observableArrayList(items);
	    table.setItems(data);
	    
	    this.setSortOrder(table);
	    this.autoResizeColumns(table);
	    
		return table;
	}

}
