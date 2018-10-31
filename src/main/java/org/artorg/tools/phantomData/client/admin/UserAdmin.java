package org.artorg.tools.phantomData.client.admin;

import java.util.List;

import org.artorg.tools.phantomData.client.boot.MainFx;
import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connector.PersonalizedHttpConnectorSpring;
import org.artorg.tools.phantomData.server.model.person.Person;

public class UserAdmin {
	private static Person user;
	private static HttpConnectorSpring<Person> personConnector;
	
	static {
		personConnector = HttpConnectorSpring.getOrCreate(Person.class);
		PersonalizedHttpConnectorSpring.setUserSupplier(() -> getUser());
	}
	
	public static List<Person> getAllPersons() {
		return personConnector.readAllAsList();
	}
	
	public static boolean isUserLoggedIn() {
		return user != null;
	}

	public static void login(Person user) {
		UserAdmin.user = user;
		
		if (user.getLastname().equals("Hutzli"))
			MainFx.getMainController().addDevToolsMenu();
		else
			MainFx.getMainController().removeDevToolsMenu();
	}
	
	public static void logout() {
		UserAdmin.user = null;
		PersonalizedHttpConnectorSpring.setUserSupplier(() -> getUser());
		
		MainFx.getMainController().removeDevToolsMenu();
		
	}
	
	public static Person getUser() {
		return user;
	}

}