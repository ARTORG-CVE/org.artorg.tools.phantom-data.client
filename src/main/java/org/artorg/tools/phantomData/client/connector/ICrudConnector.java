package org.artorg.tools.phantomData.client.connector;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.artorg.tools.phantomData.client.exceptions.DeleteException;
import org.artorg.tools.phantomData.client.exceptions.NoUserLoggedInException;
import org.artorg.tools.phantomData.client.exceptions.PermissionDeniedException;
import org.artorg.tools.phantomData.client.exceptions.PostException;
import org.artorg.tools.phantomData.client.exceptions.PutException;
import org.artorg.tools.phantomData.server.model.Identifiable;

import javafx.collections.MapChangeListener;

public interface ICrudConnector<T> {

	Class<T> getItemClass();

	boolean create(T t) throws NoUserLoggedInException, PostException;

	<ID> T readById(ID id);

	boolean update(T t) throws NoUserLoggedInException, PutException, PermissionDeniedException;

	<ID> boolean deleteById(ID id)
			throws NoUserLoggedInException, DeleteException, PermissionDeniedException;

	T[] readAll();

	<ID, V> T readByAttribute(V attribute, String annString);

	<ID> Boolean existById(ID id);

	void reload();

	void addListener(MapChangeListener<String, T> listener);

	void removeListener(MapChangeListener<String, T> listener);

	default boolean create(List<T> t) throws NoUserLoggedInException, PostException {
		if (t == null) throw new PostException(getItemClass(), " item == null");
		for (T e : t)
			if (!create(e)) return false;
		return true;
	}

	default boolean create(T[] t) throws NoUserLoggedInException, PostException {
		if (t == null) throw new PostException(getItemClass(), " item == null");
		for (T e : t)
			if (!create(e)) return false;
		return true;
	}

	default T read(T t) {
		return readById(((Identifiable<?>) t).getId());
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

	default <U extends Identifiable<ID>, ID extends Comparable<ID>> boolean delete(U t)
			throws NoUserLoggedInException, DeleteException, PermissionDeniedException {
		return deleteById((ID) t.getId());
	}

	default <U extends Identifiable<ID>, ID extends Comparable<ID>> boolean delete(List<U> t)
			throws NoUserLoggedInException, DeleteException, PermissionDeniedException {
		for (U e : t)
			if (!delete(e)) return false;
		return true;
	}

	default boolean delete(T[] t)
			throws NoUserLoggedInException, DeleteException, PermissionDeniedException {
		for (T e : t)
			if (!deleteById(((Identifiable<?>) e).getId())) return false;
		return true;
	}

	default boolean update(List<T> t) throws NoUserLoggedInException, PutException, PermissionDeniedException {
		for (T e : t)
			if (!update(e)) return false;
		return true;
	}

	default boolean update(Set<T> t) throws NoUserLoggedInException, PutException, PermissionDeniedException {
		for (T e : t)
			if (!update(e)) return false;
		return true;
	}

	default boolean update(T[] t) throws NoUserLoggedInException, PutException, PermissionDeniedException {
		for (T e : t)
			if (!update(e)) return false;
		return true;
	}

	default boolean exist(T t) throws NoUserLoggedInException {
		if (t == null) return false;
		return existById(((Identifiable<?>) t).getId());
	}

}
