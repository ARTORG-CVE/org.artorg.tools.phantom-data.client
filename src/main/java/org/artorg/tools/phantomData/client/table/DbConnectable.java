package org.artorg.tools.phantomData.client.table;

import org.artorg.tools.phantomData.client.connector.CrudConnector;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public interface DbConnectable<ITEM extends DbPersistent<ITEM,ID>, ID> {
	
	CrudConnector<ITEM,ID> getConnector();	
	
	void setConnector(CrudConnector<ITEM,ID> connector);

}
