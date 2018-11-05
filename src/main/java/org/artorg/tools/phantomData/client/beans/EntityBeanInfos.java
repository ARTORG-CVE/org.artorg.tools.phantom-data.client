package org.artorg.tools.phantomData.client.beans;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Entity;

import org.artorg.tools.phantomData.server.specification.DbPersistent;
import org.reflections.Reflections;

public class EntityBeanInfos {
	private final Set<Class<?>> entityClasses;
	private final Map<Class<?>, EntityBeanInfo> beanInfoMap;

	public EntityBeanInfos(Reflections reflections) {
		entityClasses = reflections.getSubTypesOf(DbPersistent.class).stream()
				.filter(c -> c.isAnnotationPresent(Entity.class))
				.collect(Collectors.toSet());

		beanInfoMap = entityClasses.stream()
				.collect(Collectors.toMap(c -> c, c -> new EntityBeanInfo(c)));
	}
	
	public EntityBeanInfo getEntityBeanInfo(Class<?> entityClass) {
		return beanInfoMap.get(entityClass);
	}

	public Stream<Object> getEntitiesAsStream(Object bean) {
		if (bean == null)
			return Stream.<Object>empty();
		if (beanInfoMap.containsKey(bean.getClass()))
			return getEntitiesAsStream(bean.getClass(), bean);
		throw new IllegalArgumentException();
	}

	public Stream<Collection<Object>> getEntityCollectionsAsStream(Object bean) {
		if (bean == null)
			return Stream.<Collection<Object>>empty();
		if (beanInfoMap.containsKey(bean.getClass()))
			return getEntityCollectionsAsStream(bean.getClass(), bean);
		throw new IllegalArgumentException();
	}

	public Stream<Collection<Object>> getEntityCollectionsAsStream(Class<?> entityClass,
			Object bean) {
		return beanInfoMap.get(entityClass).getEntityCollectionsAsStream(bean);
	}

	public Stream<Object> getEntitiesAsStream(Class<?> entityClass, Object bean) {
		return beanInfoMap.get(entityClass).getEntitiesAsStream(bean);
	}

	public Stream<Object> getPropertiesAsStream(Object bean) {
		if (bean == null)
			return Stream.<Object>empty();
		if (beanInfoMap.containsKey(bean.getClass()))
			return getPropertiesAsStream(bean.getClass(), bean);
		throw new IllegalArgumentException();
	}

	public Stream<Object> getPropertiesAsStream(Class<?> entityClass, Object bean) {
		if (bean == null)
			return Stream.<Object>empty();
		return beanInfoMap.get(entityClass).getPropertiesAsStream(bean);
	}

	public Function<Object, Object> getGetterAsFunction(Class<?> entityClass,
			Class<?> type, String name) {
		return beanInfoMap.get(entityClass).getGetterAsFunction(type, name);
	}

	public BiConsumer<Object, Object> getSetterAsFunction(Class<?> entityClass,
			Class<?> type, String name) {
		return beanInfoMap.get(entityClass).getSetterAsFunction(type, name);
	}

	public Set<Class<?>> getEntityClasses() {
		return entityClasses;
	}

}
