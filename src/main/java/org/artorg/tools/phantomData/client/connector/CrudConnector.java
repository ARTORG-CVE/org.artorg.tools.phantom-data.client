package org.artorg.tools.phantomData.client.connector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.admin.UserAdmin;
import org.artorg.tools.phantomData.client.exceptions.DeleteException;
import org.artorg.tools.phantomData.client.exceptions.NoUserLoggedInException;
import org.artorg.tools.phantomData.client.exceptions.PermissionDeniedException;
import org.artorg.tools.phantomData.client.exceptions.PostException;
import org.artorg.tools.phantomData.client.exceptions.PutException;
import org.artorg.tools.phantomData.client.logging.Logger;
import org.artorg.tools.phantomData.client.util.CollectionUtil;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.controller.ControllerSpecDefault;
import org.artorg.tools.phantomData.server.model.Identifiable;
import org.artorg.tools.phantomData.server.model.NameGeneratable;
import org.artorg.tools.phantomData.server.models.base.person.Person;
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
import org.springframework.web.client.RestTemplate;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public class CrudConnector<T> implements ICrudConnector<T> {
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

	private final ObservableMap<String, T> map;

	@SuppressWarnings("unchecked")
	public CrudConnector(Class<T> itemClass) {
		if (itemClass == null) {
			itemClass = (Class<T>) Reflect.findGenericClasstype(this);
		}
		this.itemClass = itemClass;

		map = FXCollections.observableHashMap();

		arrayItemClass = (Class<T[]>) Reflect.getArrayClass(itemClass);
		Class<?> controllerClass;
		List<Class<?>> controllerClasses =
				Reflect.getSubclasses(ControllerSpecDefault.class, Main.getReflections());
		Optional<Class<?>> optionalControllerClass = controllerClasses.stream().filter(c -> {
			try {
				return Reflect.findSubClassParameterType(c.newInstance(),
						ControllerSpecDefault.class, 0) == this.itemClass;
			} catch (Exception e2) {}
			return false;
		}).findFirst();
		if (!optionalControllerClass.isPresent()) throw new IllegalArgumentException();
		else
			controllerClass = optionalControllerClass.get();

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
		reload();
	}

	public void addListener(MapChangeListener<String, T> listener) {
		map.addListener(listener);
	}

	public void removeListener(MapChangeListener<String, T> listener) {
		map.removeListener(listener);
	}

	public static void setUrlLocalhost(String urlLocalhost) {
		CrudConnector.urlLocalhost = urlLocalhost;
	}

	public void reload() {
		List<T> itemList = Arrays.asList(readAllDb());
		map.clear();
		itemList.stream()
				.forEach(item -> map.put(((Identifiable<?>) item).getId().toString(), item));
	}

	@Override
	public List<T> readAllAsList() {
		return map.values().stream().sorted().collect(Collectors.toList());
	}

	@Override
	public T[] readAll() {
		Collection<T> list = map.values();
		T[] array = CollectionUtil.createGenericArray(itemClass, list.size());
		return readAllAsList().toArray(array);
	}

	@SuppressWarnings("unchecked")
	private T[] readAllDb() {
		long startTime = System.currentTimeMillis();
		HttpHeaders headers = createHttpHeaders();
		RestTemplate restTemplate = new RestTemplate();
		String url = createUrl(getAnnoStringReadAll());
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		ResponseEntity<?> responseEntity =
				restTemplate.exchange(url, HttpMethod.GET, requestEntity, getArrayModelClass());
		T[] results1 = (T[]) responseEntity.getBody();

		Logger.info
				.println(String.format("READ - %s - %d items (in %d ms)", itemClass.getSimpleName(),
						results1.length, (System.currentTimeMillis() - startTime)));
		return results1;
	}

	@Override
	public boolean create(T t) throws NoUserLoggedInException, PostException {
		if (createDb(t)) {
			map.put(((Identifiable<?>) t).getId().toString(), t);
			return true;
		}
		return false;
	}

	private boolean createDb(T t) throws PostException {
		if (t == null) throw new PostException(getItemClass(), " item == null");
		if (existById(((Identifiable<?>) t).getId())) return false;
		try {
			HttpHeaders headers = createHttpHeaders();
			RestTemplate restTemplate = new RestTemplate();
			String url = createUrl(getAnnoStringCreate());
			HttpEntity<T> requestEntity = new HttpEntity<T>(t, headers);
			restTemplate.postForLocation(url, requestEntity);
			if (t instanceof NameGeneratable) Logger.info.println(String.format("CREATE - %s - %s",
					itemClass.getSimpleName(), ((NameGeneratable) t).toName()));
			else
				Logger.info.println(
						String.format("CREATE - %s - %s", itemClass.getSimpleName(), t.toString()));
			return true;
		} catch (Exception e) {
			PostException e2 = new PostException(itemClass);
			e2.addSuppressed(e);
			throw e2;
		}
	}

	@Override
	public <ID> T readById(ID id) {
		if (map.containsKey(id.toString())) return (T) map.get(id.toString());
		T u = readByIdDb(id);
		map.put(((Identifiable<?>) u).getId().toString(), (T) u);
		return u;
	}

	@SuppressWarnings("unchecked")
	private <ID> T readByIdDb(ID id) {
		RestTemplate restTemplate = new RestTemplate();
		String url = createUrl(getAnnoStringRead());
		T result = (T) restTemplate.getForObject(url, getModelClass(), id);
		((Identifiable<Comparable<ID>>) result).setId((Comparable<ID>) id);

		if (result instanceof NameGeneratable) Logger.info.println(String.format("READ - %s - %s",
				itemClass.getSimpleName(), ((NameGeneratable) result).toName()));
		else
			Logger.info.println(
					String.format("READ - %s - %s", itemClass.getSimpleName(), result.toString()));

		return result;
	}

	@Override
	public boolean update(T t)
			throws NoUserLoggedInException, PutException, PermissionDeniedException {
		boolean result = updateDb(t);
		if (result)
			map.replace(((Identifiable<?>) t).getId().toString(), t);
		return result;
	}

	private boolean updateDb(T t)
			throws PutException, NoUserLoggedInException, PermissionDeniedException {
		if (t == null) throw new PutException(getItemClass(), " item == null");
		if (t instanceof Person) {
			if (!UserAdmin.isUserLoggedIn()) throw new NoUserLoggedInException();
			if (!permissionGranted() && !((Person) t).equalsId(UserAdmin.getUser()))
				throw new PermissionDeniedException();
		}
		try {
			HttpHeaders headers = createHttpHeaders();
			RestTemplate restTemplate = new RestTemplate();
			String url = createUrl(getAnnoStringUpdate());
			HttpEntity<T> requestEntity = new HttpEntity<T>(t, headers);
			restTemplate.put(url, requestEntity);
			if (t instanceof NameGeneratable) Logger.info.println(String.format("UPDATE - %s - %s",
					itemClass.getSimpleName(), ((NameGeneratable) t).toName()));
			else
				Logger.info.println(
						String.format("UPDATE - %s - %s", itemClass.getSimpleName(), t.toString()));
			return true;
		} catch (Exception e) {
			PutException e2 = new PutException(itemClass);
			e2.addSuppressed(e);
			throw e2;
		}
	}

	@Override
	public <ID> boolean deleteById(ID id)
			throws NoUserLoggedInException, DeleteException, PermissionDeniedException {
		boolean result = deleteByIdDb(id);
		if (result) map.remove(id.toString());
		return result;
	}

	private <ID> boolean deleteByIdDb(ID id)
			throws DeleteException, NoUserLoggedInException, PermissionDeniedException {
		if (id == null) throw new DeleteException(getItemClass(), " item == null");
		if (!UserAdmin.isUserLoggedIn()) throw new NoUserLoggedInException();
		if (!permissionGranted()) throw new PermissionDeniedException();
		if (getItemClass() == Person.class && UserAdmin.getHutzli().getId().equals(id)) {
			if (!UserAdmin.getUser().equalsId(UserAdmin.getHutzli()))
				throw new PermissionDeniedException();
		}
		try {
			HttpHeaders headers = createHttpHeaders();
			RestTemplate restTemplate = new RestTemplate();
			String url = createUrl(getAnnoStringDelete());
			HttpEntity<T> requestEntity = new HttpEntity<T>(headers);
			restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Void.class, id);
			Logger.info.println(
					String.format("DELETE - %s - %s", itemClass.getSimpleName(), id.toString()));
			return true;
		} catch (Exception e) {
			throw new DeleteException(getItemClass(), e);
		}
	}

	private boolean permissionGranted() {
		return UserAdmin.getUser().equalsId(UserAdmin.getAdmin())
				|| UserAdmin.getUser().equalsId(UserAdmin.getHutzli());
	}

	@Override
	public <ID> Boolean existById(ID id) {
		if (id == null) return false;
		return map.containsKey(id.toString());
	}

