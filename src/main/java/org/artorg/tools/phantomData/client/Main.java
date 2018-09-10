package org.artorg.tools.phantomData.client;

import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.control.MainController;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.boot.DesktopSwingBoot;
import org.artorg.tools.phantomData.server.boot.util.AbstractBooter;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import static org.artorg.tools.phantomData.client.boot.DatabaseInitializer.initDatabase;

public class Main extends Application {
	
    public static void main( String[] args ) {
    	AbstractBooter booter = new DesktopSwingBoot();
		booter.boot(args);
    	
    	try {
    		HttpDatabaseCrud.setUrlLocalhost(booter.getConfig().getUrlLocalhost());
    		initDatabase();
    		FxUtil.setMainFxClass(Main.class);
    		launch(args);
    	} catch(Exception e) {
    		booter.getLauncher().setShowStartupConsole(true);
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
