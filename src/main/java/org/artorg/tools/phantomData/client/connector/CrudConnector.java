package org.artorg.tools.phantomData.client.connector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.specification.ControllerSpecDefault;
import org.artorg.tools.phantomData.server.specification.Identifiable;
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

public class CrudConnector<T extends Identifiable<?>>
	implements ICrudConnector<T> {
	private static String urlLocalhost;
	private final String annoStringControlClass;
	private final String annoStringRead;
	private final String annoStringReadAll;
	private final String annoStringCreate;
	private final String annoStringDelete;
	private final String annoStringUpdate;
	private final String annoStringExist;
	private final Class<T> itemClass;
	private final Class<T[]> arrayItemClass;
	private final Class<?> controllerClass;

	@SuppressWarnings("unchecked")
	public CrudConnector(Class<T> itemClass) {
		if (itemClass == null) {
			itemClass = (Class<T>) Reflect.findGenericClasstype(this);
		}
		this.itemClass = itemClass;

		arrayItemClass = (Class<T[]>) Reflect.getArrayClass(itemClass);
		Class<?> controllerClass;
		List<Class<?>> controllerClasses =
			Reflect.getSubclasses(ControllerSpecDefault.class, Main.getReflections());
		Optional<Class<?>> optionalControllerClass =
			controllerClasses.stream().filter(c -> {
				try {
					return Reflect.findSubClassParameterType(c.newInstance(),
						ControllerSpecDefault.class, 0) == this.itemClass;
				} catch (Exception e2) {}
				return false;
			}).findFirst();
		if (!optionalControllerClass.isPresent()) throw new IllegalArgumentException();
		else controllerClass = optionalControllerClass.get();

		if (controllerClass == null) throw new NullPointerException();
		this.controllerClass = controllerClass;

		// class annotation
		RequestMapping anno = getControllerClass().getAnnotation(RequestMapping.class);
		annoStringControlClass = anno.value()[0];
		annoStringExceptionHandler(annoStringControlClass, RequestMapping.class);

		annoStringCreate = getAnnotatedValue("create", PostMapping.class,
			m -> m.getAnnotation(PostMapping.class).value()[0]);
		annoStringRead = getAnnotatedValue("getById", GetMapping.class,
			m -> m.getAnnotation(GetMapping.class).value()[0]);
		annoStringReadAll = getAnnotatedValue("getAll", GetMapping.class,
			m -> m.getAnnotation(GetMapping.class).value()[0]);
		annoStringUpdate = getAnnotatedValue("update", PutMapping.class,
			m -> m.getAnnotation(PutMapping.class).value()[0]);
		annoStringDelete = getAnnotatedValue("delete", DeleteMapping.class,
			m -> m.getAnnotation(DeleteMapping.class).value()[0]);
		annoStringExist = getAnnotatedValue("existById", GetMapping.class,
			m -> m.getAnnotation(GetMapping.class).value()[0]);
	}

	@Override
	public boolean create(T t) {
		if (existById(t.getId())) return false;
		try {
			HttpHeaders headers = createHttpHeaders();
			RestTemplate restTemplate = new RestTemplate();
			String url = createUrl(getAnnoStringCreate());
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
	public <U extends Identifiable<ID>, ID extends Comparable<ID>> U readById(ID id) {
		RestTemplate restTemplate = new RestTemplate();
		String url = createUrl(getAnnoStringRead());
		U result = (U) restTemplate.getForObject(url, getModelClass(), id);
		result.setId(id);
		return result;
	}

	@Override
	public boolean update(T t) {
		try {
			HttpHeaders headers = createHttpHeaders();
			RestTemplate restTemplate = new RestTemplate();
			String url = createUrl(getAnnoStringUpdate());
			HttpEntity<T> requestEntity = new HttpEntity<T>(t, headers);
			restTemplate.put(url, requestEntity);
			return true;
		} catch (Exception e) {
			handleException(e);
		}
		return false;
	}

	@Override
	public <U extends Identifiable<ID>, ID extends Comparable<ID>> boolean deleteById(ID id) {
		try {
			HttpHeaders headers = createHttpHeaders();
			RestTemplate restTemplate = new RestTemplate();
			String url = createUrl(getAnnoStringDelete());
			HttpEntity<T> requestEntity = new HttpEntity<T>(headers);
			restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Void.class, id);
			return true;
		} catch (Exception e) {
			handleException(e);
		}
		return false;
	}

	@Override
	public <U extends Identifiable<ID>, ID extends Comparable<ID>> Boolean existById(ID id) {
		RestTemplate restTemplate = new RestTemplate();
		String url = createUrl(getAnnoStringExist());
		Boolean result = (Boolean) restTemplate.getForObject(url, Boolean.class, id);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T[] readAll() {
		HttpHeaders headers = createHttpHeaders();
		RestTemplate restTemplate = new RestTemplate();
		String url = createUrl(getAnnoStringReadAll());
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		ResponseEntity<?> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
			requestEntity, getArrayModelClass());
		T[] results1 = (T[]) responseEntity.getBody();
		return results1;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <U extends Identifiable<ID>, ID extends Comparable<ID>, V> U readByAttribute(V attribute, String attributeName) {
		RestTemplate restTemplate = new RestTemplate();
		String url = getUrlLocalhost() + "/" + getAnnoStringControlClass() + "/"
			+ getAnnoStringRead(attributeName);
		U result = (U) restTemplate.getForObject(url, getModelClass(), attribute);
		return result;
	}

	private HttpHeaders createHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	private String createUrl(String mappingAnno) {
		return getUrlLocalhost() + "/" + getAnnoStringControlClass() + "/" + mappingAnno;
	}

	private void handleException(Exception e) {
		if (e instanceof org.springframework.web.client.HttpClientErrorException) {
			System.err.println(
				"Move SpringBootApplication class in correct package! ex. org.artorg.tools.phantomData.server.BootApplication");
			e.printStackTrace();
			System.exit(0);
		}
		if (e instanceof HttpServerErrorException) {
			e = (HttpServerErrorException) e;
			System.err.println(e.toString());
			System.err.println(e.getMessage());
			System.err.println(e.getCause());
			e.printStackTrace();
		}
		e.printStackTrace();
	}

	private String getAnnotatedValue(String methodName,
		Class<? extends Annotation> annotationClass,
		Function<Method, String> stringAnnosFunc) {
		Predicate<Method> predicate1 = m -> m.getName().equals(methodName);
		Predicate<Method> predicate2 = m -> m.isAnnotationPresent(annotationClass);
//	Predicate<Method> predicate3 = m -> Modifier.isPublic(m.getModifiers());
//	Predicate<Method> predicate4 = m -> !Modifier.isAbstract(m.getModifiers());
		Predicate<Method> filterPredicate = predicate1.and(predicate2)
//			.and(predicate3).and(predicate4)
		;
		return getAnnotatedValue(annotationClass, filterPredicate, stringAnnosFunc);
	}
	
	private String getAnnotatedValue(Class<? extends Annotation> annotationClass,
		Predicate<Method> methodFilterPredicate,
		Function<Method, String> stringAnnosFunc) {
		return stringAnnosFunc.apply(getAnnotatedMethod(getControllerClass(),
			annotationClass, methodFilterPredicate));
	}

	private static Method getAnnotatedMethod(Class<?> methodsClass,
		Class<? extends Annotation> annotationClass, Predicate<Method> filterPredicate) {
		return Reflect.getFirstMethod(methodsClass, stream -> stream
			.filter(m -> m.isAnnotationPresent(annotationClass)).filter(filterPredicate));
	}

	

	private final String getAnnotationString(String attributeName,
		Class<? extends Annotation> mappingClass,
		Function<Method, String[]> stringAnnoFunc) {
		Method[] methods = getControllerClass().getMethods();
		String tempValue = "";
		outer: for (Method m : methods)
			if (m.isAnnotationPresent(mappingClass)
				&& !m.getName().matches("(?i).*All.*")) {
				String[] stringAnnos = stringAnnoFunc.apply(m);
				for (String stringAnno : stringAnnos)
					if (stringAnno.contains(attributeName)) {
						tempValue = stringAnno;
						break outer;
					}
			}
		return annoStringExceptionHandler(tempValue, mappingClass);
	}

	private String annoStringExceptionHandler(String annoString,
		Class<? extends Annotation> mappingClass) {
		if (annoString == "") throw new IllegalArgumentException(
			String.format("Method annotation not found. Class: %s, Annotation: %s",
				getControllerClass().toString(), GetMapping.class.toString()));
		return annoString;
	}

	public static void setUrlLocalhost(String urlLocalhost) {
		CrudConnector.urlLocalhost = urlLocalhost;
	}
	
	public final String getAnnoStringRead(String attributeName) {
		return getAnnotationString(attributeName, GetMapping.class,
			m -> m.getAnnotation(GetMapping.class).value());
	}

	// Getters
	@SuppressWarnings("unchecked")
	public final <U extends Identifiable<ID>, ID extends Comparable<ID>> Class<U> getModelClass() {
		return (Class<U>) itemClass;
	}

	public final Class<T[]> getArrayModelClass() {
		return arrayItemClass;
	}

	public final Class<?> getControllerClass() {
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

	public final String getAnnoStringExist() {
		return annoStringExist;
	}

}
