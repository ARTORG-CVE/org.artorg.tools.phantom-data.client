package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.table.IDbTable;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public abstract class DbTableView<ITEM extends DbPersistent<ITEM,?>, TABLE extends IDbTable<ITEM>> extends TableView<ITEM,TABLE> {
	
	@Override
	public void setTable(TABLE table) {
		super.setTable(table);
		reload();
		initTable();
	}
	
	public void reload() {
		getTable().getItems().removeListener(getListenerChangedListenerRefresh());
		getTable().readAllData();
		super.setItems(getTable().getItems());
		getTable().getItems().addListener(getListenerChangedListenerRefresh());
		refresh();
	}

}
