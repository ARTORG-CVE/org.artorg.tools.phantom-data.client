package org.artorg.tools.phantomData.client.table;

import org.artorg.tools.phantomData.client.scene.control.DbTableView;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.scene.Node;

public interface FxFactory<ITEM extends DbPersistent<ITEM,?>> {
	
	Node getGraphic();
	
	void setTable(DbTableView<ITEM,?> table);

}
