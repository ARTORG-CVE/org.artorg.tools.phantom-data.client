package org.artorg.tools.phantomData.client.select;

import org.artorg.tools.phantomData.client.scene.control.tableView.DbFilterTableView;

public class DbFilterTableViewSelector<ITEM> extends DbFilterTableView<ITEM> {

	public DbFilterTableViewSelector(Class<ITEM> itemClass) {
		super(itemClass);
	}

}
