package org.artorg.tools.phantomData.client.scene.control.treeTableView;

import org.artorg.tools.phantomData.client.table.DbTable;
import org.artorg.tools.phantomData.client.table.TableBase;
import org.artorg.tools.phantomData.server.model.phantom.Phantom;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class DbTreeTableView<ITEM extends DbPersistent<ITEM,?>> extends ProTreeTableView<ITEM> {
	
	public DbTreeTableView(Class<ITEM> itemClass) {
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
		((DbTable<Phantom>)getTable()).readAllData();
		super.setItems(getTable().getItems());
		refresh();
	}

}
