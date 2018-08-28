package org.artorg.tools.phantomData.client;

import static org.artorg.tools.phantomData.server.boot.BootUtils.isConnected;
import static org.artorg.tools.phantomData.server.boot.BootUtils.logInfos;
import static org.artorg.tools.phantomData.server.boot.BootUtils.prepareFileStructure;
import static org.artorg.tools.phantomData.server.boot.BootUtils.startingServer;

import java.io.IOException;

import org.artorg.tools.phantomData.client.control.MainController;
import org.artorg.tools.phantomData.client.tables.PhantomTable;
import org.artorg.tools.phantomData.server.model.Phantom;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
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
	    	
    	FXMLLoader loader = new FXMLLoader(org.artorg.tools.phantomData.client.Main.class.getResource("Table.fxml"));
    	
		MainController controller = new MainController(stage);
		loader.setController(controller);
		
		AnchorPane pane = null;
		try {
			pane = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	Scene scene = new Scene(pane);
		scene.getStylesheets().add(org.artorg.tools.phantomData.client.Main.class.getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("Phantom Database");
		stage.setWidth(800);
		stage.setHeight(500);
		
		stage.show();
		
	}
    
}
