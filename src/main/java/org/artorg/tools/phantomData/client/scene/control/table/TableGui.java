package org.artorg.tools.phantomData.client.scene.control.table;

import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.scene.control.Control;

public interface TableGui<TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
		ITEM extends DatabasePersistent<ID_TYPE>, 
		ID_TYPE> {
	
	void autoResizeColumns();
	
	void setTable(FilterTable<TABLE, ITEM, ID_TYPE> table);
	
	void refresh();
	
	void reload();
	
	Control getGraphic();
	
}
