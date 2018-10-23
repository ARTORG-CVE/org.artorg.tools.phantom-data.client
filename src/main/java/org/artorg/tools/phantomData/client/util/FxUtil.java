package org.artorg.tools.phantomData.client.util;

import static huma.io.IOutil.readResource;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;

public class FxUtil {
	private static Class<?> mainClass;
	
	static {
		mainClass = null;
	}
	
	public static void addMenuItem(ContextMenu rowMenu,
		String name,
		EventHandler<ActionEvent> eventHandler) {
		MenuItem menuItem = new MenuItem(name);
		menuItem.setOnAction(eventHandler);
		rowMenu.getItems().add(menuItem);
	}
	
	public static void setMainFxClass(Class<?> mainClass) {
		if (FxUtil.mainClass!=null) throw new UnsupportedOperationException();
		FxUtil.mainClass = mainClass;
	}
	
	public static <T> T loadFXML(String path, Object controller) {
		FXMLLoader loader = new FXMLLoader(getMainClass().getClassLoader().getResource(path));
		loader.setController(controller);
		try {
			return loader.<T>load();
		} catch (IOException e) {}
		throw new IllegalArgumentException("path: " +path);
	}
	
	public static String readCSSstylesheet(String path) {
		return readResource(path).toExternalForm();
	}
	
	public static Class<?> getMainClass() {
		if (FxUtil.mainClass == null) 
			throw new IllegalArgumentException();
		return FxUtil.mainClass;
	}
	
	public static void addToAnchorPane(AnchorPane parentPane, Node child) {
		parentPane.getChildren().add(child);
		setAnchorZero(child);
	}
	
	public static void setAnchorZero(Node node) {
		AnchorPane.setBottomAnchor(node, 0.0);
    	AnchorPane.setLeftAnchor(node, 0.0);
    	AnchorPane.setRightAnchor(node, 0.0);
    	AnchorPane.setTopAnchor(node, 0.0);
	}

	public static void runNewSingleThreaded(Runnable rc) {
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
				rc.run();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		task.setOnSucceeded(taskEvent -> {
		});
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(task);
		executor.shutdown();
	}
}
