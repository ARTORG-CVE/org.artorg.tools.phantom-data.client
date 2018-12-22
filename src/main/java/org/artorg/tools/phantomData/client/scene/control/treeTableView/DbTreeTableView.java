package org.artorg.tools.phantomData.client.scene.control.treeTableView;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.table.DbTable;

public class DbTreeTableView<T> extends ProTreeTableView<T> {
	
	public DbTreeTableView(Class<T> itemClass) {
		super(itemClass,  Main.getUIEntity(itemClass).createDbTableBase());
		
		initTable();
		setItems(getTable().getItems());
	}
	
	public void reload() {
		((DbTable<T>)getTable()).readAllData();
		super.setItems(getTable().getItems());
		refresh();
	}

}
