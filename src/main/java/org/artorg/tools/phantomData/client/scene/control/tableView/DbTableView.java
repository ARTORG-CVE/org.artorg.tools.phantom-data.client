package org.artorg.tools.phantomData.client.scene.control.tableView;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.logging.Logger;
import org.artorg.tools.phantomData.client.table.DbTable;

import javafx.scene.control.SelectionMode;

public class DbTableView<ITEM> extends ProTableView<ITEM> {

	public DbTableView(Class<ITEM> itemClass) {
		this(itemClass, Main.getUIEntity(itemClass).createDbTableBase());

		long startTime = System.currentTimeMillis();
		if (!isFilterable()) super.setItems(getTable().getItems());
		else
			super.setItems(getTable().getFilteredItems());
		Logger.debug.println(String.format("%s - Putted items on table in %d ms",
				itemClass.getSimpleName(), System.currentTimeMillis() - startTime));

		super.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		getTable().readAllData();
		updateColumns();
		autoResizeColumns();

		startTime = System.currentTimeMillis();
		getFilterMenuButtons().stream().forEach(column -> {
			column.updateNodes();
		});
		Logger.debug.println(String.format("%s - Updated filter item nodes %d ms",
				itemClass.getSimpleName(), System.currentTimeMillis() - startTime));

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
//		getTable().getItems().removeListener(getListenerChangedListenerRefresh());

		((DbTable<ITEM>) getTable()).reload();

		if (!isFilterable()) super.setItems(getTable().getItems());
		else
			super.setItems(getTable().getFilteredItems());

//		getTable().getItems().addListener(getListenerChangedListenerRefresh());
		refresh();
	}

}
