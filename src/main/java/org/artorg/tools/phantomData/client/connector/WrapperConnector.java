package org.artorg.tools.phantomData.client.connector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.util.CollectionUtil;

import javafx.collections.MapChangeListener;

public class WrapperConnector<T> implements ICrudConnector<T> {
	private final Class<T> superClass;
	private final List<ICrudConnector<?>> propertyConnectors;
	
	public WrapperConnector(Class<T> superClass) {
		this.superClass = superClass; 
		propertyConnectors = new ArrayList<>();
		List<Class<?>> propertyClasses = Main.getEntityClasses().stream()
				.filter(cls -> superClass.isAssignableFrom(cls))
				.collect(Collectors.toList());
		propertyConnectors.addAll(propertyClasses.stream().map(cls -> Connectors.get(cls))
				.collect(Collectors.toList()));
	}

	@SuppressWarnings("unchecked")
	private ICrudConnector<T> getConnector(Class<?> propertyClass) {
		return (ICrudConnector<T>) Connectors.get(propertyClass);
	}

	private List<ICrudConnector<?>> getConnectors() {
		return propertyConnectors;
	}

	@Override
	public boolean create(T t) {
		return getConnector(t.getClass()).create(t);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <ID> T readById(ID id) {
		for (ICrudConnector<?> connector : getConnectors())
			if (connector.existById(id)) return (T) connector.readById(id);
		return null;
	}

	@Override
	public boolean update(T t) {
		return getConnector(t.getClass()).update(t);
	}

	@Override
	public <ID> boolean deleteById(ID id) {
		for (ICrudConnector<?> connector : getConnectors())
			if (connector.existById(id)) return connector.deleteById(id);
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T[] readAll() {
		List<Object> items = new ArrayList<>();
		for (ICrudConnector<?> connector : getConnectors())
			items.addAll(connector.readAllAsList());
		
		T[] array = CollectionUtil.createGenericArray(superClass, items.size());
		for (int i = 0; i < array.length; i++)
			array[i] = (T) items.get(i);
		return array;
	}

	@Override
	public <ID, V> T readByAttribute(V attribute, String annString) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <ID> Boolean existById(ID id) {
		for (ICrudConnector<?> connector : getConnectors())
			if (connector.existById(id)) return true;
		return false;
	}

	@Override
	public void reload() {
		for (ICrudConnector<?> connector : getConnectors())
			connector.reload();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addListener(MapChangeListener<String, T> listener) {
		for (ICrudConnector connector : getConnectors())
			connector.addListener(listener);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void removeListener(MapChangeListener<String, T> listener) {
		for (ICrudConnector connector : getConnectors())
			connector.removeListener(listener);
	}

}
