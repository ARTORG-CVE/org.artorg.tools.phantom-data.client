package org.artorg.tools.phantomData.client.connector;

import java.util.function.Supplier;

import org.artorg.tools.phantomData.client.exceptions.NoUserLoggedInException;
import org.artorg.tools.phantomData.server.model.base.person.Person;
import org.artorg.tools.phantomData.server.model.specification.AbstractBaseEntity;
import org.artorg.tools.phantomData.server.model.specification.AbstractPersonifiedEntity;
import org.artorg.tools.phantomData.server.specification.Identifiable;

public class PersonalizedHttpConnectorSpring<T extends Identifiable<ID>, ID extends Comparable<ID>> extends HttpConnectorSpring<T,ID> {
	private static Supplier<Person> userSupplier;
	
	static {
		userSupplier = () -> null;
	}
	
	

	

	public PersonalizedHttpConnectorSpring(Class<?> itemClass) {
		super(itemClass);
	}
	
	
	
	@Override
	public boolean create(T t) {
		return create(t, getUser());
	}
	
	private boolean create(T t, Person p) {
		if (t instanceof AbstractPersonifiedEntity) {
			((AbstractPersonifiedEntity<?>)t).setCreator(p);
			((AbstractPersonifiedEntity<?>)t).setChanger(p);
		}
		return super.create(t);
	}
	
	@Override
	public boolean update(T t) {
		return update(t, getUser());
	}
	
	private boolean update(T t, Person p) {
		if (t instanceof AbstractBaseEntity)
			((AbstractBaseEntity<?>)t).changed(p);
		return super.update(t);
	}
	
	@Override
	public boolean delete(ID id) {
		if (!isUserLoggedIn()) return false; 
		return delete(id, getUser());
	}
	
	public boolean delete(ID id, Person p) {
		return super.delete(id);
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
