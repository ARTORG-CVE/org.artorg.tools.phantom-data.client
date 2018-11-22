package org.artorg.tools.phantomData.client.connector;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.artorg.tools.phantomData.client.util.StreamUtils;
import org.artorg.tools.phantomData.server.specification.Identifiable;

public interface ICrudConnector<T extends Identifiable<ID>, ID extends Comparable<ID>> {
	
	boolean create(T t);
	
	T read(ID id);
	
	boolean update(T t);
	
	boolean delete(ID id);
	
	T[] readAll();
	
	<V> T readByAttribute(V attribute, String annString);
	
	Boolean existById(ID id);
	
	default boolean create(List<T> t) {
		return StreamUtils.forEach(this::create, t);
	}
	
	@SuppressWarnings("unchecked")
	default boolean create(T... t) {
		return StreamUtils.forEach(this::create, t);
	}
	
	default T read(T t) {
		return read(t.getId());
	}
	
	default List<T> readAllAsList() {
		return Arrays.asList(readAll());
	}
	
	default Set<T> readAllAsSet() {
		Set<T> set = new HashSet<T>();
		Collections.addAll(set, readAll());
		return set;
	}
	
	default Stream<T> readAllAsStream() {
		return Arrays.stream(readAll());
	}
	
	default boolean delete(T t) {
		return delete((ID)t.getId());
	}
	
	default boolean delete(List<T> t) {
		return StreamUtils.forEach(this::delete, t);
	}
	
	@SuppressWarnings("unchecked")
	default boolean delete(T... t) {
		return StreamUtils.forEach(this::delete, t);
	}
	
	default boolean update(List<T> t) {
		return StreamUtils.forEach(this::update, t);
	}
	
	default boolean update(Set<T> t) {
		return StreamUtils.forEach(this::update, t);
	}
	
	@SuppressWarnings("unchecked")
	default boolean update(T... t) {
		return StreamUtils.forEach(this::update, t);
	}
	
	default boolean existById(T t) {
		return existById(t.getId());
	}
	
}
