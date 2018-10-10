package org.artorg.tools.phantomData.client.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.artorg.tools.phantomData.client.Main;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

public class Reflect {
	
	
	
	
	
	@SuppressWarnings("unchecked")
	public static <T> T createInstanceByGenericAndSuperClass(Class<T> superClass, Class<?> genericClass, Reflections reflections) {
		Class<?> subClass = getClassByGenericAndSuperClass(superClass, superClass, genericClass, 0, reflections);
		T t = null;
		try {
			t = (T) subClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return t;
	}
	
	
	public static <T> Class<?> getClassByGenericAndSuperClass(Class<T> superClass, Class<?> genericClass, Reflections reflections) {
		return getClassByGenericAndSuperClass(superClass, superClass, genericClass, 0, reflections);
	}
	
	public static <T> Class<?> getClassByGenericAndSuperClass(Class<T> superClass, Class<?> genericClass, int parameterIndex, Reflections reflections) {
		return getClassByGenericAndSuperClass(superClass, superClass, genericClass, parameterIndex, reflections);
	}
	
	
	public static <T> Class<?> getClassByGenericAndSuperClass(Class<T> superClass, Class<?> classOfInterest, Class<?> genericClass, int parameterIndex, Reflections reflections) {
		List<Class<?>> superClasses = Reflect.getSubclasses(superClass, Main.getReflections());
		Class<?> cls = superClasses.stream().filter(c -> {
			try {
				return Reflect.findSubClassParameterType(c.newInstance(), classOfInterest, parameterIndex) == genericClass;
			} catch (Exception e2) {
			}
			return false;
		}).findFirst().orElseThrow(() -> new IllegalArgumentException());
		if (cls == null)
			throw new NullPointerException();
		return cls;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Class<? extends T[]> getArrayClass(Class<T> clazz) {
	    return (Class<? extends T[]>) Array.newInstance(clazz, 0).getClass();
	}
	
	
	
	public static Class<?> findGenericClasstype(Object instance) {
		return findSubClassParameterType(instance, instance.getClass().getSuperclass(), 0);
	}
	
	
	public static Class<?> findSubClassParameterType(Object instance, Class<?> classOfInterest, int parameterIndex) {
		Map<Type, Type> typeMap = new HashMap<Type, Type>();
		Class<?> instanceClass = instance.getClass();
		while (classOfInterest != instanceClass.getSuperclass()) {
			extractTypeArguments(typeMap, instanceClass);
			instanceClass = instanceClass.getSuperclass();
			if (instanceClass == null)
				throw new IllegalArgumentException();
		}
		ParameterizedType parameterizedType = (ParameterizedType) instanceClass.getGenericSuperclass();
		Type actualType = parameterizedType.getActualTypeArguments()[parameterIndex];
		if (typeMap.containsKey(actualType)) {
			actualType = typeMap.get(actualType);
		}
		if (actualType instanceof Class) {
			return (Class<?>) actualType;
		} else if (actualType instanceof TypeVariable) {
			return browseNestedTypes(instance, (TypeVariable<?>) actualType);
		} else {
			throw new IllegalArgumentException();
		}
	}

	private static Class<?> browseNestedTypes(Object instance, TypeVariable<?> actualType) {
		Class<?> instanceClass = instance.getClass();
		List<Class<?>> nestedOuterTypes = new LinkedList<Class<?>>();
		for (Class<?> enclosingClass = instanceClass
				.getEnclosingClass(); enclosingClass != null; enclosingClass = enclosingClass.getEnclosingClass()) {
			try {
				Field this$0 = instanceClass.getDeclaredField("this$0");
				Object outerInstance = this$0.get(instance);
				Class<?> outerClass = outerInstance.getClass();
				nestedOuterTypes.add(outerClass);
				Map<Type, Type> outerTypeMap = new HashMap<Type, Type>();
				extractTypeArguments(outerTypeMap, outerClass);
				for (Map.Entry<Type, Type> entry : outerTypeMap.entrySet()) {
					if (!(entry.getKey() instanceof TypeVariable)) {
						continue;
					}
					TypeVariable<?> foundType = (TypeVariable<?>) entry.getKey();
					if (foundType.getName().equals(actualType.getName())
							&& isInnerClass(foundType.getGenericDeclaration(), actualType.getGenericDeclaration())) {
						if (entry.getValue() instanceof Class) {
							return (Class<?>) entry.getValue();
						}
						actualType = (TypeVariable<?>) entry.getValue();
					}
				}
			} catch (NoSuchFieldException e) {
				/* this should never happen */ } catch (IllegalAccessException e) {
				/* this might happen */}
		}
		throw new IllegalArgumentException();
	}

	private static void extractTypeArguments(Map<Type, Type> typeMap, Class<?> clazz) {
		Type genericSuperclass = clazz.getGenericSuperclass();
		if (!(genericSuperclass instanceof ParameterizedType)) {
			return;
		}
		ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
		Type[] typeParameter = ((Class<?>) parameterizedType.getRawType()).getTypeParameters();
		Type[] actualTypeArgument = parameterizedType.getActualTypeArguments();
		for (int i = 0; i < typeParameter.length; i++) {
			if (typeMap.containsKey(actualTypeArgument[i])) {
				actualTypeArgument[i] = typeMap.get(actualTypeArgument[i]);
			}
			typeMap.put(typeParameter[i], actualTypeArgument[i]);
		}
	}

	private static boolean isInnerClass(GenericDeclaration outerDeclaration, GenericDeclaration innerDeclaration) {
		if (!(outerDeclaration instanceof Class) || !(innerDeclaration instanceof Class)) {
			throw new IllegalArgumentException();
		}
		Class<?> outerClass = (Class<?>) outerDeclaration;
		Class<?> innerClass = (Class<?>) innerDeclaration;
		while ((innerClass = innerClass.getEnclosingClass()) != null) {
			if (innerClass == outerClass) {
				return true;
			}
		}
		return false;
	}
	
	
	
	
	
	
	
	
	public static Method getFirstMethod(Class<?> cls, Function<Stream<Method>, Stream<Method>> filteringStream) {
		return getFirstMethod(filteringStream.apply(Arrays.asList(cls.getMethods()).stream()));
	}
	
	
	public static Method getFirstMethod(Stream<Method> stream) {
		return stream.findFirst().get();
	}
	
	
	public static Method getMethod(Stream<Method> stream) {
		return stream.limit(2l).collect(Collectors2.toSingleton());
	}
	
	public static Method getMethod(Class<?> cls, Function<Stream<Method>, Stream<Method>> filteringStream) {
		return getMethod(filteringStream.apply(Arrays.asList(cls.getMethods()).stream()));
	}
	
	
	public static Stream<Method> getMethods(Class<?> cls, Function<Stream<Method>, Stream<Method>> filteringStream) {
		return filteringStream.apply(Arrays.asList(cls.getMethods()).stream());
	}
	
	public static Stream<Method> getCollectionSetterMethods(Class<?> itemClass) {
		return getMethods(itemClass, stream -> stream
				.filter(m -> m.getReturnType() == Void.TYPE)
				.filter(m -> Modifier.isPublic(m.getModifiers()))
				.filter(m -> m.getParameterTypes().length == 1)
				.filter(m -> Collection.class.isAssignableFrom(m.getParameterTypes()[0]))
				.filter(m -> m.getGenericParameterTypes().length == 1));
	}
	
	public static boolean containsCollectionSetter(Object item, Class<?> genericCollectionType) {
		return getMethods(item.getClass(), stream -> stream
				.filter(m -> m.getReturnType() == Void.TYPE)
				.filter(m -> m.getParameterTypes().length == 1)
				.filter(m -> Collection.class.isAssignableFrom(m.getParameterTypes()[0]))
				.filter(m -> m.getGenericParameterTypes().length == 1)
				.filter(m -> {
					Type type = m.getGenericParameterTypes()[0];
					Class<?> clazz = Reflect.getGenericTypeClass(type);
					return clazz == genericCollectionType;
				})).count() == 1;
	}
	
	public static <T> Function<T, Object> compileFunctional(Method m, Object... args) {
		return o -> {
			try {
				return m.invoke(o, args);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		};
	}
	
	public static void invokeGenericSetter(Object reference, Class<?> paramTypeClass, Class<?> genericParamtype, Object arg) {
		Method m = getSetterMethodBySingleGenericParamtype(reference, paramTypeClass, genericParamtype);
		try {
			m.invoke(reference, arg);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public static Method getSetterMethodBySingleGenericParamtype(Object item, Class<?> paramTypeClass, Class<?> genericParamtype) {		
		return getMethod(item.getClass(), stream -> stream
				.filter(m -> m.getReturnType() == Void.TYPE)
				.filter(m -> m.getParameterTypes().length == 1)
				.filter(m -> m.getParameterTypes()[0].isAssignableFrom(paramTypeClass))
				.filter(m -> m.getGenericParameterTypes().length == 1)
				.filter(m -> {
					Type type = m.getGenericParameterTypes()[0];
					Class<?> clazz = Reflect.getGenericTypeClass(type);
					return clazz == genericParamtype;
				}));
	}

	public static Method getMethodByGenericReturnType(Object item, Class<?> genericReturnType) {
		return getMethod(item.getClass(), stream -> stream
				.filter(m -> m.getParameterTypes().length == 0)
				.filter(m -> Collection.class.isAssignableFrom(m.getReturnType()))
				.filter(m -> Reflect.getGenericReturnTypeClass(m) == genericReturnType));
	}

	public static Method getMethodByGenericParamtype(Object item, Class<?> genericParamtype) {
		return getMethod(item.getClass(), stream -> stream
				.filter(m -> m.getGenericParameterTypes().length == 1)
				.filter(m -> {
					Type type = m.getGenericParameterTypes()[0];
					Class<?> clazz = Reflect.getGenericTypeClass(type);
					return clazz == genericParamtype;
				}));
	}

	public static boolean isStatic(Method m) {
		return Modifier.isStatic(m.getModifiers());
	}

	public static Class<?> getGenericReturnTypeClass(Method m) {
		return getGenericTypeClass(m.getGenericReturnType());
	}

	public static Class<?> getGenericTypeClass(Type type) {
		try {
			ParameterizedType paramType = (ParameterizedType) type;
			Type[] argTypes = paramType.getActualTypeArguments();
			if (argTypes.length > 0)
				if (argTypes[0] instanceof Class)
					return ((Class<?>) argTypes[0]);
		} catch (Exception e) {}
		return null;
	}
	
	

	public static List<Class<?>> getSubclasses(Class<?> clazz, Reflections reflections) {		
		List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
		classLoadersList.add(ClasspathHelper.contextClassLoader());
		classLoadersList.add(ClasspathHelper.staticClassLoader());

		Set<?> classes = reflections.getSubTypesOf(clazz);
		List<Class<?>> classes2 = classes.stream().map(o -> (Class<?>)o).collect(Collectors.toList());
		return classes2;
	}

}
