package org.artorg.tools.phantomData.client.boot;

import org.artorg.tools.phantomData.client.controllers.MainController;
import org.artorg.tools.phantomData.client.util.FxUtil;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainFx extends Application {
    
    @Override
	public void start(Stage stage) throws Exception {
		MainController controller = new MainController(stage);
		AnchorPane pane = FxUtil.loadFXML("fxml/Table.fxml", controller);
		controller.init();
		
    	Scene scene = new Scene(pane);
		scene.getStylesheets().add(FxUtil.readCSSstylesheet("css/application.css"));
		
		stage.setScene(scene);
		stage.setTitle("Phantom Database");
		stage.setWidth(800);
		stage.setHeight(500);
		stage.show();
		stage.requestFocus();
		stage.toFront();
		
	}
}
