package org.artorg.tools.phantomData.client.table;

import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;

import javafx.scene.Node;

public interface FxFactory<ITEM> {
	
	Node getGraphic();
	
	void setTableView(ProTableView<ITEM> table);
	
	Node create(ITEM item, Class<?> itemClass);
	
	Node edit(ITEM item, Class<?> itemClass);

	Node create(Class<?> itemClass);

}
