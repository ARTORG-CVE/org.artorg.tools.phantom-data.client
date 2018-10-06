package org.artorg.tools.phantomData.client.table;

import org.artorg.tools.phantomData.client.scene.control.ProTableView;

import javafx.scene.Node;

public interface FxFactory<ITEM> {
	
	Node getGraphic();
	
	void setTableView(ProTableView<ITEM> table);
	
	Node create(ITEM item);
	
	Node edit(ITEM item);

	Node create();

}
