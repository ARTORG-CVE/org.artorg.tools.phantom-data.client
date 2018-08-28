package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.artorg.tools.phantomData.client.table.control.FilterMenuButton;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;

public class TableViewCrud<TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
		ITEM extends DatabasePersistent<ITEM, ID_TYPE>, 
		ID_TYPE> extends TableGui<TABLE, ITEM , ID_TYPE> {
	
	private final TableView<ITEM> tableView;
	private FilterTable<TABLE, ITEM, ID_TYPE> table;
	private List<FilterMenuButton> filterMenuButtons;
	
	{
		tableView = new TableView<ITEM>();
		tableView.setEditable(true);
		filterMenuButtons = new ArrayList<FilterMenuButton>();
	}
	
	public void setTable(FilterTable<TABLE, ITEM, ID_TYPE> table) {
		this.table = table;
		reload();
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
	    
	    TableColumn<ITEM,String> headerColumn = new TableColumn<ITEM,String>();
	    headerColumn.setCellFactory(col -> {
	    	TableCell<ITEM,String> cell = new TableCell<ITEM,String>();
	        cell.getStyleClass().add("row-header-cell");
	        return cell ;
	    });
	    headerColumn.setSortable(false);
	    columns.add(headerColumn);
	    
		List<String> columnNames = table.getFilteredColumnNames();
		
		int nCols = table.getFilteredNcols();
		for ( int col=0; col<nCols; col++) {
			TableColumn<ITEM, String> column = new TableColumn<ITEM, String>(columnNames.get(col));
			column.setSortable(false);
			
			
			final int localCol = col;
			FilterMenuButton filterMenuButton = new FilterMenuButton();
			filterMenuButton.setText(columnNames.get(col));
			filterMenuButton.setTable(table, localCol, () -> {
				table.applyFilter();
			}); 
			filterMenuButtons.add(filterMenuButton);
			
			column.setCellFactory(TextFieldTableCell.forTableColumn());
			
//			column.setCellValueFactory(cellData -> new PropertyValueFactory<ITEM,String>(
////		    		String.valueOf(table.getFilteredValue(cellData.getValue(), j))));
			
		    column.setCellValueFactory(cellData -> new SimpleStringProperty(
		    		String.valueOf(table.getFilteredValue(cellData.getValue(), localCol))));
		    column.setOnEditCommit(
		    	    new EventHandler<CellEditEvent<ITEM, String>>() {
		    	        @Override
		    	        public void handle(CellEditEvent<ITEM, String> t) {
		    	        	ITEM item = ((ITEM) t.getTableView().getItems().get(
			    	                t.getTablePosition().getRow()));
		    	        	table.setFilteredValue(item, localCol, t.getNewValue());
		    	        }
		    	    }
		    	);
		    columns.add(column);
		}
		
	    tableView.getColumns().addAll(columns);
	    ObservableList<ITEM> items = table.getItems();
	    tableView.setItems(items);
	    autoResizeColumns();
	    super.refresh();
	}
	
	public void showFilterButtons() {
        for (Node n : tableView.lookupAll(".column-header > .label")) {
            if (n instanceof Label) {
            	Label parent = (Label)n;
            	String columnName = parent.getText();
            	Optional<FilterMenuButton> filterMenuButton = filterMenuButtons.stream()
            			.filter(f -> f.getText().equals(columnName))
            			.findFirst();
            	if(filterMenuButton.isPresent()) {
            		parent.setGraphic(filterMenuButton.get());
            		filterMenuButton.get().textProperty().bind(parent.textProperty());
            		filterMenuButton.get().getStyleClass().add("filter-menu-button");
            	}
            	parent.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
        }
    }
	
	
	@Override
	public void reload() {
		table.readAllData();
		refresh();
	}
	
}
