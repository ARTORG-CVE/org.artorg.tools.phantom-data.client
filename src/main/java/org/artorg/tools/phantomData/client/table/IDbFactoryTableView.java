package org.artorg.tools.phantomData.client.table;

import org.artorg.tools.phantomData.server.specification.DbPersistent;

public interface IDbFactoryTableView<ITEM extends DbPersistent<ITEM,?>> {
	
	FxFactory<ITEM> createFxFactory();
	
	
}
