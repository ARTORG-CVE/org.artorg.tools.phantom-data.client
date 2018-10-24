package org.artorg.tools.phantomData.client.admin;

import java.util.List;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connector.PersonalizedHttpConnectorSpring;
import org.artorg.tools.phantomData.server.model.Person;

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
	
	public boolean isUserLoggedIn() {
		return user != null;
	}

	public static void login(Person user) {
		UserAdmin.user = user;
		System.out.println("User logged in: " +user.getAcademicName());
	}
	
	public static void logout() {
		if (user != null)
			System.out.println("User logged out: " +user.getAcademicName());
		UserAdmin.user = null;
		PersonalizedHttpConnectorSpring.setUserSupplier(() -> getUser());
		
	}
	
	public static Person getUser() {
		return user;
	}

}