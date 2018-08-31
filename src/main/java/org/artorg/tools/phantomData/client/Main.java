package org.artorg.tools.phantomData.client;

import static org.artorg.tools.phantomData.server.boot.BootUtils.isConnected;
import static org.artorg.tools.phantomData.server.boot.BootUtils.logInfos;
import static org.artorg.tools.phantomData.server.boot.BootUtils.prepareFileStructure;
import static org.artorg.tools.phantomData.server.boot.BootUtils.startingServer;

import org.artorg.tools.phantomData.client.boot.Launcher;
import org.artorg.tools.phantomData.client.control.MainController;
import org.artorg.tools.phantomData.client.io.ResourceReader;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {
	
    public static void main( String[] args ) {	
    	Launcher launcher = new Launcher();
    	launcher.launch(192, () -> {
	    	new Thread(() -> startingServer(args)).start();
			
			while(!isConnected()) {
				try {Thread.sleep(1000);
				} catch (InterruptedException e) {e.printStackTrace();}
			}
	
			prepareFileStructure();
			logInfos();
    	});
		
    	try {
    		launch(args);
    	} catch(Exception e) {
    		launcher.showConsoleFrame();
    		e.printStackTrace();
    	}
    	
    }
    
    @Override
	public void start(Stage stage) throws Exception {
		MainController controller = new MainController(stage);
		AnchorPane pane = ResourceReader.<AnchorPane>loadFXML("fxml/Table.fxml", controller);
		
    	Scene scene = new Scene(pane);
		scene.getStylesheets().add(ResourceReader.readCSSstylesheet("css/application.css"));
		
		stage.setScene(scene);
		stage.setTitle("Phantom Database");
		stage.setWidth(800);
		stage.setHeight(500);
		stage.show();
		stage.requestFocus();
		stage.toFront();
		
	}
    
}
