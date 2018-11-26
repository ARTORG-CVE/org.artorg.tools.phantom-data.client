package org.artorg.tools.phantomData.client.connector;

import java.util.HashMap;
import java.util.Map;

import org.artorg.tools.phantomData.server.model.base.person.AcademicTitle;
import org.artorg.tools.phantomData.server.model.base.person.Gender;
import org.artorg.tools.phantomData.server.model.base.person.Person;
import org.artorg.tools.phantomData.server.specification.Identifiable;

public class Connectors {
	private static final Map<Class<?>, CrudConnector<?>> connectorMapUnpersonified;
	protected static final Map<Class<?>, PersonifiedCrudConnector<?>> connectorMap;

	static {
		connectorMapUnpersonified = new HashMap<Class<?>, CrudConnector<?>>();
	}

	static {
		connectorMap = new HashMap<Class<?>, PersonifiedCrudConnector<?>>();
	}
	
	public static <ITEM extends Identifiable<?>>
		ICrudConnector<ITEM> getConnector(Class<ITEM> itemClass) {
		if (itemClass == Person.class || itemClass == Gender.class || itemClass == AcademicTitle.class)
			return (ICrudConnector<ITEM>) getOrCreateUnpersonified(itemClass);
		return (ICrudConnector<ITEM>) getOrCreatePersonified(itemClass);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <U extends Identifiable<?>> ICrudConnector<U>
		getOrCreateUnpersonified(Class<U> cls) {
		if (connectorMapUnpersonified.containsKey(cls))
			return (ICrudConnector<U>) connectorMapUnpersonified.get(cls);
		CrudConnector<U> connector = new CrudConnector(cls);
		connectorMapUnpersonified.put(cls, connector);
		return connector;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <U extends Identifiable<?>> ICrudConnector<U>
		getOrCreatePersonified(Class<U> cls) {
		if (connectorMap.containsKey(cls))
			return (ICrudConnector<U>) connectorMap.get(cls);
		PersonifiedCrudConnector<U> connector =
			new PersonifiedCrudConnector(cls);
		connectorMap.put(cls, connector);
		return connector;
	}

}
