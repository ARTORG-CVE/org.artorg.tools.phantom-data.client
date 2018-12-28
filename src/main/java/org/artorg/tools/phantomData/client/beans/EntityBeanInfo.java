package org.artorg.tools.phantomData.client.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Entity;

import org.artorg.tools.phantomData.server.model.AbstractPersonifiedEntity;
import org.artorg.tools.phantomData.server.model.AbstractPropertifiedEntity;

public class EntityBeanInfo<T> {
	private static final EntityBeanInfo<?> abstractPersonifiedEntityBeanInfo;
//	private static final EntityBeanInfo abstractBaseEntityBeanInfo;
	private final Class<T> entityClass;
	private final List<PropertyDescriptor> allPropertyDescriptors;
	private final List<PropertyDescriptor> notPersonifiedPropertyDescriptors;
	private final Function<T, List<PropertyDescriptor>> entityDescriptors;
	private final Function<T, List<PropertyDescriptor>> entityCollectionDescriptors;
	private final Function<T, List<PropertyDescriptor>> propertiesDescriptors;

	private final Map<Class<?>, Map<String, Function<Object, Object>>> getterFunctionsMap;
	private final Map<Class<?>,
		Map<String, BiConsumer<Object, Object>>> setterFunctionsMap;

	static {
		abstractPersonifiedEntityBeanInfo =
			new EntityBeanInfo<>(AbstractPersonifiedEntity.class);
//		abstractBaseEntityBeanInfo = new EntityBeanInfo(AbstractBaseEntity.class);
	}
	
	public EntityBeanInfo(Class<T> entityClass) {
		if (!entityClass.isAnnotationPresent(Entity.class))
			if (entityClass != AbstractPropertifiedEntity.class && entityClass != AbstractPersonifiedEntity.class)
				throw new IllegalArgumentException();
		this.entityClass = entityClass;

		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(entityClass);
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}

		allPropertyDescriptors = Arrays.asList(beanInfo.getPropertyDescriptors()).stream()
			.collect(Collectors.toList());

		if (entityClass == AbstractPersonifiedEntity.class)
			notPersonifiedPropertyDescriptors = new ArrayList<PropertyDescriptor>();
		else {
			notPersonifiedPropertyDescriptors = allPropertyDescriptors.stream().filter(d -> {
				return !(abstractPersonifiedEntityBeanInfo.allPropertyDescriptors.stream()
					.filter(bd -> bd == d).findFirst().isPresent());
			}).collect(Collectors.toList());
		}

		entityDescriptors = bean -> notPersonifiedPropertyDescriptors.stream()
			.filter(d -> d.getPropertyType().isAnnotationPresent(Entity.class))
			.filter(d -> getValue(d, bean) != null).collect(Collectors.toList());

		entityCollectionDescriptors = bean -> notPersonifiedPropertyDescriptors.stream()
			.filter(d -> !d.getPropertyType().isAnnotationPresent(Entity.class))
			.filter(d -> Collection.class.isAssignableFrom(d.getPropertyType()))
			.filter(d -> {
				Object value = getValue(d, bean);
				if (value == null) return false; 
				Collection<?> coll = (Collection<?>)value;
				if (coll.isEmpty()) return false;
				return coll.stream().findFirst()
					.get().getClass().isAnnotationPresent(Entity.class);
			})
			.collect(Collectors.toList());

		propertiesDescriptors = bean -> notPersonifiedPropertyDescriptors.stream()
			.filter(d -> !d.getPropertyType().isAnnotationPresent(Entity.class))
			.filter(d -> !Collection.class.isAssignableFrom(d.getPropertyType()))
			.filter(d -> getValue(d, bean) != null).collect(Collectors.toList());

		getterFunctionsMap = notPersonifiedPropertyDescriptors.stream()
			.collect(Collectors.groupingBy((PropertyDescriptor d) -> d.getPropertyType(),
				Collectors.toMap(d -> d.getName(), d -> {
					return bean -> getValue(d, bean);
				})));

		setterFunctionsMap = notPersonifiedPropertyDescriptors.stream()
			.collect(Collectors.groupingBy((PropertyDescriptor d) -> d.getPropertyType(),
				Collectors.toMap(d -> d.getName(), d -> {
					return (bean, value) -> d.createPropertyEditor(bean).setValue(value);
				})));
	}

	public Stream<NamedTreeItem> getNamedEntityValuesAsStream(T bean) {
		return entityDescriptors.apply(bean).stream().map(d -> {
			Object value = EntityBeanInfo.getValue(d, bean);
			if (value == null) return null;
			return new NamedTreeItem(value, d.getName(), "EntityValueAsStream");
		}).filter(property -> property != null);
	}
	
	public Stream<NamedTreeItem> getNamedEntityCollectionValuesAsStream(T bean) {
		return entityCollectionDescriptors.apply(bean).stream().map(d -> {
			Object value = EntityBeanInfo.getValue(d, bean);
			if (value == null) return null;
			return new NamedTreeItem(value, d.getName(), "CollectionValueAsStream");
		}).filter(property -> property != null);
	}

	public Stream<NamedTreeItem> getNamedPropertiesValueAsStream(T bean) {
		return propertiesDescriptors.apply(bean).stream().map(d -> {
			Object value = EntityBeanInfo.getValue(d, bean);
			if (value == null) return null;
			return new NamedTreeItem(value, d.getName(), "PropertiesValueAsStream");
		}).filter(property -> property != null);
	}

	public static boolean isEntity(Object o) {
		return !o.getClass().isAnnotationPresent(Entity.class);
	}

	public static EntityBeanInfo<?> getAbstractPersonifiedEntityBeanInfo() {
		return abstractPersonifiedEntityBeanInfo;
	}

	public static Object getValue(PropertyDescriptor descriptor, Object bean) {
		try {
			return descriptor.getReadMethod().invoke(bean);
		} catch (IllegalAccessException | IllegalArgumentException
			| InvocationTargetException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException();
	}

	public Stream<Object> getEntitiesAsStream(T bean) {
		if (bean == null) return Stream.<Object>empty();
		return entityDescriptors.apply(bean).stream().map(d -> getValue(d, bean));
	}

	@SuppressWarnings("unchecked")
	public Stream<Collection<Object>> getEntityCollectionsAsStream(T bean) {
		if (bean == null) return Stream.<Collection<Object>>empty();
		return entityCollectionDescriptors.apply(bean).stream()
			.map(d -> ((Collection<Object>) getValue(d, bean)));
	}

	public Stream<Object> getPropertiesAsStream(T bean) {
		if (bean == null) return Stream.<Object>empty();
		return propertiesDescriptors.apply(bean).stream().map(d -> getValue(d, bean));
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public Function<Object, Object> getGetterAsFunction(Class<?> type, String name) {
		return getterFunctionsMap.get(type).get(name);
	}

	public BiConsumer<Object, Object> getSetterAsFunction(Class<?> type, String name) {
		return setterFunctionsMap.get(type).get(name);
	}

	// Getters & Setters
	public List<PropertyDescriptor> getAllPropertyDescriptors() {
		return allPropertyDescriptors;
	}

	public List<PropertyDescriptor> getNotBasePropertyDescriptors() {
		return notPersonifiedPropertyDescriptors;
	}

}
