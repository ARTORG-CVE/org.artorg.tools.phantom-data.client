package org.artorg.tools.phantomData.client.controllers;

import org.artorg.tools.phantomData.client.admin.UserAdmin;
import org.artorg.tools.phantomData.client.connector.PersonalizedHttpConnectorSpring;
import org.artorg.tools.phantomData.client.scene.control.VGridBoxPane;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.person.Person;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class LoginController extends VGridBoxPane {
	private final Label activeUser;

	public LoginController() {
		activeUser = new Label();
		activeUser.setText("-");
		
		super.addColumn(80.0);
		super.addColumn(180.0);
		
		ComboBox<Person> personChoiceBox = new ComboBox<Person>();
		personChoiceBox.setMaxWidth(Double.MAX_VALUE);
		FxUtil.createDbComboBox(personChoiceBox,
			PersonalizedHttpConnectorSpring.getOrCreate(Person.class),
			p -> p.getAcademicName());
		addRow("Users", personChoiceBox);
		
		addRow("User", activeUser);
		
		Button loginButton = new Button("Login");
		loginButton.setOnAction(event -> {
			Person user = personChoiceBox.getSelectionModel().getSelectedItem();
			if (user != null) {
				UserAdmin.login(user);
				updateActiveUserLabel();

			}
		});
		AnchorPane loginButtonPane = createButtonPane(loginButton);
		getvBox().getChildren().add(loginButtonPane);

		Button logoutButton = new Button("Logout");
		logoutButton.setOnAction(event -> {
			UserAdmin.logout();
			updateActiveUserLabel();
			});
		AnchorPane logoutButtonPane = createButtonPane(logoutButton);
		getvBox().getChildren().add(logoutButtonPane);
		
		this.setPadding(new Insets(10, 10, 10, 10));

	}
	
	private void updateActiveUserLabel() {
		Person user = UserAdmin.getUser();
		String username = "-"; 
		if (user != null)
			username = user.getAcademicName();
		activeUser.setText(username);
	}

}
