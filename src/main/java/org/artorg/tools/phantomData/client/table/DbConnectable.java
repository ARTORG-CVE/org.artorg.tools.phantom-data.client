package org.artorg.tools.phantomData.client.table;

import org.artorg.tools.phantomData.client.connector.CrudConnector;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public interface DbConnectable<ITEM extends DbPersistent<ITEM,?>> {
	
	CrudConnector<ITEM,?> getConnector();	
	
	void setConnector(CrudConnector<ITEM,?> connector);

}
