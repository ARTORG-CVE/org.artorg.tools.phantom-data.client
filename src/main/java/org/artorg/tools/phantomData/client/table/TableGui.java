package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.scene.Node;
import javafx.scene.control.Control;

public abstract class TableGui<TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
		ITEM extends DatabasePersistent<ITEM, ID_TYPE>, 
		ID_TYPE> {
	
	private List<Runnable> refreshListener;
	
	{
		refreshListener = new ArrayList<Runnable>();
	}
			
	abstract Control getGraphic();
	
	abstract void autoResizeColumns();
	
	abstract void setTable(FilterTable<TABLE, ITEM, ID_TYPE> table);
	
	public void refresh() {
		refreshListener.stream().forEach(l -> l.run());
	}
	
	abstract void reload();
	
	public void addRefreshListener(Runnable refresher) {
		this.refreshListener.add(refresher);
	}
	
}
