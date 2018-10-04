package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.scene.layout.AddableToAnchorPane;
import org.artorg.tools.phantomData.client.table.IDbTable;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.collections.ListChangeListener;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public abstract class TableView<ITEM extends DbPersistent<ITEM,?>, TABLE extends IDbTable<ITEM>> extends javafx.scene.control.TableView<ITEM> implements AddableToAnchorPane {
	private TABLE table;
	private ListChangeListener<ITEM> listenerChangedListenerRefresh;
	
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
	
	public abstract void initTable();
	
	public void setTable(TABLE table) {
		this.table = table;
		initTable();
		table.getItems().addListener(listenerChangedListenerRefresh);
	}
	
	public TABLE getTable() {
		return table;
	}
	
	public ICrudConnector<ITEM,?> getConnector() {
		return getTable().getConnector();
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
	
	public javafx.scene.control.TableView<ITEM> getGraphic() {
		return this;
	}
	
	public void addTo(AnchorPane pane) {
		FxUtil.addToAnchorPane(pane, this);
	}
	
	@Override
	public void refresh() {
		super.refresh();
	}
	
}
