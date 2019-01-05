package org.artorg.tools.phantomData.client.exceptions;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class PermissionDeniedException extends Exception {
	private static final long serialVersionUID = -907156658561801117L;

	public PermissionDeniedException() {
		super("Logged in as wrong user for activity");
	}

	public void showAlert() {
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Permission denied");
			alert.setContentText("Not enough rights.\nLogged in as wrong user for activity");
			alert.showAndWait();
		});
	}
}
