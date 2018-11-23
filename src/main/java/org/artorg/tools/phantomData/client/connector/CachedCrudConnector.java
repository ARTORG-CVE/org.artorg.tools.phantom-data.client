package org.artorg.tools.phantomData.client.connector;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.artorg.tools.phantomData.server.specification.Identifiable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class CachedCrudConnector<T extends Identifiable<?>> extends CrudConnector<T> {
	private final ObservableList<T> items;
	private final ObservableMap<String, T> map;

	{
		items = FXCollections.observableArrayList();
		map = FXCollections.observableHashMap();
	}

	public CachedCrudConnector(Class<T> itemClass) {
		super(itemClass);
	}

	@Override
	public ObservableList<T> readAllAsList() {
		return items;
	}

	@Override
	public boolean create(T t) {
		if (super.create(t)) {
			if (!map.containsKey(t.getId().toString())) {
				map.put(t.getId().toString(), t);
				items.add(t);
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U extends Identifiable<ID>, ID extends Comparable<ID>> U readById(ID id) {
		if (map.containsKey(id.toString())) return (U) map.get(id.toString());
		U u = super.readById(id);
		T t = (T) u;
		map.put(u.getId().toString(), t);
		items.add(t);
		return u;
	}

	@Override
	public <U extends Identifiable<ID>, ID extends Comparable<ID>> boolean deleteById(ID id) {
		if (super.deleteById(id)) {
			if (map.containsKey(id.toString())) {
				items.remove(map.get(id.toString()));
				map.remove(id.toString());
			}
			return true;
		}
		return false;
	}

	@Override
	public <U extends Identifiable<ID>, ID extends Comparable<ID>> Boolean
		existById(ID id) {
		if (map.containsKey(id.toString())) return true;
		return super.existById(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T[] readAll() {
		if (items.isEmpty()) reload();
		return (T[]) items.toArray(createGenericArray(getModelClass(), items.size()));
	}

	@SuppressWarnings("unchecked")
	private static <T> T[] createGenericArray(Class<T> itemClass, int length) {
		return (T[]) Array.newInstance(itemClass, length);
	}

	@SuppressWarnings("unchecked")
	public <U extends Identifiable<ID>, ID extends Comparable<ID>> boolean reload() {
		try {
			Arrays.stream(super.readAll()).forEach(dbItem -> {
				if (map.containsKey(dbItem.getId().toString())) {
					items.set(items.indexOf(map.get(dbItem.getId().toString())), dbItem);
					map.replace(dbItem.getId().toString(), dbItem);
				}
				else {
					map.put(dbItem.getId().toString(), dbItem);
					items.add(dbItem);
				}
			});

			List<String> removableIds = new ArrayList<String>();
			map.entrySet().stream().map(item -> (U) (item.getValue())).forEach(item -> {
				if (!super.exist((T) item)) removableIds.add(item.getId().toString());
			});
			removableIds.stream().forEach(id -> {
				items.remove(map.get(id));
				map.remove(id);
			});

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U extends Identifiable<ID>, ID extends Comparable<ID>, V> U
		readByAttribute(V attribute, String attributeName) {
		U u = super.readByAttribute(attribute, attributeName);
		if (map.containsKey(u.getId().toString()))
			return (U) map.get(u.getId().toString());
		map.put(u.getId().toString(), (T) u);
		return u;
	}

}
