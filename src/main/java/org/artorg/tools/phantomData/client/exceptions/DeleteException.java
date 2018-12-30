package org.artorg.tools.phantomData.client.exceptions;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class DeleteException extends Exception {
	private static final long serialVersionUID = -823798714874196379L;
	private final Class<?> itemClass;

	public DeleteException(Class<?> itemClass) {
		this(itemClass, itemClass.getSimpleName() + " could not delete");
	}

	public DeleteException(Class<?> itemClass, String message) {
		super(message);
		this.itemClass = itemClass;
	}

	public Class<?> getItemClass() {
		return itemClass;
	}

	public void showAlert() {
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Delete " + itemClass.getSimpleName());
			alert.setContentText(String.format("Can't delete %s:\n%s",
					getItemClass().getSimpleName(), getMessage()));
			alert.showAndWait();
		});
	}
	
}
