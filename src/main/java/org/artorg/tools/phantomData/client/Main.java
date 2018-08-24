package org.artorg.tools.phantomData.client;

import static org.artorg.tools.phantomData.server.boot.BootUtils.logInfos;
import static org.artorg.tools.phantomData.server.boot.BootUtils.prepareFileStructure;
import static org.artorg.tools.phantomData.server.boot.BootUtils.startingServer;
import static org.artorg.tools.phantomData.server.boot.BootUtils.isConnected;

import org.artorg.tools.phantomData.client.control.MainController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main( String[] args ) {
		prepareFileStructure();
		logInfos();
		
		new Thread(() -> startingServer(args)).start();
		while(!isConnected()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
    	launch(args);
    }
    
    @Override
	public void start(Stage stage) throws Exception {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("Main.fxml"));
			MainController controller = new MainController();
			loader.setController(controller);
			Pane root = loader.load();
			Scene scene = new Scene(root,1400,1400);
			scene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.setWidth(root.getPrefWidth());
			stage.setHeight(root.getPrefHeight()+50);
			stage.setTitle("Phantom database");
			stage.show();
	}
    
}
