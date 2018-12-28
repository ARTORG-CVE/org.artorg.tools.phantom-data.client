package org.artorg.tools.phantomData.client.connector;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.artorg.tools.phantomData.server.model.DbPersistent;
import org.artorg.tools.phantomData.server.models.base.person.AcademicTitle;
import org.artorg.tools.phantomData.server.models.base.person.Gender;
import org.artorg.tools.phantomData.server.models.base.person.Person;

public class Connectors {
	private static final Map<Class<?>, CrudConnector<?>> connectorMapUnpersonified;
	protected static final Map<Class<?>, PersonifiedCrudConnector<?>> connectorMap;

	static {
		connectorMapUnpersonified = new HashMap<Class<?>, CrudConnector<?>>();
	}

	static {
		connectorMap = new HashMap<Class<?>, PersonifiedCrudConnector<?>>();
	}
	
	public static <T>
		ICrudConnector<T> get(Class<T> itemClass) {
		if (itemClass == Person.class || itemClass == Gender.class || itemClass == AcademicTitle.class)
			return (ICrudConnector<T>) getOrCreateUnpersonified(itemClass);
		return (ICrudConnector<T>) getOrCreatePersonified(itemClass);
	}
	
	@SuppressWarnings("unchecked")
	public static void createConnectors(Collection<Class<?>> itemClasses) {
		itemClasses.forEach(itemClass -> Connectors.get((Class<DbPersistent<?,?>>)itemClass));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> ICrudConnector<T>
		getOrCreateUnpersonified(Class<T> cls) {
		if (connectorMapUnpersonified.containsKey(cls))
			return (ICrudConnector<T>) connectorMapUnpersonified.get(cls);
		CrudConnector<T> connector = new CrudConnector(cls);
		connectorMapUnpersonified.put(cls, connector);
		return connector;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> ICrudConnector<T>
		getOrCreatePersonified(Class<T> cls) {
		if (connectorMap.containsKey(cls))
			return (ICrudConnector<T>) connectorMap.get(cls);
		PersonifiedCrudConnector<T> connector =
			new PersonifiedCrudConnector(cls);
		connectorMap.put(cls, connector);
		return connector;
	}

}
