package org.artorg.tools.phantomData.client.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Reflect {
	
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

	public static List<Class<?>> getSubclasses(Class<?> clazz, String packageName) {
		List<Class<?>> classes = getClasses(packageName);
		return classes.stream().filter(c -> clazz.isAssignableFrom(c)).filter(c -> c != clazz)
				.collect(Collectors.toList());
	}

	/**
	 * Scans all classes accessible from the context class loader which belong to
	 * the given package and subpackages.
	 *
	 * @param packageName The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static List<Class<?>> getClasses(String packageName) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources;
		List<File> dirs = new ArrayList<File>();
		Set<Class<?>> classes = new HashSet<Class<?>>();
		try {
			resources = classLoader.getResources(path);

			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				dirs.add(new File(resource.getFile()));
			}
			for (File directory : dirs)
				classes.addAll(findClasses(directory, packageName));
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return classes.stream().collect(Collectors.toList());
	}

	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base
	 *                    directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	public static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(
						Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}

}
