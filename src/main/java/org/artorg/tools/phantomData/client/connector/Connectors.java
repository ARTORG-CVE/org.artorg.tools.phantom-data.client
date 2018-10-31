package org.artorg.tools.phantomData.client.connector;

import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class Connectors {
	
	@SuppressWarnings("unchecked")
	public static <ITEM extends DbPersistent<ITEM,ID>, ID extends Comparable<ID>> CrudConnector<ITEM, ID> getConnector(Class<?> itemClass) {
		return (CrudConnector<ITEM, ID>) PersonalizedHttpConnectorSpring.getOrCreate(itemClass);
		
	}

}
