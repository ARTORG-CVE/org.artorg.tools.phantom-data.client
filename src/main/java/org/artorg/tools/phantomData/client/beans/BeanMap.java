package org.artorg.tools.phantomData.client.beans;

import java.util.HashMap;
import java.util.Map;

public class BeanMap {
	private final Map<Class<?>, PersistentIntrospector> map;

	{
		map = new HashMap<Class<?>, PersistentIntrospector>();
	}
	
	public boolean addBeanClass(Class<?> cls) {
		if (!map.containsKey(cls)) {
			map.put(cls, new PersistentIntrospector(cls));
			return true;
		}
		return false;
	}
	
	public PersistentIntrospector getIntrospector(Class<?> cls) {
		return map.get(cls);
	}
	
	public Map<Class<?>, PersistentIntrospector> getMap() {
		return map;
	}
	
}
