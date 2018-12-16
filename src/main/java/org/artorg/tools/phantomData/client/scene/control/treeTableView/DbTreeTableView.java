package org.artorg.tools.phantomData.client.scene.control.treeTableView;

import org.artorg.tools.phantomData.client.table.DbTable;
import org.artorg.tools.phantomData.client.table.TableBase;
import org.artorg.tools.phantomData.server.model.phantom.Phantom;

public class DbTreeTableView<T> extends ProTreeTableView<T> {
	
	public DbTreeTableView(Class<T> itemClass) {
		super(itemClass);
	}
	
	
	@Override
	public void setTable(TableBase<T> table) {
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
