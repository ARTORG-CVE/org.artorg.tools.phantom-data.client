package org.artorg.tools.phantomData.client.connector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.client.util.StreamUtils;
import org.artorg.tools.phantomData.server.specification.ControllerSpec;
import org.artorg.tools.phantomData.server.specification.Identifiable;
import org.reflections.Reflections;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.artorg.tools.phantomData.server.controller.*;

@SuppressWarnings("unused")
public class HttpConnectorSpring<T extends Identifiable<UUID>> extends CrudConnectors<T, UUID> {
	private final Function<Method, String[]> stringAnnosFuncCreate;
	private final Function<Method, String[]> stringAnnosFuncRead;
	private final Function<Method, String[]> stringAnnosFuncUpdate;
	private final Function<Method, String[]> stringAnnosFuncDelete;
	private final String annoStringControlClass;
	private final String annoStringRead;
	private final String annoStringReadAll;
	private final String annoStringCreate;
	private final String annoStringDelete;
	private final String annoStringUpdate;
	private final String annoStringExist;
	private final Class<?> itemClass;
	private final Class<?> arrayItemClass;
	private final Class<?> controllerClass;

	private static String urlLocalhost;
	private static final Map<Class<?>, HttpConnectorSpring<?>> connectorMap;

	static {
		connectorMap = new HashMap<Class<?>, HttpConnectorSpring<?>>();
	}

