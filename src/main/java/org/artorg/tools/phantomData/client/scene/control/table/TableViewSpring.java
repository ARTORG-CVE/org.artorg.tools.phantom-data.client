package org.artorg.tools.phantomData.client.scene.control.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.scene.control.FilterMenuButton;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.application.Platform;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public abstract class TableViewSpring<ITEM extends DatabasePersistent<ID_TYPE>, 
		ID_TYPE> extends TableView<ITEM> {
	
	private FilterTableSpringDb<ITEM, ID_TYPE> filterTable;
	private List<FilterMenuButton> filterMenuButtons;
	
	{
		super.setEditable(true);
		filterMenuButtons = new ArrayList<FilterMenuButton>();
	}
	
	public abstract AddEditController<ITEM, ID_TYPE> createAddEditController();
	
	public void setTable(FilterTableSpringDb<ITEM, ID_TYPE> table) {
		this.filterTable = table;
		reload();
	}
	
	public void autoResizeColumns() {
		super.setColumnResizePolicy( TableView.UNCONSTRAINED_RESIZE_POLICY);
	    super.getColumns().stream().forEach( (column) -> {
	        Text t = new Text( column.getText() );
	        double max = t.getLayoutBounds().getWidth()+45.0;
	        for ( int i = 0; i < super.getItems().size(); i++ ) {
	            if ( column.getCellData( i ) != null ) {
	                t = new Text( column.getCellData( i ).toString() );
	                double calcwidth = t.getLayoutBounds().getWidth()+10;
	                if ( calcwidth > max )
	                    max = calcwidth;
	            }
	        }
	        column.setPrefWidth( max);
	    } );
	}

	public FilterTableSpringDb<ITEM, ID_TYPE> getFilterTable() {
		return filterTable;
	}
	
	public Control getGraphic() {
		return this;
	}

	@Override
	public void refresh() {
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
	    
		List<String> columnNames = filterTable.getFilteredColumnNames();
		
		int nCols = filterTable.getFilteredNcols();
		for ( int col=0; col<nCols; col++) {
			TableColumn<ITEM, String> column = new TableColumn<ITEM, String>(columnNames.get(col));
			column.setSortable(false);
			
			final int localCol = col;
			FilterMenuButton filterMenuButton = new FilterMenuButton();
			filterMenuButton.setText(columnNames.get(col));
			filterMenuButton.setTable(filterTable, localCol, () -> filterTable.applyFilter()); 
			filterMenuButtons.add(filterMenuButton);
			
			column.setCellFactory(TextFieldTableCell.forTableColumn());
		    column.setCellValueFactory(cellData -> new SimpleStringProperty(
		    		String.valueOf(filterTable.getFilteredValue(cellData.getValue(), localCol))));
		    column.setOnEditCommit(
		    	    new EventHandler<CellEditEvent<ITEM, String>>() {
		    	        @Override
		    	        public void handle(CellEditEvent<ITEM, String> t) {
		    	        	ITEM item = ((ITEM) t.getTableView().getItems().get(
			    	                t.getTablePosition().getRow()));
		    	        	filterTable.setFilteredValue(item, localCol, t.getNewValue());
		    	        	System.out.println(filterTable.toString());
		    	        }
		    	    }
		    	);
		    columns.add(column);
		}
		
	    super.getColumns().addAll(columns);
	    ObservableList<ITEM> items = filterTable.getItems();
	    super.setItems(items);
	    autoResizeColumns();
	    
	    Platform.runLater(() -> showFilterButtons());
	}
	
	public void showFilterButtons() {
        for (Node n : super.lookupAll(".column-header > .label")) {
            if (n instanceof Label) {
            	Label label = (Label)n;
            	
            	String columnName = label.getText();
            	Optional<FilterMenuButton> filterMenuButton = filterMenuButtons.stream()
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
	
	public void reload() {
		filterTable.readAllData();
		refresh();
	}
	
	public void addTo(AnchorPane pane) {
		FxUtil.addToAnchorPane(pane, this);
	}
	
}
