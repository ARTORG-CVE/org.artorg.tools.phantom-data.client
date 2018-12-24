package org.artorg.tools.phantomData.client.scene.control.tableView;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.logging.Logger;
import org.artorg.tools.phantomData.client.table.DbTable;

import javafx.scene.control.SelectionMode;

public class DbTableView<ITEM> extends ProTableView<ITEM> {

	public DbTableView(Class<ITEM> itemClass) {
		this(itemClass, Main.getUIEntity(itemClass).createDbTableBase());
		
		super.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		getTable().readAllData();
		
		updateColumns();
		autoResizeColumns();
		
		if (!isFilterable()) super.setItems(getTable().getItems());
		else
			super.setItems(getTable().getFilteredItems());
		
	}

	protected DbTableView(Class<ITEM> itemClass, DbTable<ITEM> table) {
		super(itemClass, table);
	}

	@Override
	public DbTable<ITEM> getTable() {
		return (DbTable<ITEM>) super.getTable();
	}

	public void reload() {
		Logger.debug.println(getItemClass().getSimpleName());

		((DbTable<ITEM>) getTable()).reload();

		if (!isFilterable()) super.setItems(getTable().getItems());
		else
			super.setItems(getTable().getFilteredItems());

		refresh();
	}

}