	public HttpConnectorSpring(Class<?> itemClass) {
		if (itemClass == null)
			itemClass = Reflect.findGenericClasstype(this);
		this.itemClass = itemClass;
		if (connectorMap.containsKey(this.itemClass))
			throw new IllegalArgumentException();

		arrayItemClass = Reflect.getArrayClass(itemClass);
		Class<?> controllerClass;
		List<Class<?>> controllerClasses = Reflect.getSubclasses(ControllerSpec.class, Main.getReflections());
		controllerClass = controllerClasses.stream().filter(c -> {
			try {
				return Reflect.findSubClassParameterType(c.newInstance(), ControllerSpec.class, 0) == this.itemClass;
			} catch (Exception e2) {
			}
			return false;
		}).findFirst().orElseThrow(() -> new IllegalArgumentException());
		if (controllerClass == null)
			throw new NullPointerException();
		this.controllerClass = controllerClass;

		System.out.println(String.format("create connector: itemClass = %s, arrayClass = %s, controllerClass = %s",
				itemClass.getSimpleName(), arrayItemClass.getSimpleName(), controllerClass.getSimpleName()));

		stringAnnosFuncCreate = m -> m.getAnnotation(PostMapping.class).value();
		stringAnnosFuncRead = m -> m.getAnnotation(GetMapping.class).value();
		stringAnnosFuncUpdate = m -> m.getAnnotation(PutMapping.class).value();
		stringAnnosFuncDelete = m -> m.getAnnotation(DeleteMapping.class).value();

		// class annotation
		RequestMapping anno = getControllerClass().getAnnotation(RequestMapping.class);
		annoStringControlClass = anno.value()[0];
		annoStringExceptionHandler(annoStringControlClass, RequestMapping.class);

		// method annotations
//		annoStringCreate = getAnnotationString(PostMapping.class, stringAnnosFuncCreate);
//		annoStringRead = getAnnotationStringRead("ID");
//		annoStringReadAll = getAnnotationStringAll(GetMapping.class, stringAnnosFuncRead);
//		annoStringUpdate = getAnnotationString(PutMapping.class, stringAnnosFuncUpdate);
//		annoStringDelete = getAnnotationString(DeleteMapping.class, stringAnnosFuncDelete);

//		annoStringCreate = getAnnotationsString(PostMapping.class, Void.class, stringAnnosFuncCreate);
//		annoStringRead = getAnnotationsString(GetMapping.class, itemClass, Arrays.asList(), Arrays.asList("all", "All", "ALL"), stringAnnosFuncRead);
//		annoStringUpdate = getAnnotationsString(PutMapping.class, itemClass, stringAnnosFuncUpdate);
//		annoStringDelete = getAnnotationsString(DeleteMapping.class, Void.class, stringAnnosFuncDelete);

		{
			Predicate<Method> predicate1 = m -> m.getName().equals("create");
			Predicate<Method> predicate2 = m -> m.isAnnotationPresent(PostMapping.class);
			Predicate<Method> predicate3 = m -> Modifier.isPublic(m.getModifiers());
			Predicate<Method> predicate4 = m -> !Modifier.isAbstract(m.getModifiers());
			Predicate<Method> predicate5 = m -> !m.isDefault();
			Predicate<Method> filterPredicate = predicate1.and(predicate2).and(predicate3).and(predicate4).and(predicate5);
			Function<Method, String> stringAnnosFunc = m -> m.getAnnotation(PostMapping.class).value()[0];
			annoStringCreate = getAnnotatedValue(PostMapping.class, filterPredicate, stringAnnosFunc);
		}

		{
			Predicate<Method> predicate1 = m -> m.getName().equals("getById");
			Predicate<Method> predicate2 = m -> m.isAnnotationPresent(GetMapping.class);
			Predicate<Method> predicate3 = m -> Modifier.isPublic(m.getModifiers());
			Predicate<Method> predicate4 = m -> !Modifier.isAbstract(m.getModifiers());
			Predicate<Method> predicate5 = m -> !m.isDefault();
			Predicate<Method> filterPredicate = predicate1.and(predicate2).and(predicate3).and(predicate4).and(predicate5);
			Function<Method, String> stringAnnosFunc = m -> m.getAnnotation(GetMapping.class).value()[0];
			annoStringRead = getAnnotatedValue(GetMapping.class, filterPredicate, stringAnnosFunc);
		}
		
		{
			Predicate<Method> predicate1 = m -> m.getName().equals("getAll");
			Predicate<Method> predicate2 = m -> m.isAnnotationPresent(GetMapping.class);
			Predicate<Method> predicate3 = m -> Modifier.isPublic(m.getModifiers());
			Predicate<Method> predicate4 = m -> !Modifier.isAbstract(m.getModifiers());
			Predicate<Method> predicate5 = m -> !m.isDefault();
			Predicate<Method> filterPredicate = predicate1.and(predicate2).and(predicate3).and(predicate4).and(predicate5);
			Function<Method, String> stringAnnosFunc = m -> m.getAnnotation(GetMapping.class).value()[0];
			annoStringReadAll = getAnnotatedValue(GetMapping.class, filterPredicate, stringAnnosFunc);
		}
		
		{
			Predicate<Method> predicate1 = m -> m.getName().equals("update");
			Predicate<Method> predicate2 = m -> m.isAnnotationPresent(PutMapping.class);
			Predicate<Method> predicate3 = m -> Modifier.isPublic(m.getModifiers());
			Predicate<Method> predicate4 = m -> !Modifier.isAbstract(m.getModifiers());
			Predicate<Method> predicate5 = m -> !m.isDefault();
			Predicate<Method> filterPredicate = predicate1.and(predicate2).and(predicate3).and(predicate4).and(predicate5);
			Function<Method, String> stringAnnosFunc = m -> m.getAnnotation(PutMapping.class).value()[0];
			annoStringUpdate = getAnnotatedValue(PutMapping.class, filterPredicate, stringAnnosFunc);
		}
		
		{
			Predicate<Method> predicate1 = m -> m.getName().equals("delete");
			Predicate<Method> predicate2 = m -> m.isAnnotationPresent(DeleteMapping.class);
			Predicate<Method> predicate3 = m -> Modifier.isPublic(m.getModifiers());
			Predicate<Method> predicate4 = m -> !Modifier.isAbstract(m.getModifiers());
			Predicate<Method> predicate5 = m -> !m.isDefault();
			Predicate<Method> filterPredicate = predicate1.and(predicate2).and(predicate3).and(predicate4).and(predicate5);
			Function<Method, String> stringAnnosFunc = m -> m.getAnnotation(DeleteMapping.class).value()[0];
			annoStringDelete = getAnnotatedValue(DeleteMapping.class, filterPredicate, stringAnnosFunc);
		}
		
		{
			Predicate<Method> predicate1 = m -> m.getName().equals("existById");
			Predicate<Method> predicate2 = m -> m.isAnnotationPresent(GetMapping.class);
			Predicate<Method> predicate3 = m -> Modifier.isPublic(m.getModifiers());
			Predicate<Method> predicate4 = m -> !Modifier.isAbstract(m.getModifiers());
			Predicate<Method> filterPredicate = predicate1.and(predicate2).and(predicate3).and(predicate4);
			Function<Method, String> stringAnnosFunc = m -> m.getAnnotation(GetMapping.class).value()[0];
			annoStringExist = getAnnotatedValue(GetMapping.class, filterPredicate, stringAnnosFunc);
		}


	}

//	private String getAnnotationsString(Class<? extends Annotation> annotationClass, Class<?> genericReturnType,
//			Function<Method, String[]> stringAnnosFunc) {
//		Method m = getAnnotatedMethod(annotationClass, genericReturnType, stringAnnosFunc);
//		return stringAnnosFunc.apply(m)[0];
//	}

//	private String getAnnotationsString(Class<? extends Annotation> annotationClass, Class<?> genericReturnType,
//			List<String> includingFindStrings, List<String> excludingFindStrings,
//			Function<Method, String[]> stringAnnosFunc) {
//		Method m = getAnnotatedMethod(annotationClass, genericReturnType, includingFindStrings, excludingFindStrings,
//				stringAnnosFunc);
//		return stringAnnosFunc.apply(m)[0];
//	}

//	private Method getAnnotatedMethod(Class<? extends Annotation> annotationClass, Class<?> genericReturnType,
//			Function<Method, String[]> stringAnnosFunc) {
//		return getAnnotatedMethod(annotationClass, genericReturnType, Collections.emptyList(), Collections.emptyList(),
//				stringAnnosFunc);
//	}

//	private Predicate<String> getRegexTextFilterPredicate(List<String> includeRegexAnd, List<String> excludeRegexAnd,
//			List<String> includeRegerOr, List<String> excludeRegexOr) {
//		Predicate<String> includeAndPredicate = StreamUtils.getRegexTextFilterPredicate(includeRegexAnd, true, false);
//		Predicate<String> iexcludeAndPredicate = StreamUtils.getRegexTextFilterPredicate(includeRegexAnd, true, false);
//		Predicate<String> includeOrPredicate = StreamUtils.getRegexTextFilterPredicate(includeRegexAnd, true, true);
//		Predicate<String> iexcludeOrPredicate = StreamUtils.getRegexTextFilterPredicate(includeRegexAnd, true, true);
//
//		List<Pattern> includePatterns = includeRegexAnd.stream().map(regex -> Pattern.compile(regex))
//				.collect(Collectors.toList());
//		List<Pattern> excludePatterns = excludeRegexAnd.stream().map(regex -> Pattern.compile(regex))
//				.collect(Collectors.toList());
//		Predicate<String> includingPredicate = s -> {
//			return includePatterns.stream().filter(pattern -> pattern.matcher(s).matches()).count() == includePatterns
//					.size();
//		};
//		Predicate<String> excludingPredicate = s -> {
//			return excludePatterns.stream().filter(pattern -> !pattern.matcher(s).matches()).count() == excludePatterns
//					.size();
//		};
//		return excludingPredicate.and(includingPredicate);
//	}

