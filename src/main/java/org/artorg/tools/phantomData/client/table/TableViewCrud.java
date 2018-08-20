package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

public class TableViewCrud<TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
		ITEM extends DatabasePersistent<ITEM, ID_TYPE>, 
		ID_TYPE> implements TableGui<TABLE, ITEM , ID_TYPE> {
	
	private final TableView<ITEM> tableView;
	private Table<TABLE, ITEM, ID_TYPE> table;
	private List<Runnable> refreshListeners;
	
	{
		tableView = new TableView<ITEM>();
		refreshListeners = new ArrayList<Runnable>();
	}
	
	public void setTable(FilterTable<TABLE, ITEM, ID_TYPE> table) {
		this.table = table;
		reload();
	}
	
	public void setSortOrder() {
		tableView.getSortOrder().addAll(tableView.getColumns());
	}
	
	@Override
	public void autoResizeColumns() {
		tableView.setColumnResizePolicy( TableView.UNCONSTRAINED_RESIZE_POLICY);
	    tableView.getColumns().stream().forEach( (column) -> {
	        Text t = new Text( column.getText() );
	        double max = t.getLayoutBounds().getWidth();
	        for ( int i = 0; i < tableView.getItems().size(); i++ ) {
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

	public Table<TABLE, ITEM, ID_TYPE> getTable() {
		return table;
	}

	@Override
	public Control getGraphic() {
		return tableView;
	}

	@Override
	public void refresh() {
		tableView.getColumns().removeAll(tableView.getColumns());

	    // creating columns
	    List<TableColumn<ITEM,?>> columns = new ArrayList<TableColumn<ITEM,?>>();
		List<String> columnNames = table.getColumnNames();
		
		int nCols = table.getNcols();
		for ( int i=0; i<nCols; i++) {
			TableColumn<ITEM, String> column = new TableColumn<ITEM, String>(columnNames.get(i));
			int j = i;
		    column.setCellValueFactory(cellData -> new SimpleStringProperty(
		    		String.valueOf(table.getValue(cellData.getValue(), j))));
		    columns.add(column);
		}
	    tableView.getColumns().addAll(columns);
	    
	    // fill with items
	    ObservableList<ITEM> items = table.getItems();
	    tableView.setItems(items);
	    
	    // finishing
	    this.setSortOrder();
	    autoResizeColumns();
	}

	@Override
	public void reload() {
		table.readAllData();
		refresh();
	}
	
}
