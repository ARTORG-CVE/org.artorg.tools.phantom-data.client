package org.artorg.tools.phantomData.client.controllers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.controlsfx.dialog.ProgressDialog;

import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.StageStyle;

public class SimpleProgressDialog {
	private final ProgressDialog dialog;
	private final Task<?> task;

	public SimpleProgressDialog(Task<?> task, String title) {
		this.task = task;
		this.dialog = new ProgressDialog(task);
		
//		dialog.setContentText(contentText);
		dialog.setTitle(title);
//		dialog.setHeaderText(headerText);
		dialog.setGraphic(null);
		dialog.initStyle(StageStyle.UTILITY);

		DialogPane dialogPane = dialog.getDialogPane();
		dialogPane.getButtonTypes().add(ButtonType.CANCEL);
		Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
		cancelButton.setOnAction(evt -> {
			task.cancel();
			dialog.close();
		});

	}

	public void showAndWait() {
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.submit(task);
		dialog.showAndWait();
	}

	public ProgressDialog getDialog() {
		return dialog;
	}

	public Task<?> getTask() {
		return task;
	}

}
