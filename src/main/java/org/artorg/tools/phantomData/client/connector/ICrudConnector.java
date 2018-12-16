package org.artorg.tools.phantomData.client.connector;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.artorg.tools.phantomData.client.util.StreamUtils;
import org.artorg.tools.phantomData.server.specification.Identifiable;

public interface ICrudConnector<T> {
	
	boolean create(T t);
	
	<ID> T readById(ID id);
	
	boolean update(T t);
	
	<ID> boolean deleteById(ID id);
	
	T[] readAll();
	
	<ID, V> T readByAttribute(V attribute, String annString);
	
	<ID> Boolean existById(ID id);
	
	default boolean create(List<T> t) {
		return StreamUtils.forEach(this::create, t);
	}
	
	default boolean create(T[] t) {
		return StreamUtils.forEach(item -> create(item), t);
	}
	
	default T read(T t) {
		return readById(((Identifiable<?>)t).getId());
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
	
	default <U extends Identifiable<ID>, ID extends Comparable<ID>> boolean delete(U t) {
		return deleteById((ID)t.getId());
	}
	
	default <U extends Identifiable<ID>, ID extends Comparable<ID>> boolean delete(List<U> t) {
		return StreamUtils.forEach(this::delete, t);
	}
	
	default boolean delete(T[] t) {
		return StreamUtils.forEach(item -> deleteById(((Identifiable<?>)item).getId()), t);
	}
	
	default boolean update(List<T> t) {
		return StreamUtils.forEach(this::update, t);
	}
	
	default boolean update(Set<T> t) {
		return StreamUtils.forEach(this::update, t);
	}
	
	default boolean update(T[] t) {
		return StreamUtils.forEach(this::update, t);
	}
	
	default boolean exist(T t) {
		return existById(((Identifiable<?>)t).getId());
	}
	
}
