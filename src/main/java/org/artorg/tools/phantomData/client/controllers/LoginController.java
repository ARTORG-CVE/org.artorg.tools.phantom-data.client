package org.artorg.tools.phantomData.client.controllers;

import org.artorg.tools.phantomData.client.admin.UserAdmin;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.scene.control.VGridBoxPane;
import org.artorg.tools.phantomData.server.models.base.person.Person;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;

public class LoginController extends VGridBoxPane {
	private final Label activeUser;
	private final ComboBox<Person> personChoiceBox;
	private final PasswordField pwdField;

	public LoginController() {
		activeUser = new Label();
		personChoiceBox = new ComboBox<Person>();
		pwdField = new PasswordField();

		updateActiveUserLabel();

		super.addColumn(80.0);
		super.addColumn(180.0);

		personChoiceBox.setMaxWidth(Double.MAX_VALUE);
		ItemEditor.createDbComboBox(personChoiceBox, Person.class, p -> p.getAcademicName());
		pwdField.setText("123456789");

		addRow("Users", personChoiceBox);
		addRow("User", activeUser);
		addRow("Password", pwdField);

		Button loginButton = new Button("Login");
		loginButton.setOnAction(event -> {
			login();
		});
		AnchorPane loginButtonPane = createButtonPane(loginButton);
		getvBox().getChildren().add(loginButtonPane);

		Button logoutButton = new Button("Logout");
		logoutButton.setOnAction(event -> {
			UserAdmin.logout();
			updateActiveUserLabel();
			pwdField.setText("123456789");
		});
		AnchorPane logoutButtonPane = createButtonPane(logoutButton);
		getvBox().getChildren().add(logoutButtonPane);

		pwdField.setOnAction(event -> {
			login();
		});

		this.setPadding(new Insets(10, 10, 10, 10));

	}

	private void login() {
		Person user = personChoiceBox.getSelectionModel().getSelectedItem();
		if (user != null) {
			if (user.getFirstname().equals("Marc")) {
				if (pwdField.getText().equals("swordfish")) {
					UserAdmin.login(user);
					updateActiveUserLabel();
				}
			} else {
				if (pwdField.getText().equals("123456789")) {
					UserAdmin.login(user);
					updateActiveUserLabel();
				}
			}
		}
	}

	private void updateActiveUserLabel() {
		Person user = UserAdmin.getUser();
		String username = "-";
		if (user != null) username = user.getAcademicName();
		activeUser.setText(username);
	}

}
