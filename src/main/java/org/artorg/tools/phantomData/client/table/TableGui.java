package org.artorg.tools.phantomData.client.table;

import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.scene.Node;
import javafx.scene.control.Control;

public interface TableGui<TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
		ITEM extends DatabasePersistent<ITEM, ID_TYPE>, 
		ID_TYPE> {
	
	Control getGraphic();
	
	void autoResizeColumns();
	
	void setTable(FilterTable<TABLE, ITEM, ID_TYPE> table);
	
	void refresh();
	
	void reload();
	
}