	private String getAnnotatedValue(Class<? extends Annotation> annotationClass,
			Function<Method, String> stringAnnosFunc) {
		return getAnnotatedValue(getControllerClass(), annotationClass, m -> true, stringAnnosFunc);
	}

//	private String getAnnotatedValue(Class<? extends Annotation> annotationClass,
//			Predicate<String> textFilterPredicate, Function<Method, String> stringAnnosFunc) {
//		return getAnnotatedValue(getControllerClass(), annotationClass, textFilterPredicate,
//				m -> true, stringAnnosFunc);
//	}

//	private String getAnnotatedValue(Class<? extends Annotation> annotationClass,
//			Predicate<Method> methodFilterPredicate, Function<Method, String> stringAnnosFunc) {
//		return getAnnotatedValue(getControllerClass(), annotationClass, s -> true,
//				methodFilterPredicate, stringAnnosFunc);
//	}

//	private String getAnnotatedValue(Class<? extends Annotation> annotationClass, Predicate<Method> methodFilterPredicate, 
//			Predicate<String> textFilterPredicate, Function<Method, String> stringAnnosFunc) {
//		return getAnnotatedValue(getControllerClass(), annotationClass, textFilterPredicate,
//				methodFilterPredicate, stringAnnosFunc);
//	}

	private Predicate<Method> convertTextFilterPredicate(Predicate<String> textFilterPredicate,
			Function<Method, String> stringAnnosFunc) {
		return m -> textFilterPredicate.test(stringAnnosFunc.apply(m));
	}

