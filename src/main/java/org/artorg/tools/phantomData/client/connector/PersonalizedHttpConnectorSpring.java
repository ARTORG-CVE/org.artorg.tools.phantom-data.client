package org.artorg.tools.phantomData.client.connector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.artorg.tools.phantomData.client.exceptions.NoUserLoggedInException;
import org.artorg.tools.phantomData.server.model.Person;
import org.artorg.tools.phantomData.server.model.specification.AbstractBaseEntity;
import org.artorg.tools.phantomData.server.specification.Identifiable;

public class PersonalizedHttpConnectorSpring<T extends Identifiable<UUID>> extends HttpConnectorSpring<T> {
	private static Supplier<Person> userSupplier;
	
	static {
		userSupplier = () -> null;
	}
	
	protected static final Map<Class<?>, PersonalizedHttpConnectorSpring<?>> connectorMap;

	static {
		connectorMap = new HashMap<Class<?>, PersonalizedHttpConnectorSpring<?>>();
	}

	public PersonalizedHttpConnectorSpring(Class<?> itemClass) {
		super(itemClass);
	}
	
	@SuppressWarnings("unchecked")
	public static <U extends Identifiable<UUID>> PersonalizedHttpConnectorSpring<U> getOrCreate(Class<?> cls) {
		if (connectorMap.containsKey(cls))
			return (PersonalizedHttpConnectorSpring<U>) connectorMap.get(cls);
		
		PersonalizedHttpConnectorSpring<U> connector = new PersonalizedHttpConnectorSpring<U>(cls);
		connectorMap.put(cls, connector);
		return connector;
	}
	
	@Override
	public boolean create(T t) {
		return create(t, getUser());
	}
	
	private boolean create(T t, Person p) {
		if (t instanceof AbstractBaseEntity) {
			((AbstractBaseEntity<?>)t).setCreator(p);
			((AbstractBaseEntity<?>)t).setChanger(p);
		}
		return super.create(t);
	}
	
	@Override
	public boolean update(T t) {
		return update(t, getUser());
	}
	
	private boolean update(T t, Person p) {
		if (t instanceof AbstractBaseEntity)
			((AbstractBaseEntity<?>)t).setChanger(p);
		return super.update(t);
	}
	
	public Person getUser() throws NoUserLoggedInException {
		Person user = userSupplier.get();
		if (user == null)
			throw new NoUserLoggedInException();
		return user;
	}
	
	public boolean isUserLoggedIn() {
		Person user = userSupplier.get();
		return user != null;
	}

	public static Supplier<Person> getUserSupplier() {
		return userSupplier;
	}

	public static void setUserSupplier(Supplier<Person> userSupplier) {
		PersonalizedHttpConnectorSpring.userSupplier = userSupplier;
	}

}
