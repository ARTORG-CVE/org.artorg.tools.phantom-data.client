package org.artorg.tools.phantomData.client.exceptions;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class InvalidUIInputException extends Exception {
	private static final long serialVersionUID = -4908511201073823084L;
	private final Class<?> itemClass;
	private Mode mode;
	
	
	
	public InvalidUIInputException(Class<?> itemClass) {
		this(itemClass, Mode.UNKNOWN, "Invalid input(s) in user interface");
	}
	
	public InvalidUIInputException(Class<?> itemClass, String message) {
		this(itemClass, Mode.UNKNOWN, message);
	}
	
	public InvalidUIInputException(Class<?> itemClass, Mode mode) {
		this(itemClass, mode, "Invalid input(s) in user interface");
	}
	
	public InvalidUIInputException(Class<?> itemClass, Mode mode, String message) {
		super(message);
		this.itemClass = itemClass;
		this.mode = mode;
	}
	
	public enum Mode {
		CREATE, EDIT, UNKNOWN
	}
	
	public void showAlert() {
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.WARNING);
			if (mode == Mode.CREATE) {
				alert.setTitle("Create " + itemClass.getSimpleName());
				alert.setContentText(String.format("Can't create %s:\n%s",
						itemClass.getSimpleName(), getMessage()));
			} else if (mode == Mode.EDIT){
				alert.setTitle("Edit " + itemClass.getSimpleName());
				alert.setContentText(String.format("Can't edit %s:\n%s",
						itemClass.getSimpleName(), getMessage()));
			} else {
				alert.setTitle("Apply changes on " + itemClass.getSimpleName());
				alert.setContentText(String.format("Can't apply changes on %s:\n%s",
						itemClass.getSimpleName(), getMessage()));
			}
			alert.showAndWait();
		});
	}
	
	public Class<?> getItemClass() {
		return itemClass;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

}