	private String getAnnotatedValue(Class<? extends Annotation> annotationClass,
			Predicate<Method> methodFilterPredicate, Function<Method, String> stringAnnosFunc) {
		return stringAnnosFunc.apply(getAnnotatedMethod(getControllerClass(), annotationClass, methodFilterPredicate));
	}

	private static String getAnnotatedValue(Class<?> methodsClass, Class<? extends Annotation> annotationClass,
			Predicate<Method> methodFilterPredicate, Function<Method, String> stringAnnosFunc) {
		return stringAnnosFunc.apply(getAnnotatedMethod(methodsClass, annotationClass, methodFilterPredicate));
	}

	private static Method getAnnotatedMethod(Class<?> methodsClass, Class<? extends Annotation> annotationClass,
			Predicate<Method> filterPredicate) {
		return Reflect.getFirstMethod(methodsClass,
				stream -> stream.filter(m -> m.isAnnotationPresent(annotationClass)).filter(filterPredicate));
	}

//	private Method getAnnotatedMethod(Class<? extends Annotation> annotationClass, Class<?> genericReturnType,
//			List<String> includingFindStrings, List<String> excludingFindStrings,
//			Function<Method, String[]> stringAnnosFunc) {
//		Predicate<String> includingPredicate = s -> {
//			return includingFindStrings.stream().filter(pattern -> s.contains(pattern)).count() == includingFindStrings
//					.size();
//		};
//		Predicate<String> excludingPredicate = s -> {
//			return excludingFindStrings.stream().filter(pattern -> !s.contains(pattern)).count() == excludingFindStrings
//					.size();
//		};
//		Predicate<Method> annoStringTextFilterPredicate = m -> {
//			return Arrays.asList(stringAnnosFunc.apply(m)).stream().filter(includingPredicate.and(excludingPredicate))
//					.findFirst().isPresent();
//		};
//
//		List<Method> methods0 = Reflect.getMethods(getControllerClass(), stream -> stream).collect(Collectors.toList());
//		List<Method> methods1 = Reflect
//				.getMethods(getControllerClass(), stream -> stream.filter(m -> m.isAnnotationPresent(annotationClass)))
//				.collect(Collectors.toList());
//		List<Method> methods2 = Reflect
//				.getMethods(getControllerClass(),
//						stream -> stream.filter(m -> m.isAnnotationPresent(annotationClass))
//								.filter(m -> Reflect.getGenericReturnTypeClass(m) == genericReturnType))
//				.collect(Collectors.toList());
//		List<Method> methods3 = Reflect.getMethods(getControllerClass(),
//				stream -> stream.filter(m -> m.isAnnotationPresent(annotationClass))
//						.filter(m -> Reflect.getGenericReturnTypeClass(m) == genericReturnType)
//						.filter(annoStringTextFilterPredicate))
//				.collect(Collectors.toList());
//		methods1.stream().forEach(m -> System.out.println(Reflect.getGenericReturnTypeClass(m)));
//
//		return Reflect.getMethod(getControllerClass(),
//				stream -> stream.filter(m -> m.isAnnotationPresent(annotationClass))
//						.filter(m -> Reflect.getGenericReturnTypeClass(m) == genericReturnType)
//						.filter(annoStringTextFilterPredicate));
//	}

