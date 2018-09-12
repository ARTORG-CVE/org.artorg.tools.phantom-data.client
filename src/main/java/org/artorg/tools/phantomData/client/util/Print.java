package org.artorg.tools.phantomData.client.util;

import java.util.Properties;
import java.util.stream.Collectors;

public class Print {
	
	public static String toString(Properties properties) {
		return properties.entrySet().stream()
				.map(e -> e.getKey().toString() +" = " +e.getValue().toString())
				.collect(Collectors.joining("\n"));
	}

}
