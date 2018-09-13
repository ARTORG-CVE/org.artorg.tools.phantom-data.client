package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.scene.control.table.FilterTable;
import org.artorg.tools.phantomData.client.scene.control.table.Table;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewCrud;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class MainTable<TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
ITEM extends DatabasePersistent<ID_TYPE>, 
ID_TYPE> extends TableViewCrud<TABLE, ITEM, ID_TYPE> {
	private FilterTable<TABLE, ITEM, ID_TYPE> table;
	
	public void setTable(FilterTable<TABLE, ITEM, ID_TYPE> table) {
		this.table = table;
		super.setTable(this.table);
	}
	
}