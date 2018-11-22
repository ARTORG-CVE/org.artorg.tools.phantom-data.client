package org.artorg.tools.phantomData.client.connector;

import java.util.HashMap;
import java.util.Map;

import org.artorg.tools.phantomData.server.model.base.person.Gender;
import org.artorg.tools.phantomData.server.model.base.person.Person;
import org.artorg.tools.phantomData.server.specification.Identifiable;

public class Connectors {
	private static final Map<Class<?>, HttpConnectorSpring<?,?>> connectorMapUnpersonified;
	protected static final Map<Class<?>, PersonalizedHttpConnectorSpring<?,?>> connectorMap;

	static {
		connectorMapUnpersonified = new HashMap<Class<?>, HttpConnectorSpring<?,?>>();
	}

	static {
		connectorMap = new HashMap<Class<?>, PersonalizedHttpConnectorSpring<?,?>>();
	}
	
	public static <ITEM extends Identifiable<?>>
		ICrudConnector<ITEM, ?> getConnector(Class<ITEM> itemClass) {
		if (itemClass == Person.class || itemClass == Gender.class)
			return (ICrudConnector<ITEM, ?>) getOrCreateUnpersonified(itemClass);
		return (ICrudConnector<ITEM, ?>) getOrCreatePersonified(itemClass);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <U extends Identifiable<?>> ICrudConnector<U,?>
		getOrCreateUnpersonified(Class<U> cls) {
		if (connectorMapUnpersonified.containsKey(cls))
			return (ICrudConnector<U,?>) connectorMapUnpersonified.get(cls);
		HttpConnectorSpring<U,?> connector = new HttpConnectorSpring(cls);
		connectorMapUnpersonified.put(cls, connector);
		return connector;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <U extends Identifiable<?>> ICrudConnector<U,?>
		getOrCreatePersonified(Class<U> cls) {
		if (connectorMap.containsKey(cls))
			return (ICrudConnector<U,?>) connectorMap.get(cls);
		PersonalizedHttpConnectorSpring<U,?> connector =
			new PersonalizedHttpConnectorSpring(cls);
		connectorMap.put(cls, connector);
		return connector;
	}

}
