package org.artorg.tools.phantomData.client;

import java.io.IOException;

import org.artorg.tools.phantomData.client.boot.LaunchConfigurationsClient;
import org.artorg.tools.phantomData.client.control.MainController;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.BootApplication;
import org.artorg.tools.phantomData.server.boot.LaunchConfigurationsServer;
import org.artorg.tools.phantomData.server.boot.ServerLauncher;
import org.artorg.tools.phantomData.server.io.IOutil;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {
	
    public static void main( String[] args ) {
    	ServerLauncher launcher = new ServerLauncher();
		launcher.launch(BootApplication.class, LaunchConfigurationsClient.START_SERVER, args);
//    	
//    	
//    	Launcher launcher = new Launcher();
//    	launcher.launch(192, () -> {
//	    	new Thread(() -> startingServer(args)).start();
//			
//			while(!isConnected()) {
//				try {Thread.sleep(1000);
//				} catch (InterruptedException e) {e.printStackTrace();}
//			}
//	
//			prepareFileStructure();
//			logInfos();
//    	});
		
		FxUtil.setMainFxClass(Main.class);
		
    	try {
    		launch(args);
    	} catch(Exception e) {
    		launcher.setShowStartupConsole(true);
    		e.printStackTrace();
    	}
    	
    }
    
    @Override
	public void start(Stage stage) throws Exception {    	
		MainController controller = new MainController(stage);
		AnchorPane pane = FxUtil.loadFXML("fxml\\Table.fxml", controller);
		
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
