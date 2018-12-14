package org.artorg.tools.phantomData.client.select;

import org.artorg.tools.phantomData.client.scene.control.tableView.DbFilterTableView;
import org.artorg.tools.phantomData.client.table.DbTable;

public class DbFilterTableViewSelector<ITEM> extends DbFilterTableView<ITEM> {

	public DbFilterTableViewSelector(Class<ITEM> itemClass, DbTable<ITEM> table) {
		super(itemClass, table);
	}

}
