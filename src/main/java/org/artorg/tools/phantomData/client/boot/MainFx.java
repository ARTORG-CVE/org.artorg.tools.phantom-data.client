package org.artorg.tools.phantomData.client.boot;

import org.artorg.tools.phantomData.client.controllers.MainController;
import org.artorg.tools.phantomData.client.util.FxUtil;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainFx extends Application {
	private static boolean isStarted;
	private static Scene scene;
	private static Stage stage;
	private static MainController mainController;

	{
		isStarted = false;
	}
    
    @SuppressWarnings("static-access")
	@Override
	public void start(Stage stage) throws Exception {
    	this.stage = stage;
    	mainController = new MainController(stage);
    	StackPane pane = mainController;
		
    	scene = new Scene(pane);
		scene.getStylesheets().add(FxUtil.readCSSstylesheet("css/application.css"));
		
		stage.setScene(scene);
		stage.setTitle("Phantom Database");
		stage.setWidth(800);
		stage.setHeight(500);
		stage.show();
		stage.requestFocus();
		stage.toFront();
		isStarted = true;
	}
    
    public static void openFrame(String title, Node node) {
    	Stage stage = new Stage();
		AnchorPane root = new AnchorPane();
		Scene scene = new Scene(root);
		
		FxUtil.addToPane(root, node);
		scene.setRoot(root);
		
		stage.setScene(scene);
		stage.sizeToScene();
		stage.setTitle(title);
		stage.show();
    }
    
    public static boolean isStarted() {
		return isStarted;
	}
    
    public static Scene getScene() {
		return scene;
	}

	public static void setScene(Scene scene) {
		MainFx.scene = scene;
	}

	public static Stage getStage() {
		return stage;
	}

	public static void setStage(Stage stage) {
		MainFx.stage = stage;
	}
	
	public static MainController getMainController() {
		return mainController;
	}

	public static void setMainController(MainController mainController) {
		MainFx.mainController = mainController;
	}
    
}
