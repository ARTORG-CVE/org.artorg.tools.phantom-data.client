package org.artorg.tools.phantomData.client.connector;

import java.util.Date;
import java.util.function.Supplier;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.admin.UserAdmin;
import org.artorg.tools.phantomData.client.exceptions.NoUserLoggedInException;
import org.artorg.tools.phantomData.server.model.AbstractPersonifiedEntity;
import org.artorg.tools.phantomData.server.models.base.person.Person;

import javafx.application.Platform;

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
		if (!UserAdmin.isUserLoggedIn()) {
			Platform.runLater(() -> {
			Main.getMainController().openLoginLogoutFrame();
			});
			return false;
		}
		Person user = getUser();
		if (AbstractPersonifiedEntity.class.isAssignableFrom(t.getClass())) {
			AbstractPersonifiedEntity<?> item = ((AbstractPersonifiedEntity<?>)t);
			item.setCreator(user);
			item.setDateAdded(new Date());
			item.setChanger(user);
			item.setDateLastModified(new Date());
		}
		return super.create(t);
	}
	
	@Override
	public boolean update(T t) {
		if (!UserAdmin.isUserLoggedIn()) {
			Platform.runLater(() -> {
			Main.getMainController().openLoginLogoutFrame();
			});
			return false;
		}
		Person user = getUser();
		if (AbstractPersonifiedEntity.class.isAssignableFrom(t.getClass())) {
			AbstractPersonifiedEntity<?> item = ((AbstractPersonifiedEntity<?>)t);
			item.setChanger(user);
			item.setDateLastModified(new Date());
		}
		return super.update(t);
	}
	
	@Override
	public <ID> boolean deleteById(ID id) {
		if (!UserAdmin.isUserLoggedIn()) {
			Platform.runLater(() -> {
			Main.getMainController().openLoginLogoutFrame();
			});
			return false;
		}
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
