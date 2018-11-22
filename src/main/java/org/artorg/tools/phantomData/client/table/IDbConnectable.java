package org.artorg.tools.phantomData.client.table;

import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public interface IDbConnectable<ITEM extends DbPersistent<ITEM,?>> {
	
	ICrudConnector<ITEM> getConnector();	

}
