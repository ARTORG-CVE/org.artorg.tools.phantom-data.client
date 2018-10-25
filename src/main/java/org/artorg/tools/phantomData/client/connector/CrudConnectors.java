package org.artorg.tools.phantomData.client.connector;

import java.util.function.Function;

import org.artorg.tools.phantomData.server.specification.DbPersistent;
import org.artorg.tools.phantomData.server.specification.Identifiable;

public abstract class CrudConnectors<T extends Identifiable<ID>, ID extends Comparable<ID>> implements ICrudConnector<T,ID> {

	public static Function<Class<?>,CrudConnectors<?,?>> connectorGetter = null;
	
	@SuppressWarnings("unchecked")
	public static <ITEM extends DbPersistent<ITEM,?>> CrudConnectors<ITEM, ?> getConnector(Class<?> itemClass) {
		CrudConnectors<ITEM, ?> connector = (CrudConnectors<ITEM, ?>) connectorGetter.apply(itemClass);
		return connector;
	}
	
}
