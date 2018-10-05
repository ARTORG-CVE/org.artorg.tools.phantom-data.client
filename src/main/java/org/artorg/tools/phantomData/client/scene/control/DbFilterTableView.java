package org.artorg.tools.phantomData.client.scene.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.client.table.IDbTable;
import org.artorg.tools.phantomData.client.table.IFilterTable;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

public class DbFilterTableView<ITEM extends DbPersistent<ITEM,?>, TABLE extends IDbTable<ITEM> & IFilterTable<ITEM>> extends DbTableView<ITEM,TABLE> {
	protected List<FilterMenuButton<ITEM>> filterMenuButtons;
	
	{
		super.setEditable(true);
		filterMenuButtons = new ArrayList<FilterMenuButton<ITEM>>();	
	}
	
	public void showFilterButtons() {
        for (Node n : super.lookupAll(".column-header > .label")) {
            if (n instanceof Label) {
            	Label label = (Label)n;
            	
            	String columnName = label.getText();
            	Optional<FilterMenuButton<ITEM>> filterMenuButton = filterMenuButtons.stream()
            			.filter(f -> f.getText().equals(columnName))
            			.findFirst();
            	if(filterMenuButton.isPresent()) {
            		filterMenuButton.get().prefWidthProperty().bind(label.widthProperty());
            		filterMenuButton.get().getStyleClass().add("filter-menu-button");
            		label.setGraphic(filterMenuButton.get());
            		label.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);	
            	}
            	
            }
        }
    }
	
	@Override
	public void initTable() {
		super.getColumns().removeAll(super.getColumns());

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
	    
		List<String> columnNames = getTable().getFilteredColumnNames();
		
		
		int nCols = getTable().getFilteredNcols();
		
		for ( int col=0; col<nCols; col++) {
			TableColumn<ITEM, String> column = new TableColumn<ITEM, String>(columnNames.get(col));
			column.setSortable(false);
			
			final int localCol = col;
			FilterMenuButton<ITEM> filterMenuButton = new FilterMenuButton<ITEM>();
			filterMenuButton.setText(columnNames.get(col));
			
			AbstractColumn<ITEM> filterTableColumn = getTable().getFilteredColumns().get(localCol);
			FilterColumn<ITEM> filterColumn = (FilterColumn<ITEM>)filterTableColumn;
			filterColumn.setSortComparatorQueue(getTable().getSortComparatorQueue());
			filterMenuButton.setColumn(filterColumn, () -> getTable().applyFilter()); 
			filterMenuButtons.add(filterMenuButton);
			
		    column.setCellValueFactory(cellData -> new SimpleStringProperty(
		    		String.valueOf(getTable().getFilteredValue(cellData.getValue(), localCol))));
		    columns.add(column);
		}
		
	    super.getColumns().addAll(columns);
	    super.setItems(getTable().getFilteredItems());
	    autoResizeColumns();
	    
	    Platform.runLater(() -> showFilterButtons());
	}
	
	@Override
	public void reload() {
		getTable().getItems().removeListener(getListenerChangedListenerRefresh());
		getTable().readAllData();
		super.setItems(getTable().getFilteredItems());
		getTable().getItems().addListener(getListenerChangedListenerRefresh());
		refresh();
	}
	
}