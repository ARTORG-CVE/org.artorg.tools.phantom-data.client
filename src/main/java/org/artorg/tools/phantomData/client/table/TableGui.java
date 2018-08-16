package org.artorg.tools.phantomData.client.table;

import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public interface TableGui<TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
		ITEM extends DatabasePersistent<ITEM, ID_TYPE>, 
		ID_TYPE> {
	
	javafx.scene.control.Control getGraphic();
	
	void autoResizeColumns();
	
	void setTable(Table<TABLE, ITEM, ID_TYPE> table);
	
	void refresh();
	
	void reload();
	
}
