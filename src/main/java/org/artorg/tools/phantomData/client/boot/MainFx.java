package org.artorg.tools.phantomData.client.boot;

import java.util.UUID;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.controllers.MainController;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.BootApplication;
import org.artorg.tools.phantomData.server.beans.BeanMap;
import org.artorg.tools.phantomData.server.model.Phantom;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainFx extends Application {
	private static boolean isStarted;

	{
		isStarted = false;
	}
    
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
		isStarted = true;
		
		BeanMap beanMap = BootApplication.getBeanmap();
		
		System.out.println(beanMap.getMap().size());
		
//		HttpConnectorSpring<Phantom> phantomConn = HttpConnectorSpring.getOrCreate(Phantom.class);
//		
//		UUID id = phantomConn.readAllAsList().get(0).getId();
//		System.out.println(id.toString());
//		
//		System.out.println(phantomConn.existById(id));
//		System.out.println(phantomConn.existById(UUID.randomUUID()));
	}
    
    public static boolean isStarted() {
		return isStarted;
	}
    
}
