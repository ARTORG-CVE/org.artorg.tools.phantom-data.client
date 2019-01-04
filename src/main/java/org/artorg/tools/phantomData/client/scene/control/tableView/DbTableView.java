package org.artorg.tools.phantomData.client.scene.control.tableView;

import org.artorg.tools.phantomData.client.logging.Logger;
import org.artorg.tools.phantomData.client.table.DbTable;

public class DbTableView<ITEM> extends ProTableView<ITEM> {
	
	public DbTableView(Class<ITEM> itemClass, DbTable<ITEM> table) {
		super(itemClass, table);
		getTable().readAllData();
		updateColumns();
	}

	@Override
	public DbTable<ITEM> getTable() {
		return (DbTable<ITEM>) super.getTable();
	}

	public void reload() {
		Logger.debug.println(getItemClass().getSimpleName());

		((DbTable<ITEM>) getTable()).reload();

		refresh();
	}

}
