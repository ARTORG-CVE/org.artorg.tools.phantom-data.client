package org.artorg.tools.phantomData.client.scene.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.artorg.tools.phantomData.client.table.FilterableTable;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.client.util.TableViewUtils;
import org.artorg.tools.phantomData.server.specification.DbPersistentUUID;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

public class TableViewReadOnly<ITEM> extends TableView<ITEM> {	
	private FilterableTable<ITEM> filterTable;
	private List<FilterMenuButton> filterMenuButtons;
	
	public List<FilterMenuButton> getFilterMenuButtons() {
		return filterMenuButtons;
	}

	{
		super.setEditable(true);
		filterMenuButtons = new ArrayList<FilterMenuButton>();
	}
	
	public void setTable(FilterableTable<ITEM> table) {
		this.filterTable = table;

		reload();
		initTable();
	}
	
	private void initTable() {
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
			
		    column.setCellValueFactory(cellData -> new SimpleStringProperty(
		    		String.valueOf(filterTable.getFilteredValue(cellData.getValue(), localCol))));
		    columns.add(column);
		}
		
	    super.getColumns().addAll(columns);
	    super.setItems(filterTable.getItems());
	    TableViewUtils.autoResizeColumns(this);
	    
	    Platform.runLater(() -> showFilterButtons());
	}

	public FilterableTable<ITEM> getFilterTable() {
		return filterTable;
	}
	
	public Control getGraphic() {
		return this;
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
		super.setItems(filterTable.getItems());
	}
	
	public void addTo(AnchorPane pane) {
		FxUtil.addToAnchorPane(pane, this);
	}
	
}
