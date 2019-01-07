package org.artorg.tools.phantomData.client.controllers;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.admin.UserAdmin;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.scene.control.VGridBoxPane;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.models.base.person.Person;

import javafx.collections.FXCollections;
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
		Collection<Person> persons = Connectors.get(Person.class).readAllAsSet().stream()
				.filter(person -> person.isActive()).collect(Collectors.toList());
		personChoiceBox.setItems(FXCollections.observableArrayList(persons));
		FxUtil.setComboBoxCellFactory(personChoiceBox, p -> {
			if (p.equalsId(UserAdmin.getAdmin()))
				return "admin";
			return p.getAcademicName();
		});

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
			pwdField.setText("");
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
		if (user.equalsId(UserAdmin.getAdmin())) {
			Date date = new Date();
			LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			int month = localDate.getMonthValue();
			final int[] codes =
					new int[] { 356, 791, 324, 681, 992, 231, 533, 875, 391, 118, 823, 886 };
			int code = codes[month-1];
			String input = pwdField.getText();
			if (input.length() < 5) return;
			String[] splits = new String[] { input.substring(0, input.length() - 3),
					input.substring(input.length() - 3, input.length()) };
			if (splits[0].equals(user.getPassword()) && splits[1].equals(Integer.toString(code))) {
				UserAdmin.login(user);
				updateActiveUserLabel();
			}
		}
		if (pwdField.getText().equals(user.getPassword())) {
			UserAdmin.login(user);
			updateActiveUserLabel();
		}
	}

	private void updateActiveUserLabel() {
		Person user = UserAdmin.getUser();
		String username = "-";
		if (user != null) username = user.getAcademicName();
		activeUser.setText(username);
	}

}
