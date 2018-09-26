package org.artorg.tools.phantomData.client.connector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.util.Reflect;

public class Connectors2 {
	private static Map<Class<?>,HttpConnectorSpring2<?>> connectorMap;
	
	static {
		List<Class<?>> classes = Reflect.getSubclasses(HttpConnectorSpring2.class, "org.artorg.tools.phantomData.client.connectors");
		
		List<Method> methods = classes.stream().flatMap(c -> Arrays.asList(c.getMethods()).stream())
			.filter(m -> Reflect.isStatic(m))
			.filter(m -> HttpConnectorSpring2.class.isAssignableFrom(m.getReturnType()))
			.collect(Collectors.toList());
		
		connectorMap = new HashMap<Class<?>, HttpConnectorSpring2<?>>();
		
		methods.stream().forEach(m -> {
			try {
				HttpConnectorSpring2<?> connector = (HttpConnectorSpring2<?>) m.invoke(null);
				connectorMap.put(connector.getModelClass(), connector);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public static <ITEM> HttpConnectorSpring2<ITEM> getConnector(Class<ITEM> itemClass) {
		HttpConnectorSpring2<ITEM> connector = (HttpConnectorSpring2<ITEM>) connectorMap.get(itemClass);
		if (connector == null) throw new IllegalArgumentException();
		
		return connector;
	}

}
