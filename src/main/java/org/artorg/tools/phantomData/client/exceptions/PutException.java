package org.artorg.tools.phantomData.client.exceptions;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class PutException extends Exception {
	private static final long serialVersionUID = -8378458121017548473L;
	private final Class<?> itemClass;

	public PutException(Class<?> itemClass) {
		this(itemClass, itemClass.getSimpleName() +" could not created");
	}
	
	public PutException(Class<?> itemClass, String message) {
		super(message);
		this.itemClass = itemClass;
	}
	
	public Class<?> getItemClass() {
		return itemClass;
	}

	public void showAlert() {
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Put " + itemClass.getSimpleName());
			alert.setContentText(String.format("Can't save %s:\n%s",
					getItemClass().getSimpleName(), getMessage()));
			alert.showAndWait();
		});
	}
	
}