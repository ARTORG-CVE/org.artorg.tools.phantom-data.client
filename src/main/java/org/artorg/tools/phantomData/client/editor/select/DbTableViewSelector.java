package org.artorg.tools.phantomData.client.editor.select;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.util.CollectionUtil;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.model.AbstractProperty;
import org.artorg.tools.phantomData.server.model.Identifiable;

public class DbTableViewSelector<F, U> extends TableViewSelector<U> {

	private static final Map<Class<?>,
		Map<Class<?>, Function<Object, Collection<Object>>>> subItemGetterMap;
	private static final Map<Class<?>,
		Map<Class<?>, Boolean>> containsCollectionSetterMap;
	private final Class<F> parentItemClass;
	private final Class<U> subItemClass;

	static {

		subItemGetterMap = new HashMap<>();
		containsCollectionSetterMap = new HashMap<>();
	}

	public DbTableViewSelector(Class<F> parentItemClass, Class<U> subItemClass) {
		super(subItemClass);
		this.parentItemClass = parentItemClass;
		this.subItemClass = subItemClass;

	}

	public void setItems(Collection<U> selectedItems) {
		List<U> selectableItems =
			getSelectableItems(parentItemClass, (Class<U>) subItemClass);
		List<U> selectedItems2 = selectedItems.stream().collect(Collectors.toList());

		List<Integer> selectableItemsIndexes = CollectionUtil.searchLeftInRight(
			selectableItems, selectedItems2, (l, r) -> ((Identifiable<?>) l).getId()
				.equals(((Identifiable<?>) r).getId()));
		selectedItems2 = CollectionUtil.subList(selectableItems, selectableItemsIndexes);
		selectableItems.removeAll(selectedItems);
		setSelectableItems(selectableItems);
		setSelectedItems(selectedItems);
	}

	@SuppressWarnings("unchecked")
	public void setItem(F item) {
		if (item == null) setItems(Collections.emptyList());

		Function<Object, Collection<Object>> subItemGetter =
			getSubItemGetter(parentItemClass, subItemClass);
		if (subItemGetter != null) {
			setItems(subItemGetter.apply(item).stream().filter(e -> e != null)
				.map(e -> (U) e).collect(Collectors.toList()));
		} else {
			throw new RuntimeException();
		}

//				TitledPane titledPane =
//						((TitledPane) ((TitledPaneTableViewSelector<?>) titledSelector)
//								.getGraphic());
//				titledPane.expandedProperty().addListener(new ChangeListener<Boolean>() {
//					@Override
//					public void changed(ObservableValue<? extends Boolean> observable,
//							Boolean oldValue, Boolean newValue) {
//						if (newValue) selectors.stream()
//								.map(titledSelector -> ((TitledPane) ((TitledPaneTableViewSelector<
//										?>) titledSelector).getGraphic()))
//								.filter(titledPane2 -> titledPane2 != titledPane)
//								.forEach(titledSelector -> {
//									titledSelector.setAnimated(true);
//									titledSelector.setExpanded(false);
//								});
//
//					}
//
//				});

//				return titledSelector;

	}

	@SuppressWarnings("unchecked")
	public static <F, U> List<U> getSelectableItems(Class<F> parentItemClass,
		Class<U> subItemClass) {
		ICrudConnector<U> connector = Connectors.getConnector(subItemClass);
		List<U> items = connector.readAllAsList();
		if (AbstractProperty.class.isAssignableFrom(subItemClass)) {
			items = items.stream().filter(item -> {
				try {
					String type =
						((AbstractProperty<U, ?>) item).getPropertyField().getType();
					Class<?> classType = Class.forName(type);
					if (classType == parentItemClass) return true;
				} catch (NullPointerException | ClassNotFoundException e) {}
				return false;
			}).collect(Collectors.toList());
		}
		return items;
	}

	@SuppressWarnings("unchecked")
	private static Function<Object, Collection<Object>>
		createSubItemGetter(Class<?> parentItemClass, Class<?> subItemClass) {
		Method selectedMethod =
			Reflect.getMethodByGenericReturnType(parentItemClass, subItemClass);
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

	private static Function<Object, Collection<Object>>
		getSubItemGetter(Class<?> parentItemClass, Class<?> subItemClass) {
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
			if (containsCollectionSetterMap.get(parentItemClass)
				.containsKey(subItemClass))
				result =
					containsCollectionSetterMap.get(parentItemClass).get(subItemClass);
			else {
				result = Reflect.containsCollectionSetter(parentItemClass, subItemClass);
				containsCollectionSetterMap.get(parentItemClass).put(subItemClass,
					result);
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
