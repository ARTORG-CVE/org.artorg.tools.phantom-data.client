package org.artorg.tools.phantomData.client.admin;

import java.util.List;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.connector.PersonifiedCrudConnector;
import org.artorg.tools.phantomData.client.logging.Logger;
import org.artorg.tools.phantomData.server.models.base.person.Person;

import javafx.application.Platform;

public class UserAdmin {
	private static Person user;
	private static ICrudConnector<Person> personConnector;

	static {
		personConnector = Connectors.get(Person.class);
		PersonifiedCrudConnector.setUserSupplier(() -> getUser());
	}

	public static List<Person> getAllPersons() {
		return personConnector.readAllAsList();
	}

	public static boolean isUserLoggedIn() {
		return user != null;
	}

	public static void login(Person user) {
		UserAdmin.user = user;
		if (Main.isStarted()) {
			if (user.getLastname().equals("Hutzli")) Platform.runLater(() -> {
				Main.getMainController().addDevToolsMenu();
			});
			else Platform.runLater(() -> {
				Main.getMainController().removeDevToolsMenu();
			});
		}
		Logger.info.println("Logged in as " +user.getAcademicName());
	}

	public static void logout() {
		Person person = UserAdmin.user;
		UserAdmin.user = null;
		PersonifiedCrudConnector.setUserSupplier(() -> getUser());

		if (Main.isStarted()) {
			Platform.runLater(() -> {
				Main.getMainController().removeDevToolsMenu();
			});
		}
		
		if (person != null)
		Logger.info.println("Logged out " +person.getAcademicName());
		else
			Logger.info.println("Logged out");
	}

	public static Person getUser() {
		return user;
	}

}