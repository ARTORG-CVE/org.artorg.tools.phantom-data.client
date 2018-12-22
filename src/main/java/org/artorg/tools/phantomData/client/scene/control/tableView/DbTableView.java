package org.artorg.tools.phantomData.client.scene.control.tableView;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.table.DbTable;
import org.artorg.tools.phantomData.client.logging.Logger;

import javafx.application.Platform;
import javafx.scene.control.SelectionMode;

public class DbTableView<ITEM> extends ProTableView<ITEM> {

	public DbTableView(Class<ITEM> itemClass) {
		this(itemClass, Main.getUIEntity(itemClass).createDbTableBase());

		if (!isFilterable()) super.setItems(getTable().getItems());
		else
			super.setItems(getTable().getFilteredItems());

		super.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		getTable().readAllData();
		
		updateColumns();
		autoResizeColumns();
		
		getFilterMenuButtons().stream().forEach(column -> {
			column.updateNodes();
		});
		
		Platform.runLater(() -> {
			showFilterButtons();
			});

	}

	protected DbTableView(Class<ITEM> itemClass, DbTable<ITEM> table) {
		super(itemClass, table);
	}

	@Override
	public DbTable<ITEM> getTable() {
		return (DbTable<ITEM>) super.getTable();
	}

//	@Override
//	public void setTable(TableBase<ITEM> table) {
//		super.setTable(table);
////		reload();
////		initTable();
//	}

	public void reload() {
		Logger.debug.println(getItemClass().getSimpleName());
//		getTable().getItems().removeListener(getListenerChangedListenerRefresh());

		((DbTable<ITEM>) getTable()).reload();

		if (!isFilterable()) super.setItems(getTable().getItems());
		else
			super.setItems(getTable().getFilteredItems());
//		super.getItems().clear();
//		super.getItems().addAll(getTable().getItems());

//		getTable().getItems().addListener(getListenerChangedListenerRefresh());
		refresh();
	}

}
