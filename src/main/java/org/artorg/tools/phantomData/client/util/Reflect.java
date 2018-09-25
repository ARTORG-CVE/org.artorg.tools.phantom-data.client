package org.artorg.tools.phantomData.client.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Reflect {

	public static boolean containsCollectionSetter(Object item, Class<?> genericCollectionType) {
		List<Method> selectedMethods = Arrays.asList(item.getClass().getMethods()).stream()
				.filter(m -> m.getReturnType() == Void.TYPE)
				.filter(m -> m.getParameterTypes().length == 1)
				.filter(m -> Collection.class.isAssignableFrom(m.getParameterTypes()[0]))
				.filter(m -> m.getGenericParameterTypes().length == 1).filter(m -> {
					Type type = m.getGenericParameterTypes()[0];
					Class<?> clazz = Reflect.getGenericTypeClass(type);
					return clazz == genericCollectionType;
				})
				.collect(Collectors.toList());
		
		if (selectedMethods.size() == 1)
			return true;
		
		return false;
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
		List<Method> selectedMethods = Arrays.asList(item.getClass().getMethods()).stream()
				.filter(m -> m.getReturnType() == Void.TYPE)
				.filter(m -> m.getParameterTypes().length == 1)
				.filter(m -> m.getParameterTypes()[0].isAssignableFrom(paramTypeClass))
				.filter(m -> m.getGenericParameterTypes().length == 1).filter(m -> {
					Type type = m.getGenericParameterTypes()[0];
					Class<?> clazz = Reflect.getGenericTypeClass(type);
					return clazz == genericParamtype;
				}).collect(Collectors.toList());
		if (selectedMethods.size() != 1)
			throw new IllegalArgumentException();
		return selectedMethods.get(0);
	}

	public static Method getMethodByGenericReturnType(Object item, Class<?> genericReturnType) {
		List<Method> selectedMethods = Arrays.asList(item.getClass().getMethods()).stream()
				.filter(m -> m.getParameterTypes().length == 0)
				.filter(m -> Collection.class.isAssignableFrom(m.getReturnType()))
				.filter(m -> Reflect.getGenericReturnTypeClass(m) == genericReturnType).collect(Collectors.toList());
		if (selectedMethods.size() != 1)
			throw new IllegalArgumentException();
		return selectedMethods.get(0);
	}

	public static Method getMethodByGenericParamtype(Object item, Class<?> genericParamtype) {
		List<Method> selectedMethods = Arrays.asList(item.getClass().getMethods()).stream()
				.filter(m -> m.getGenericParameterTypes().length == 1).filter(m -> {
					Type type = m.getGenericParameterTypes()[0];
					Class<?> clazz = Reflect.getGenericTypeClass(type);
					return clazz == genericParamtype;
				}).collect(Collectors.toList());
		if (selectedMethods.size() != 1)
			throw new IllegalArgumentException();
		return selectedMethods.get(0);
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
