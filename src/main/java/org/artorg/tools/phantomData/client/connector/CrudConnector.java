package org.artorg.tools.phantomData.client.connector;

import java.util.function.Function;

import org.artorg.tools.phantomData.server.specification.DbPersistent;
import org.artorg.tools.phantomData.server.specification.Identifiable;

public abstract class CrudConnector<T extends Identifiable<ID>, ID extends Comparable<ID>> implements ICrudConnector<T,ID> {
	public static Function<Class<?>,CrudConnector<?,?>> connectorGetter = null;
	
	@SuppressWarnings("unchecked")
	public static <ITEM extends DbPersistent<ITEM,?>> CrudConnector<ITEM, ?> getConnector(Class<?> itemClass) {
		CrudConnector<ITEM, ?> connector = (CrudConnector<ITEM, ?>) connectorGetter.apply(itemClass);
		return connector;
	}
	
}
