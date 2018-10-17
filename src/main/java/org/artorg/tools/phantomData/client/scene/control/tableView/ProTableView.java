package org.artorg.tools.phantomData.client.scene.control.tableView;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.layout.AddableToAnchorPane;
import org.artorg.tools.phantomData.client.table.TableBase;
import org.artorg.tools.phantomData.server.util.Reflect;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.text.Text;

public class ProTableView<ITEM> extends javafx.scene.control.TableView<ITEM> implements AddableToAnchorPane {
	private final Class<ITEM> itemClass;
	private TableBase<ITEM> table;
	private ListChangeListener<ITEM> listenerChangedListenerRefresh;
	
	
	@SuppressWarnings("unchecked")
	public ProTableView() {
		itemClass = (Class<ITEM>) Reflect.findSubClassParameterType(this, ProTableView.class, 0);
	}
	
	public ProTableView(Class<ITEM> itemClass) {
		this.itemClass = itemClass;
	}
	
	public void removeHeaderRow() {
		try {
			this.getStyleClass().add("noheader");
		} catch(Exception e) {
			this.setStyle("-fx-max-height: 0; -fx-pref-height: 0; -fx-min-height: 0;");
		}
	}
	
	
	public ListChangeListener<ITEM> getListenerChangedListenerRefresh() {
		return listenerChangedListenerRefresh;
	}

	public void setListenerChangedListenerRefresh(ListChangeListener<ITEM> listenerChangedListenerRefresh) {
		this.listenerChangedListenerRefresh = listenerChangedListenerRefresh;
	}

	{
		listenerChangedListenerRefresh = new ListChangeListener<ITEM>() {
			@Override
			public void onChanged(Change<? extends ITEM> c) {
				refresh();
			}
		};
	}
	
	public void initTable() {
		super.getColumns().removeAll(super.getColumns());

	    // creating columns
	    List<TableColumn<ITEM,?>> columns = new ArrayList<TableColumn<ITEM,?>>();
		List<String> columnNames = table.getColumnNames();
		
		int nCols = table.getNcols();
		for ( int col=0; col<nCols; col++) {
			TableColumn<ITEM, String> column = new TableColumn<ITEM, String>(columnNames.get(col));
			column.setSortable(false);
			
			final int localCol = col;
		    column.setCellValueFactory(cellData -> new SimpleStringProperty(
		    		String.valueOf(table.getValue(cellData.getValue(), localCol))));
		    columns.add(column);
		}
		
	    super.getColumns().addAll(columns);
	    super.setItems(table.getItems());
	    autoResizeColumns();
	    super.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}
	
	protected TableColumn<ITEM,String> createHeaderColumn() {
		TableColumn<ITEM,String> headerColumn = new TableColumn<ITEM,String>();
	    headerColumn.setCellFactory(col -> {
	    	TableCell<ITEM,String> cell = new TableCell<ITEM,String>();
	        cell.getStyleClass().add("row-header-cell");
	        return cell ;
	    });
	    headerColumn.setSortable(false);
	    return headerColumn;
	}
	
	public void setTable(TableBase<ITEM> table) {
		this.table = table;
		initTable();
		table.getItems().addListener(listenerChangedListenerRefresh);
	}
	
	public TableBase<ITEM> getTable() {
		return table;
	}
	
	public void autoResizeColumns() {
		super.setColumnResizePolicy(ProTableView.UNCONSTRAINED_RESIZE_POLICY);
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
	
	public javafx.scene.control.TableView<ITEM> getGraphic() {
		return this;
	}
	
	@Override
	public void refresh() {
		super.refresh();
	}
	
	public Class<ITEM> getItemClass() {
		return itemClass;
	}

	
}
