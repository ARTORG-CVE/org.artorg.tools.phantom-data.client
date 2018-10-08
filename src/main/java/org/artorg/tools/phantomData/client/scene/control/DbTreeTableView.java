package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.table.IDbTable;
import org.artorg.tools.phantomData.client.table.TableBase;
import org.artorg.tools.phantomData.server.model.Phantom;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class DbTreeTableView extends ProTreeTableView {
	
	public DbTreeTableView() {
		super();
	}
	
	public DbTreeTableView(Class<Phantom> itemClass) {
		super(itemClass);
	}
	
	
	@Override
	public void setTable(TableBase<Phantom> table) {
		super.setTable(table);
//		reload();
//		initTable();
	}
	
	@SuppressWarnings("unchecked")
	public void reload() {
		((IDbTable<Phantom>)getTable()).readAllData();
		super.setItems(getTable().getItems());
		refresh();
	}

}
