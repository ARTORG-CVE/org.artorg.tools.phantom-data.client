package org.artorg.tools.phantomData.client.exceptions;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class DeleteException extends Exception {
	private static final long serialVersionUID = -823798714874196379L;
	private final Class<?> itemClass;

	public DeleteException(Class<?> itemClass, Exception suppressedException) {
		this(itemClass);
		this.addSuppressed(suppressedException);
	}
	
	public DeleteException(Class<?> itemClass) {
		this(itemClass, itemClass.getSimpleName() + " could not deleted");
	}

	public DeleteException(Class<?> itemClass, String message) {
		super(message);
		this.itemClass = itemClass;
	}

	public void showAlert() {
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Delete " + itemClass.getSimpleName());
			alert.setContentText(getMessage());
			alert.showAndWait();
		});
	}

	public Class<?> getItemClass() {
		return itemClass;
	}
	
}
