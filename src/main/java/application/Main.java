package application;

import static org.artorg.tools.phantomData.server.boot.BootInit.initDatabase;
import static org.artorg.tools.phantomData.server.boot.BootUtils.deleteDatabase;
import static org.artorg.tools.phantomData.server.boot.BootUtils.deleteFileStructure;
import static org.artorg.tools.phantomData.server.boot.BootUtils.logInfos;
import static org.artorg.tools.phantomData.server.boot.BootUtils.prepareFileStructure;
import static org.artorg.tools.phantomData.server.boot.BootUtils.shutdownServer;
import static org.artorg.tools.phantomData.server.boot.BootUtils.startingServer;

import control.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main( String[] args ) {
//    	shutdownServer();
//		deleteDatabase();
//		deleteFileStructure();
//		prepareFileStructure();
//		logInfos();
//		startingServer(args);
//		
//		initDatabase();
 
    	
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
    
    @Override
    public void stop(){
//    	shutdownServer();
    }
    
}
