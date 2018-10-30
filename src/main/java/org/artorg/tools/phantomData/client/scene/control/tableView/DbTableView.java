package org.artorg.tools.phantomData.client.scene.control.tableView;

import org.artorg.tools.phantomData.client.table.IDbTable;
import org.artorg.tools.phantomData.client.table.TableBase;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class DbTableView<ITEM extends DbPersistent<ITEM,?>> extends ProTableView<ITEM> {
	
	public DbTableView() {
		super();
	}
	
	public DbTableView(Class<ITEM> itemClass) {
		super(itemClass);
	}
	
	
	@Override
	public void setTable(TableBase<ITEM> table) {
		super.setTable(table);
//		reload();
//		initTable();
	}
	
	@SuppressWarnings("unchecked")
	public void reload() {
		getTable().getItems().removeListener(getListenerChangedListenerRefresh());
		((IDbTable<ITEM>)getTable()).readAllData();
		super.setItems(getTable().getItems());
		getTable().getItems().addListener(getListenerChangedListenerRefresh());
		refresh();
	}
	
	

}
