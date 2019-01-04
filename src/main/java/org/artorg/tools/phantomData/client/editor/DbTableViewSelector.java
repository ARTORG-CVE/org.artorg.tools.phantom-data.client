package org.artorg.tools.phantomData.client.editor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.util.CollectionUtil;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.model.AbstractProperty;
import org.artorg.tools.phantomData.server.model.Identifiable;

public class DbTableViewSelector<T, U> extends TableViewSelector<U> {
	private static final Map<Class<?>,
			Map<Class<?>, Function<Object, Collection<Object>>>> subItemGetterMap;
	private static final Map<Class<?>, Map<Class<?>, Boolean>> containsCollectionSetterMap;
	private final Class<T> parentItemClass;
	private final Class<U> subItemClass;

	static {
		subItemGetterMap = new HashMap<>();
		containsCollectionSetterMap = new HashMap<>();
	}

	public DbTableViewSelector(Class<T> parentItemClass, Class<U> subItemClass, ProTableView<U> tableView1, ProTableView<U> tableView2) {
		super(subItemClass, tableView1, tableView2);
		this.parentItemClass = parentItemClass;
		this.subItemClass = subItemClass;
		
	}

	public void setItems(Collection<U> selectedItems) {
		List<U> selectableItems = getSelectableItems(parentItemClass, (Class<U>) subItemClass);
		List<U> selectedItems2 = selectedItems.stream().collect(Collectors.toList());

		List<Integer> selectableItemsIndexes = CollectionUtil.searchLeftInRight(selectableItems,
				selectedItems2,
				(l, r) -> ((Identifiable<?>) l).getId().equals(((Identifiable<?>) r).getId()));
		selectedItems2 = CollectionUtil.subList(selectableItems, selectableItemsIndexes);
		selectableItems.removeAll(selectedItems);
		setSelectableItems(selectableItems);
		setSelectedItems(selectedItems);
	}

	@SuppressWarnings("unchecked")
	public void setItem(T item) {
		if (item == null) setItems(Collections.emptyList());

		Function<Object, Collection<Object>> subItemGetter =
				getSubItemGetter(parentItemClass, subItemClass);
		if (subItemGetter != null) setItems(subItemGetter.apply(item).stream()
				.filter(e -> e != null).map(e -> (U) e).collect(Collectors.toList()));
		else
			throw new RuntimeException();

	}

	@SuppressWarnings("unchecked")
	public static <T, U> List<U> getSelectableItems(Class<T> parentItemClass,
			Class<U> subItemClass) {
		ICrudConnector<U> connector = Connectors.get(subItemClass);
		List<U> items = connector.readAllAsList();
		if (AbstractProperty.class.isAssignableFrom(subItemClass)) {
			items = items.stream().filter(item -> {
				String simpleName =
						((AbstractProperty<U, ?>) item).getPropertyField().getEntityType();
				Class<?> entityType = Main.getEntityClassBySimpleName(simpleName);
				if (entityType == null) return false;
				if (entityType == parentItemClass) return true;
				return false;
			}).collect(Collectors.toList());
		}
		return items;
	}

	@SuppressWarnings("unchecked")
	private static Function<Object, Collection<Object>>
			createSubItemGetter(Class<?> parentItemClass, Class<?> subItemClass) {
		Method selectedMethod = Reflect.getMethodByGenericReturnType(parentItemClass, subItemClass);
		if (selectedMethod == null) return null;
		Function<Object, Collection<Object>> subItemGetter;
		subItemGetter = i -> {
			if (i == null) return null;
			try {
				return (Collection<Object>) (selectedMethod.invoke(i));
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		};
		return subItemGetter;
	}

	private static Function<Object, Collection<Object>> getSubItemGetter(Class<?> parentItemClass,
			Class<?> subItemClass) {
		Function<Object, Collection<Object>> subItemGetter = null;
		if (subItemGetterMap.containsKey(parentItemClass)) {
			if (subItemGetterMap.get(parentItemClass).containsKey(subItemClass))
				subItemGetter = subItemGetterMap.get(parentItemClass).get(subItemClass);
			else {
				subItemGetter = createSubItemGetter(parentItemClass, subItemClass);
				if (subItemGetter == null) return null;
				subItemGetterMap.get(parentItemClass).put(subItemClass, subItemGetter);
			}
		} else {
			subItemGetter = createSubItemGetter(parentItemClass, subItemClass);
			if (subItemGetter == null) return null;
			Map<Class<?>, Function<Object, Collection<Object>>> map = new HashMap<>();
			map.put(subItemClass, subItemGetter);
			subItemGetterMap.put(parentItemClass, map);
		}
		return subItemGetter;
	}

	public static boolean containsCollectionSetter(Class<?> parentItemClass,
			Class<?> subItemClass) {
		boolean result;
		if (containsCollectionSetterMap.containsKey(parentItemClass)) {
			if (containsCollectionSetterMap.get(parentItemClass).containsKey(subItemClass))
				result = containsCollectionSetterMap.get(parentItemClass).get(subItemClass);
			else {
				result = Reflect.containsCollectionSetter(parentItemClass, subItemClass);
				containsCollectionSetterMap.get(parentItemClass).put(subItemClass, result);
			}
		} else {
			result = Reflect.containsCollectionSetter(parentItemClass, subItemClass);
			Map<Class<?>, Boolean> map = new HashMap<>();
			map.put(subItemClass, result);
			containsCollectionSetterMap.put(parentItemClass, map);
		}
		return result;
	}

}
