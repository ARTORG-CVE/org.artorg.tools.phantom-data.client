package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.table.IDbTable;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public abstract class DbTableView<ITEM extends DbPersistent<ITEM,?>, TABLE_TYPE extends IDbTable<ITEM>> extends TableView<ITEM,TABLE_TYPE> {
	
	@Override
	public void setTable(TABLE_TYPE table) {
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
