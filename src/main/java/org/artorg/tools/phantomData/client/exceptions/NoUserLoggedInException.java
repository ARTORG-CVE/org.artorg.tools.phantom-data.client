package org.artorg.tools.phantomData.client.exceptions;

import org.artorg.tools.phantomData.client.controllers.LoginController;
import org.artorg.tools.phantomData.client.util.FxUtil;

import javafx.application.Platform;

public class NoUserLoggedInException extends Exception {
	private static final long serialVersionUID = -5671371006858399853L;
	
	public NoUserLoggedInException() {
		super("No user logged in");
	}

	public void showAlert() {
		Platform.runLater(() -> {
			FxUtil.openFrame("Login/Logout", new LoginController());
		});
	}

}
