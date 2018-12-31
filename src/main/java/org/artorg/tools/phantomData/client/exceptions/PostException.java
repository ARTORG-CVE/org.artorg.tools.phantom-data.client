package org.artorg.tools.phantomData.client.exceptions;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class PostException extends Exception {
	private static final long serialVersionUID = -7064433845072094188L;
	private final Class<?> itemClass;

	public PostException(Class<?> itemClass) {
		this(itemClass, itemClass.getSimpleName() + " could not created");
	}

	public PostException(Class<?> itemClass, String message) {
		super(message);
		this.itemClass = itemClass;
	}

	public void showAlert() {
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Create " + itemClass.getSimpleName());
			alert.setContentText(String.format("Can't create %s:\n%s",
					getItemClass().getSimpleName(), getMessage()));
			alert.showAndWait();
		});
	}
	
	public Class<?> getItemClass() {
		return itemClass;
	}

}