//	private <U extends Identifiable<ID>, ID extends Comparable<ID>> Boolean existByIdDb(ID id) {
//		RestTemplate restTemplate = new RestTemplate();
//		String url = createUrl(getAnnoStringExist());
//		Boolean result = (Boolean) restTemplate.getForObject(url, Boolean.class, id);
//		return result;
//	}

	@Override
	public <ID, V> T readByAttribute(V attribute, String attributeName) {
		T t = readByAttributeDb(attribute, attributeName);
		map.put(((Identifiable<?>) t).getId().toString(), (T) t);
		return t;
	}

	@SuppressWarnings("unchecked")
	private <U extends Identifiable<ID>, ID extends Comparable<ID>, V> U
			readByAttributeDb(V attribute, String attributeName) {
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

	private String getAnnotatedValue(String methodName, Class<? extends Annotation> annotationClass,
			Function<Method, String> stringAnnosFunc) {
		Predicate<Method> predicate1 = m -> m.getName().equals(methodName);
		Predicate<Method> predicate2 = m -> m.isAnnotationPresent(annotationClass);
		Predicate<Method> filterPredicate = predicate1.and(predicate2);
		return getAnnotatedValue(annotationClass, filterPredicate, stringAnnosFunc);
	}

	private String getAnnotatedValue(Class<? extends Annotation> annotationClass,
			Predicate<Method> methodFilterPredicate, Function<Method, String> stringAnnosFunc) {
		return stringAnnosFunc.apply(
				getAnnotatedMethod(getControllerClass(), annotationClass, methodFilterPredicate));
	}

	private static Method getAnnotatedMethod(Class<?> methodsClass,
			Class<? extends Annotation> annotationClass, Predicate<Method> filterPredicate) {
		return Reflect.getFirstMethod(methodsClass, stream -> stream
				.filter(m -> m.isAnnotationPresent(annotationClass)).filter(filterPredicate));
	}

	private final String getAnnotationString(String attributeName,
			Class<? extends Annotation> mappingClass, Function<Method, String[]> stringAnnoFunc) {
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

	private String annoStringExceptionHandler(String annoString,
			Class<? extends Annotation> mappingClass) {
		if (annoString == "") throw new IllegalArgumentException(
				String.format("Method annotation not found. Class: %s, Annotation: %s",
						getControllerClass().toString(), GetMapping.class.toString()));
		return annoString;
	}

	private final String getAnnoStringRead(String attributeName) {
		return getAnnotationString(attributeName, GetMapping.class,
				m -> m.getAnnotation(GetMapping.class).value());
	}

	@SuppressWarnings("unchecked")
	public final <U extends Identifiable<ID>, ID extends Comparable<ID>> Class<U> getModelClass() {
		return (Class<U>) itemClass;
	}

	// Getters
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

	@Override
	public Class<T> getItemClass() {
		return itemClass;
	}

}