	private String getAnnoString(Class<? extends Annotation> annotationClass) {

		return "";
	}

	@SuppressWarnings("unchecked")
	public static <U extends Identifiable<UUID>> HttpConnectorSpring<U> getOrCreate(Class<?> cls) {
		if (connectorMap.containsKey(cls))
			return (HttpConnectorSpring<U>) connectorMap.get(cls);
		HttpConnectorSpring<U> connector = new HttpConnectorSpring<U>(cls);
		connectorMap.put(cls, connector);
		return connector;
	}

	@Override
	public boolean create(T t) {
		try {
			System.out.println("create " + getModelClass() + ": " + t.toString());
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			RestTemplate restTemplate = new RestTemplate();
			String url = getUrlLocalhost() + "/" + getAnnoStringControlClass() + "/" + getAnnoStringCreate();
			HttpEntity<T> requestEntity = new HttpEntity<T>(t, headers);
			restTemplate.postForLocation(url, requestEntity);
			return true;
		} catch (Exception e) {
			handleException(e);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T read(UUID id) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		RestTemplate restTemplate = new RestTemplate();
		String url = getUrlLocalhost() + "/" + getAnnoStringControlClass() + "/" + getAnnoStringRead();
		T result = (T) restTemplate.getForObject(url, getModelClass(), id);
		result.setId(id);
		return result;
	}

	@Override
	public boolean update(T t) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			RestTemplate restTemplate = new RestTemplate();
			String url = getUrlLocalhost() + "/" + getAnnoStringControlClass() + "/" + getAnnoStringUpdate();
			HttpEntity<T> requestEntity = new HttpEntity<T>(t, headers);
			restTemplate.put(url, requestEntity);
			return true;
		} catch (Exception e) {
			handleException(e);
		}
		return false;
	}

	@Override
	public boolean delete(UUID id) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			RestTemplate restTemplate = new RestTemplate();
			String url = getUrlLocalhost() + "/" + getAnnoStringControlClass() + "/" + getAnnoStringDelete();
			HttpEntity<T> requestEntity = new HttpEntity<T>(headers);
			restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Void.class, id);
			return true;
		} catch (Exception e) {
			handleException(e);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T[] readAll() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		RestTemplate restTemplate = new RestTemplate();
		String url = getUrlLocalhost() + "/" + getAnnoStringControlClass() + "/" + getAnnoStringReadAll();
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		ResponseEntity<?> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
				getArrayModelClass());
		T[] results1 = (T[]) responseEntity.getBody();
		return results1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V> T readByAttribute(V attribute, String attributeName) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		RestTemplate restTemplate = new RestTemplate();
		String url = getUrlLocalhost() + "/" + getAnnoStringControlClass() + "/"
				+ getAnnotationStringRead(attributeName);
		T result = (T) restTemplate.getForObject(url, getModelClass(), attribute);
		return result;
	}

	private void handleException(Exception e) {
		if (e instanceof org.springframework.web.client.HttpClientErrorException) {
			System.err.println(
					"Move SpringBootApplication class in correct package! ex. org.artorg.tools.phantomData.server.BootApplication");
			e.printStackTrace();
			System.exit(0);
		}
		if (e instanceof HttpServerErrorException) {
			System.err.println("///// EXCEPTION HANDLER //////");
			e = (HttpServerErrorException) e;
			System.out.println(e.toString());
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
			e.printStackTrace();
		}
		e.printStackTrace();
	}

	private final String getAnnotationString(Class<? extends Annotation> mappingClass,
			Function<Method, String[]> stringAnnoFunc) {
		Method[] methods = getControllerClass().getMethods();
		String tempValue = "";
		for (Method m : methods)
			if (m.isAnnotationPresent(mappingClass) && !m.getName().matches("(?i).*All.*")) {
				String[] stringAnnos = stringAnnoFunc.apply(m);
				tempValue = stringAnnos[0];
				break;
			}
		return annoStringExceptionHandler(tempValue, mappingClass);
	}

	public String getAnnotationStringRead(String attributeName) {
		return getAnnotationString(attributeName, GetMapping.class, stringAnnosFuncRead);
	}

	private final String getAnnotationString(String attributeName, Class<? extends Annotation> mappingClass,
			Function<Method, String[]> stringAnnoFunc) {
		Method[] methods = getControllerClass().getMethods();
		String tempValue = "";
		outer: for (Method m : methods)
			if (m.isAnnotationPresent(mappingClass) && !m.getName().matches("(?i).*All.*")) {
				String[] stringAnnos = stringAnnoFunc.apply(m);
				for (String stringAnno : stringAnnos)
					if (stringAnno.contains(attributeName)) {
						tempValue = stringAnno;
						break outer;
					}
			}
		return annoStringExceptionHandler(tempValue, mappingClass);
	}

	private final String getAnnotationStringAll(Class<? extends Annotation> mappingClass,
			Function<Method, String[]> stringAnnoFunc) {
		Method[] methods = getControllerClass().getMethods();
		String tempValue = "";
		for (Method m : methods)
			if (m.isAnnotationPresent(mappingClass) && m.getName().matches("(?i).*All.*")) {
				String[] stringAnnos = stringAnnoFunc.apply(m);
				tempValue = stringAnnos[0];
				break;
			}
		return annoStringExceptionHandler(tempValue, mappingClass);
	}

	private String annoStringExceptionHandler(String annoString, Class<? extends Annotation> mappingClass) {
		if (annoString == "")
			throw new IllegalArgumentException(String.format("Method annotation not found. Class: %s, Annotation: %s",
					getControllerClass().toString(), GetMapping.class.toString()));
		return annoString;
	}

	public static void setUrlLocalhost(String urlLocalhost) {
		HttpConnectorSpring.urlLocalhost = urlLocalhost;
	}

	// Getters
	public Class<?> getModelClass() {
		return itemClass;
	}

	public Class<?> getArrayModelClass() {
		return arrayItemClass;
	}

	public Class<?> getControllerClass() {
		return controllerClass;
	}

	public static String getUrlLocalhost() {
		return urlLocalhost;
	}

	public final String getAnnoStringControlClass() {
		return annoStringControlClass;
	}

	public final String getAnnoStringRead() {
		return annoStringRead;
	}

	public final String getAnnoStringReadAll() {
		return annoStringReadAll;
	}

	public final String getAnnoStringUpdate() {
		return annoStringUpdate;
	}

	public final String getAnnoStringCreate() {
		return annoStringCreate;
	}

	public final String getAnnoStringDelete() {
		return annoStringDelete;
	}

	@Override
	public Boolean existById(UUID id) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		RestTemplate restTemplate = new RestTemplate();
		String url = getUrlLocalhost() + "/" + getAnnoStringControlClass() + "/" + getAnnoStringRead();
		Boolean result = (Boolean) restTemplate.getForObject(url, Boolean.class, id);
		return result;
	}

}
