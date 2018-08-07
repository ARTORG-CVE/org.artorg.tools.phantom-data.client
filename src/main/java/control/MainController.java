package control;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.artorg.tools.phantomData.server.model.PhantomFile;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.graphics.Scene3D;
import table.FileTable;
import table.PhantomTable;

public class MainController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane parentPane, pane3d, paneSpreadsheet, paneTableView;

    @FXML
    private MenuBar menuBar;
    
    @FXML
    private Menu menuFile, menuEdit, MenuHelp;

    @FXML
    private MenuItem menuItemImport, menuItemExport, menuItemSettings, menuItemClose, menuItemSearch, menuItemAbout, menuItemNew;

    @FXML
    private SplitPane splitPane;   
    
    @FXML
    private TableView<PhantomFile> tableViewFiles;
    
    @FXML
    void initialize() {
        assert parentPane != null : "fx:id=\"parentPane\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuBar != null : "fx:id=\"menuBar\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuFile != null : "fx:id=\"menuFile\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemNew != null : "fx:id=\"menuItemNew\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemImport != null : "fx:id=\"menuItemImport\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemExport != null : "fx:id=\"menuItemExport\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemSettings != null : "fx:id=\"menuItemSettings\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemClose != null : "fx:id=\"menuItemClose\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuEdit != null : "fx:id=\"menuEdit\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemSearch != null : "fx:id=\"menuItemSearch\" was not injected: check your FXML file 'Main.fxml'.";
        assert MenuHelp != null : "fx:id=\"MenuHelp\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemAbout != null : "fx:id=\"menuItemAbout\" was not injected: check your FXML file 'Main.fxml'.";
        assert pane3d != null : "fx:id=\"pane3d\" was not injected: check your FXML file 'Main.fxml'.";
        assert paneSpreadsheet != null : "fx:id=\"pane3d\" was not injected: check your FXML file 'Main.fxml'.";        
        assert splitPane != null : "fx:id=\"splitPane\" was not injected: check your FXML file 'Main.fxml'.";
        assert paneTableView != null : "fx:id=\"paneTableView\" was not injected: check your FXML file 'Main.fxml'.";
        assert tableViewFiles != null : "fx:id=\"tableViewFiles\" was not injected: check your FXML file 'Main.fxml'.";
        
        // init 3d pane
        Scene3D scene3d = new Scene3D(pane3d);
        
        String workingDir = System.getProperty("user.dir");
		String filePath = workingDir +"/src/main/resources/model.STL";
		scene3d.loadFile(filePath);
        
		// init spreadsheet
		PhantomTable phantomTable = new PhantomTable();
		SpreadsheetView spreadsheet = phantomTable.createSpreadsheetView();
        paneSpreadsheet.getChildren().add(spreadsheet);
        spreadsheet.setStyle("-fx-focus-color: transparent;");
        
        AnchorPane.setTopAnchor(spreadsheet, 0.0);
        AnchorPane.setLeftAnchor(spreadsheet, 0.0);
        AnchorPane.setRightAnchor(spreadsheet, 0.0);
        AnchorPane.setBottomAnchor(spreadsheet, 0.0);
        paneSpreadsheet.setMinWidth(300);
        
        // init tableview
//        paneTableView.getChildren().add(new FileTable().createTableView());
        FileTable fileTable = new FileTable();
        fileTable.createTableView(tableViewFiles);
        
    }
    
    @FXML
    void about(ActionEvent event) throws IOException {
			FXMLLoader loader = new FXMLLoader(application.Main.class.getResource("About.fxml"));
			AboutController controller = new AboutController();
			loader.setController(controller);
			Pane pane = loader.load();
			
			Scene scene = new Scene(pane,400,400);
			scene.getStylesheets().add(application.Main.class.getResource("application.css").toExternalForm());
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setWidth(pane.getPrefWidth());
			stage.setHeight(pane.getPrefHeight()+50);
			stage.setMinWidth(pane.getPrefWidth());
			stage.setMinHeight(pane.getPrefHeight()+50);
			stage.show();
		
    }

    @FXML
    void addphantom(ActionEvent event) throws IOException {
    	FXMLLoader loader = new FXMLLoader(application.Main.class.getResource("AddPhantom.fxml"));
		AddPhantomController controller = new AddPhantomController();
		loader.setController(controller);
		Pane pane = loader.load();
		
		Scene scene = new Scene(pane,400,400);
		scene.getStylesheets().add(application.Main.class.getResource("application.css").toExternalForm());
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.setWidth(pane.getPrefWidth());
		stage.setHeight(pane.getPrefHeight()+50);
		stage.setMinWidth(pane.getPrefWidth());
		stage.setMinHeight(pane.getPrefHeight()+50);
		stage.show();
    }

    @FXML
    void close(ActionEvent event) {
    	System.exit(0);
    }

    @FXML
    void export(ActionEvent event) {

    }

    @FXML
    void importing(ActionEvent event) {

    }

    @FXML
    void search(ActionEvent event) {

    }

    @FXML
    void settings(ActionEvent event) {

    }
    
}
