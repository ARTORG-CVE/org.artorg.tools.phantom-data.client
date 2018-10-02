package org.artorg.tools.phantomData.client.connector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.specification.ControllerSpec;
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

public class HttpConnectorSpring<T extends Identifiable<UUID>> extends CrudConnectors<T,UUID> {
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
	private final Class<?> itemClass;
	private final Class<?> arrayItemClass;
	private final Class<?> controllerClass;
	
	private static String urlLocalhost;
	private static final Map<Class<?>,HttpConnectorSpring<?>> connectorMap;
	
	static {
		connectorMap = new HashMap<Class<?>,HttpConnectorSpring<?>>();
	}

	public HttpConnectorSpring() {
		this(null);
	}
	
	public HttpConnectorSpring(Class<?> itemClass) {
		if (itemClass == null)
			itemClass = Reflect.findGenericClasstype(this);
		this.itemClass = itemClass;
		if (connectorMap.containsKey(this.itemClass))
			throw new IllegalArgumentException();
		
		arrayItemClass = Reflect.getArrayClass(itemClass);
		Class<?> controllerClass;
		try {
		controllerClass = Reflect.findClass(itemClass.getSimpleName() +"Controller", "org");
		} catch (Exception e) {
			List<Class<?>> controllerClasses = Reflect.getSubclasses(ControllerSpec.class, "org");
			controllerClass = controllerClasses.stream().filter(c-> {
				try {
					return Reflect.findSubClassParameterType(c.newInstance(), ControllerSpec.class, 0) == this.itemClass;
				} catch (Exception e2) {}
				return false;
			}).findFirst().orElseThrow(() -> new IllegalArgumentException());
		}
		if (controllerClass == null)
			throw new NullPointerException();
		this.controllerClass = controllerClass;
		
		stringAnnosFuncCreate = m -> m.getAnnotation(PostMapping.class).value();
		stringAnnosFuncRead = m -> m.getAnnotation(GetMapping.class).value();
		stringAnnosFuncUpdate = m -> m.getAnnotation(PutMapping.class).value();
		stringAnnosFuncDelete = m -> m.getAnnotation(DeleteMapping.class).value();
		
		// class annotation
		RequestMapping anno = getControllerClass().getAnnotation(RequestMapping.class);
		annoStringControlClass = anno.value()[0];
		annoStringExceptionHandler(annoStringControlClass, RequestMapping.class);
		
		// method annotations
		annoStringCreate = getAnnotationString(PostMapping.class, stringAnnosFuncCreate);
		annoStringRead = getAnnotationStringRead("ID");
		annoStringReadAll = getAnnotationStringAll(GetMapping.class, stringAnnosFuncRead);
		annoStringUpdate = getAnnotationString(PutMapping.class, stringAnnosFuncUpdate);
		annoStringDelete = getAnnotationString(DeleteMapping.class, stringAnnosFuncDelete);
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
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		RestTemplate restTemplate = new RestTemplate();
		String url = getUrlLocalhost() + "/" 
				+ getAnnoStringControlClass() + "/" + getAnnoStringCreate();
		HttpEntity<T> requestEntity = new HttpEntity<T>(t, headers);
		restTemplate.postForLocation(url, requestEntity);
		return true;
		} catch( Exception e) {
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
		String url = getUrlLocalhost() + "/" 
				+ getAnnoStringControlClass() + "/" + getAnnoStringRead();
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
		String url = getUrlLocalhost() + "/" 
				+ getAnnoStringControlClass() + "/" + getAnnoStringUpdate();
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
		String url = getUrlLocalhost() + "/" 
				+ getAnnoStringControlClass() + "/" + getAnnoStringDelete();
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
		String url = getUrlLocalhost() + "/" 
				+ getAnnoStringControlClass() + "/" + getAnnotationStringRead(attributeName);
		T result = (T) restTemplate.getForObject(url, getModelClass(), attribute);
		return result;
	}

	private void handleException(Exception e) {
		if (e instanceof org.springframework.web.client.HttpClientErrorException) {
			System.err.println("Move SpringBootApplication class in correct package! ex. org.artorg.tools.phantomData.server.BootApplication");
			e.printStackTrace();
			System.exit(0);
		}
		if (e instanceof HttpServerErrorException) {
			System.err.println("///// EXCEPTION HANDLER //////");
			e = (HttpServerErrorException)e;
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
	
	private final String getAnnotationString(String attributeName, 
			Class<? extends Annotation> mappingClass, 
			Function<Method, String[]> stringAnnoFunc) {
		Method[] methods = getControllerClass().getMethods();
		String tempValue = "";
		outer:
		for (Method m : methods)
			if (m.isAnnotationPresent(mappingClass) && !m.getName().matches("(?i).*All.*")) {
				String[] stringAnnos = stringAnnoFunc.apply(m);
				for (String stringAnno: stringAnnos)
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
			throw new IllegalArgumentException(
					String.format("Method annotation not found. Class: %s, Annotation: %s",
							getControllerClass().toString(), GetMapping.class.toString()));
		return annoString;
	}
	
	public static void setUrlLocalhost(String urlLocalhost) {
		HttpConnectorSpring.urlLocalhost = urlLocalhost;
	}
	
	// Getters
	public Class<?> getModelClass() {return itemClass;}
	public Class<?> getArrayModelClass() {return arrayItemClass;}
	public Class<?> getControllerClass() {return controllerClass;}
	public static String getUrlLocalhost() {return urlLocalhost;}
	public final String getAnnoStringControlClass() {return annoStringControlClass;}
	public final String getAnnoStringRead() {return annoStringRead;}
	public final String getAnnoStringReadAll() {return annoStringReadAll;}
	public final String getAnnoStringUpdate() {return annoStringUpdate;}
	public final String getAnnoStringCreate() {return annoStringCreate;}
	public final String getAnnoStringDelete() {return annoStringDelete;}

}
