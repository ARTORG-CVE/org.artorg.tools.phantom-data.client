package org.artorg.tools.phantomData.client.connector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class Connectors {
	private static Map<Class<?>,HttpConnectorSpring<?>> connectorMap;
	
	static {
		List<Class<?>> classes = Reflect.getSubclasses(HttpConnectorSpring.class, "org.artorg.tools.phantomData.client.connectors");
		
		List<Method> methods = classes.stream().flatMap(c -> Arrays.asList(c.getMethods()).stream())
			.filter(m -> Reflect.isStatic(m))
			.filter(m -> HttpConnectorSpring.class.isAssignableFrom(m.getReturnType()))
			.collect(Collectors.toList());
		
		connectorMap = new HashMap<Class<?>, HttpConnectorSpring<?>>();
		
		methods.stream().forEach(m -> {
			try {
				HttpConnectorSpring<?> connector = (HttpConnectorSpring<?>) m.invoke(null);
				connectorMap.put(connector.getModelClass(), connector);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public static <ITEM extends DbPersistent<ITEM,ID>, ID> CrudConnectors<ITEM, ID> getConnector(Class<?> itemClass) {
		CrudConnectors<ITEM, ID> connector = (CrudConnectors<ITEM, ID>) connectorMap.get(itemClass);
		if (connector == null) throw new IllegalArgumentException();
		
		return connector;
	}

}
