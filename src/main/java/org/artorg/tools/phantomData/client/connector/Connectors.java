package org.artorg.tools.phantomData.client.connector;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.artorg.tools.phantomData.server.model.AbstractPersonifiedEntity;
import org.artorg.tools.phantomData.server.model.AbstractProperty;
import org.artorg.tools.phantomData.server.model.DbPersistent;
import org.artorg.tools.phantomData.server.models.base.person.AcademicTitle;
import org.artorg.tools.phantomData.server.models.base.person.Gender;
import org.artorg.tools.phantomData.server.models.base.person.Person;

public class Connectors {
	private static final Map<Class<?>, CrudConnector<?>> unpersonifiedConnectorsMap;
	protected static final Map<Class<?>, PersonifiedCrudConnector<?>> personifiedConnectorsMap;
	private static final Map<Class<?>, ICrudConnector<?>> wrappingConnectorsMap;

	static {
		unpersonifiedConnectorsMap = new HashMap<>();
		personifiedConnectorsMap = new HashMap<>();
		wrappingConnectorsMap = new HashMap<>();
	}
	
	@SuppressWarnings("unchecked")
	public static <T>
		ICrudConnector<T> get(Class<T> itemClass) {
		if (itemClass == AbstractProperty.class)
			return (ICrudConnector<T>) getOrCreateWrapper(itemClass);
		if (itemClass == AbstractPersonifiedEntity.class)
			return (ICrudConnector<T>) getOrCreateWrapper(itemClass);
		
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
		if (unpersonifiedConnectorsMap.containsKey(cls))
			return (ICrudConnector<T>) unpersonifiedConnectorsMap.get(cls);
		CrudConnector<T> connector = new CrudConnector(cls);
		unpersonifiedConnectorsMap.put(cls, connector);
		return connector;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> ICrudConnector<T>
		getOrCreatePersonified(Class<T> cls) {
		if (personifiedConnectorsMap.containsKey(cls))
			return (ICrudConnector<T>) personifiedConnectorsMap.get(cls);
		PersonifiedCrudConnector<T> connector =
			new PersonifiedCrudConnector(cls);
		personifiedConnectorsMap.put(cls, connector);
		return connector;
	}
	
	@SuppressWarnings("unchecked")
	private static ICrudConnector<?> getOrCreateWrapper(Class<?> cls) {
		if (wrappingConnectorsMap.containsKey(AbstractProperty.class))
			return (ICrudConnector<AbstractProperty<?,?>>) wrappingConnectorsMap.get(AbstractProperty.class);
		ICrudConnector<?> connector = new WrapperConnector<>(cls);
		wrappingConnectorsMap.put(AbstractProperty.class, connector);
		return connector;
	}

}
