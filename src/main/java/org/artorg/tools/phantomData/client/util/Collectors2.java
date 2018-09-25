package org.artorg.tools.phantomData.client.util;

import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Collectors2 {
	
	
	public static <T> Collector<T, ?, T> toSingleton() {
	    return Collectors.collectingAndThen(
	            Collectors.toList(),
	            list -> {
	                if (list.size() != 1) {
	                    throw new IllegalStateException();
	                }
	                return list.get(0);
	            }
	    );
	}

}
