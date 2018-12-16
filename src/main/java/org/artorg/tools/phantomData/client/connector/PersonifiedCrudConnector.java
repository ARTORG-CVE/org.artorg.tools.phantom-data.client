package org.artorg.tools.phantomData.client.connector;

import java.util.function.Supplier;

import org.artorg.tools.phantomData.client.exceptions.NoUserLoggedInException;
import org.artorg.tools.phantomData.server.model.base.person.Person;
import org.artorg.tools.phantomData.server.model.specification.AbstractPersonifiedEntity;
import org.artorg.tools.phantomData.server.model.specification.AbstractPropertifiedEntity;

public class PersonifiedCrudConnector<T> extends CrudConnector<T> {
	private static Supplier<Person> userSupplier;
	
	static {
		userSupplier = () -> null;
	}

	public PersonifiedCrudConnector(Class<T> itemClass) {
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
		if (t instanceof AbstractPropertifiedEntity)
			((AbstractPropertifiedEntity<?>)t).changed(p);
		return super.update(t);
	}
	
	@Override
	public <ID> boolean deleteById(ID id) {
		if (!isUserLoggedIn()) return false; 
		return deleteById(id, getUser());
	}
	
	public <ID> boolean deleteById(ID id, Person p) {
		return super.deleteById(id);
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
		PersonifiedCrudConnector.userSupplier = userSupplier;
	}

}
