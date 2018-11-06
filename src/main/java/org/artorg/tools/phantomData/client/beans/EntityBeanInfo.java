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

import org.artorg.tools.phantomData.server.model.specification.AbstractBaseEntity;
import org.artorg.tools.phantomData.server.model.specification.AbstractPersonifiedEntity;

public class EntityBeanInfo {
	private static final EntityBeanInfo abstractPersonifiedEntityBeanInfo;
//	private static final EntityBeanInfo abstractBaseEntityBeanInfo;
	private final Class<?> entityClass;
	private final List<PropertyDescriptor> allPropertyDescriptors;
	private final List<PropertyDescriptor> notBasePropertyDescriptors;
	private final Function<Object, List<PropertyDescriptor>> entityDescriptors;
	private final Function<Object, List<PropertyDescriptor>> collectionDescriptors;
	private final Function<Object, List<PropertyDescriptor>> propertiesDescriptors;

	private final Map<Class<?>, Map<String, Function<Object, Object>>> getterFunctionsMap;
	private final Map<Class<?>,
		Map<String, BiConsumer<Object, Object>>> setterFunctionsMap;

	static {
		abstractPersonifiedEntityBeanInfo =
			new EntityBeanInfo(AbstractPersonifiedEntity.class);
//		abstractBaseEntityBeanInfo = new EntityBeanInfo(AbstractBaseEntity.class);
	}

	@SuppressWarnings("unchecked")
	public EntityBeanInfo(Class<?> entityClass) {
		if (!entityClass.isAnnotationPresent(Entity.class))
			if (entityClass != AbstractBaseEntity.class && entityClass != AbstractPersonifiedEntity.class)
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
			notBasePropertyDescriptors = new ArrayList<PropertyDescriptor>();
		else {
			notBasePropertyDescriptors = allPropertyDescriptors.stream().filter(d -> {
				return !(abstractPersonifiedEntityBeanInfo.allPropertyDescriptors.stream()
					.filter(bd -> bd == d).findFirst().isPresent());
			}).collect(Collectors.toList());
		}

		entityDescriptors = bean -> notBasePropertyDescriptors.stream()
			.filter(d -> d.getPropertyType().isAnnotationPresent(Entity.class))
			.filter(d -> getValue(d, bean) != null).collect(Collectors.toList());

		collectionDescriptors = bean -> notBasePropertyDescriptors.stream()
			.filter(d -> !d.getPropertyType().isAnnotationPresent(Entity.class))
			.filter(d -> Collection.class.isAssignableFrom(d.getPropertyType()))
			.filter(d -> !((Collection<Object>) getValue(d, bean)).isEmpty())
			.filter(d -> ((Collection<Object>) getValue(d, bean)).stream().findFirst()
				.get().getClass().isAnnotationPresent(Entity.class))
			.filter(d -> getValue(d, bean) != null).collect(Collectors.toList());

		propertiesDescriptors = bean -> notBasePropertyDescriptors.stream()
			.filter(d -> !d.getPropertyType().isAnnotationPresent(Entity.class))
			.filter(d -> !Collection.class.isAssignableFrom(d.getPropertyType()))
			.filter(d -> getValue(d, bean) != null).collect(Collectors.toList());

		getterFunctionsMap = notBasePropertyDescriptors.stream()
			.collect(Collectors.groupingBy((PropertyDescriptor d) -> d.getPropertyType(),
				Collectors.toMap(d -> d.getName(), d -> {
					return bean -> getValue(d, bean);
				})));

		setterFunctionsMap = notBasePropertyDescriptors.stream()
			.collect(Collectors.groupingBy((PropertyDescriptor d) -> d.getPropertyType(),
				Collectors.toMap(d -> d.getName(), d -> {
					return (bean, value) -> d.createPropertyEditor(bean).setValue(value);
				})));
	}

	public Stream<DbNode> getNamedEntityValuesAsStream(Object bean) {
		return entityDescriptors.apply(bean).stream().map(d -> {
			Object value = EntityBeanInfo.getValue(d, bean);
			if (value == null) return null;
			return new DbNode(value, d.getName(), "EntityValueAsStream");
		}).filter(property -> property != null);
	}

	public Stream<DbNode> getNamedCollectionValuesAsStream(Object bean) {
		return collectionDescriptors.apply(bean).stream().map(d -> {
			Object value = EntityBeanInfo.getValue(d, bean);
			if (value == null) return null;
			return new DbNode(value, d.getName(), "CollectionValueAsStream");
		}).filter(property -> property != null);
	}

	public Stream<DbNode> getNamedPropertiesValueAsStream(Object bean) {
		return propertiesDescriptors.apply(bean).stream().map(d -> {
			Object value = EntityBeanInfo.getValue(d, bean);
			if (value == null) return null;
			return new DbNode(value, d.getName(), "PropertiesValueAsStream");
		}).filter(property -> property != null);
	}

	public static boolean isEntity(Object o) {
		return !o.getClass().isAnnotationPresent(Entity.class);
	}

	public static EntityBeanInfo getAbstractPersonifiedEntityBeanInfo() {
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

	public Stream<Object> getEntitiesAsStream(Object bean) {
		if (bean == null) return Stream.<Object>empty();
		return entityDescriptors.apply(bean).stream().map(d -> getValue(d, bean));
	}

	@SuppressWarnings("unchecked")
	public Stream<Collection<Object>> getEntityCollectionsAsStream(Object bean) {
		if (bean == null) return Stream.<Collection<Object>>empty();
		return collectionDescriptors.apply(bean).stream()
			.map(d -> ((Collection<Object>) getValue(d, bean)));
	}

	public Stream<Object> getPropertiesAsStream(Object bean) {
		if (bean == null) return Stream.<Object>empty();
		return propertiesDescriptors.apply(bean).stream().map(d -> getValue(d, bean));
	}

	public Class<?> getEntityClass() {
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
		return notBasePropertyDescriptors;
	}

}
