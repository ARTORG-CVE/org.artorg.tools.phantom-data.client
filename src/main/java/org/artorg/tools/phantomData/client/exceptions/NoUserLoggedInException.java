package org.artorg.tools.phantomData.client.exceptions;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.controllers.LoginController;
import org.artorg.tools.phantomData.client.util.FxUtil;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;

public class NoUserLoggedInException extends Exception {
	private static final long serialVersionUID = -5671371006858399853L;

	public NoUserLoggedInException() {
		super("No user logged in");
	}

	public void showAlert() {
		Platform.runLater(() -> {
			Rectangle2D bounds = new Rectangle2D(Main.getStage().getX(), Main.getStage().getY(),
					Main.getStage().getWidth(), Main.getStage().getHeight());
			FxUtil.openFrame("Login/Logout", new LoginController(), bounds);
		});
	}

}
