package org.artorg.tools.phantomData.client.control;

import static org.artorg.tools.phantomData.server.boot.BootUtils.shutdownServer;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.artorg.tools.phantomData.client.graphics.Scene3D;
import org.artorg.tools.phantomData.client.table.FilterTable;
import org.artorg.tools.phantomData.client.table.SpreadsheetViewCrud;
import org.artorg.tools.phantomData.client.table.StageTable;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.client.table.TableViewCrud;
import org.artorg.tools.phantomData.client.tables.AnnulusDiameterTable;
import org.artorg.tools.phantomData.client.tables.BooleanPropertyTable;
import org.artorg.tools.phantomData.client.tables.FabricationTypeTable;
import org.artorg.tools.phantomData.client.tables.FileTable;
import org.artorg.tools.phantomData.client.tables.LiteratureBaseTable;
import org.artorg.tools.phantomData.client.tables.PhantomTable;
import org.artorg.tools.phantomData.client.tables.PropertyFieldTable;
import org.artorg.tools.phantomData.client.tables.SpecialTable;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.FabricationType;
import org.artorg.tools.phantomData.server.model.LiteratureBase;
import org.artorg.tools.phantomData.server.model.Phantom;
import org.artorg.tools.phantomData.server.model.PhantomFile;
import org.artorg.tools.phantomData.server.model.Special;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;
import org.artorg.tools.phantomData.server.model.property.PropertyField;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
    private Menu menuFile, menuEdit, menuTables, menuView, menuHelp;

    @FXML
    private MenuItem menuItemImport, menuItemExport, menuItemSettings, menuItemClose, 
    	menuItemSearch, menuItemAbout, menuItemNew, menuItemUndo, menuItemRedo, menuitemResetView;
    
    @FXML
    private MenuItem menuItemTablePhantoms, menuItemTableAnnulusDiameters, menuItemTableLiteratureBases, 
    	menuItemTableFabricationTypes, menuItemTableSpecials, menuItemTableProperties,
    	menuItemTableFiles, menuItemTableFileTypes, menuItemTablePropertyFields;
    
    @FXML
    private RadioMenuItem menuItemShowFilters, menuItemShow3d, menuItemShowTableBelow;
    
    @FXML
    private SplitPane splitPane;   
    
    @FXML
    private TableView<PhantomFile> tableViewFiles;
    
    @FXML
    void initialize() {
        assert parentPane != null : "fx:id=\"parentPane\" was not injected: check your FXML file 'Main.fxml'.";
        assert splitPane != null : "fx:id=\"splitPane\" was not injected: check your FXML file 'Main.fxml'.";
        assert paneSpreadsheet != null : "fx:id=\"pane3d\" was not injected: check your FXML file 'Main.fxml'.";
        assert paneTableView != null : "fx:id=\"paneTableView\" was not injected: check your FXML file 'Main.fxml'.";
        assert pane3d != null : "fx:id=\"pane3d\" was not injected: check your FXML file 'Main.fxml'.";
        
        assert menuBar != null : "fx:id=\"menuBar\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuFile != null : "fx:id=\"menuFile\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuEdit != null : "fx:id=\"menuEdit\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuTables != null : "fx:id=\"menuTables\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuView != null : "fx:id=\"menuView\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuHelp != null : "fx:id=\"MenuHelp\" was not injected: check your FXML file 'Main.fxml'.";
        
        assert menuItemNew != null : "fx:id=\"menuItemNew\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemImport != null : "fx:id=\"menuItemImport\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemExport != null : "fx:id=\"menuItemExport\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemSettings != null : "fx:id=\"menuItemSettings\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemClose != null : "fx:id=\"menuItemClose\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemSearch != null : "fx:id=\"menuItemSearch\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemAbout != null : "fx:id=\"menuItemAbout\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemUndo != null : "fx:id=\"menuItemUndo\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemRedo != null : "fx:id=\"menuItemRedo\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemShowFilters != null : "fx:id=\"menuItemShowFilters\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemShow3d != null : "fx:id=\"menuItemShow3d\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuitemResetView != null : "fx:id=\"menuitemResetView\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemShowTableBelow != null : "fx:id=\"menuItemShowTableBelow\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemTablePhantoms != null : "fx:id=\"menuItemTablePhantoms\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemTableAnnulusDiameters != null : "fx:id=\"menuItemTableAnnulusDiameter\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemTableLiteratureBases != null : "fx:id=\"menuItemTableLiteratureBase\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemTableFabricationTypes != null : "fx:id=\"menuItemTableFabricationType\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemTableSpecials != null : "fx:id=\"menuItemTableSpecials\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemTableProperties != null : "fx:id=\"menuItemTableProperties\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemTableFiles != null : "fx:id=\"menuItemTableFiles\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemTableFileTypes != null : "fx:id=\"menuItemTableFileTypes\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemTablePropertyFields != null : "fx:id=\"menuItemTablePropertyField\" was not injected: check your FXML file 'Main.fxml'.";
        
        assert tableViewFiles != null : "fx:id=\"tableViewFiles\" was not injected: check your FXML file 'Main.fxml'.";
        
        // init 3d pane
        Scene3D scene3d = new Scene3D(pane3d);
        
        String workingDir = System.getProperty("user.dir");
		String filePath = workingDir +"/src/main/resources/model.STL";
		scene3d.loadFile(filePath);
        
		// init spreadsheet
//		StageTable<PhantomTable, Phantom, Integer> stageTable = new StageTable<PhantomTable, Phantom, Integer>();
		PhantomTable phantomTable = new PhantomTable();
//		stageTable.setTable(phantomTable);
		SpreadsheetViewCrud<PhantomTable, Phantom, Integer> view = 
    			new SpreadsheetViewCrud<PhantomTable, Phantom, Integer>(); 
//		stageTable.setView(view);
		
//		phantomTable.readAllData();
		view.setTable(phantomTable);
		Region spreadsheet = view.getGraphic();
		
        paneSpreadsheet.getChildren().add(spreadsheet);
        AnchorPane.setTopAnchor(spreadsheet, 0.0);
        AnchorPane.setLeftAnchor(spreadsheet, 0.0);
        AnchorPane.setRightAnchor(spreadsheet, 0.0);
        AnchorPane.setBottomAnchor(spreadsheet, 0.0);
        paneSpreadsheet.setMinWidth(300);
        
        // init tableview
        FileTable fileTable = new FileTable();
        TableViewCrud<FileTable, PhantomFile, Integer> viewTable = 
    			new TableViewCrud<FileTable, PhantomFile, Integer>();
        
//        fileTable.readAllData();
        viewTable.setTable(fileTable);
        Control tableView  = viewTable.getGraphic();
        paneTableView.getChildren().add(tableView);
        
        AnchorPane.setTopAnchor(tableView, 0.0);
        AnchorPane.setLeftAnchor(tableView, 0.0);
        AnchorPane.setRightAnchor(tableView, 0.0);
        AnchorPane.setBottomAnchor(tableView, 0.0);
        
    }
    
    @FXML
    void about(ActionEvent event) throws IOException {
			FXMLLoader loader = new FXMLLoader(org.artorg.tools.phantomData.client.Main.class.getResource("About.fxml"));
			AboutController controller = new AboutController();
			loader.setController(controller);
			Pane pane = loader.load();
			
			Scene scene = new Scene(pane,400,400);
			scene.getStylesheets().add(org.artorg.tools.phantomData.client.Main.class.getResource("application.css").toExternalForm());
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
    	FXMLLoader loader = new FXMLLoader(org.artorg.tools.phantomData.client.Main.class.getResource("AddPhantom.fxml"));
		AddPhantomController controller = new AddPhantomController();
		loader.setController(controller);
		Pane pane = loader.load();
		
		Scene scene = new Scene(pane,400,400);
		scene.getStylesheets().add(org.artorg.tools.phantomData.client.Main.class.getResource("application.css").toExternalForm());
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
    	Platform.exit();
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
    
    @FXML
    void openTableFileTypes(ActionEvent event) {

    }
    
    

    @FXML
    void openTableFiles(ActionEvent event) {
    	initTableHelperSpreadsheet(new FileTable(), "Files");
    }

    @FXML
    void openTablePhantoms(ActionEvent event) {
    	initTableHelperSpreadsheet(new PhantomTable(), "Phantoms");
    }

    @FXML
    void openTableProperties(ActionEvent event) {
    	initTableHelperSpreadsheet(new BooleanPropertyTable(), "Boolean Properties");
    }

    @FXML
    void openTableSpecials(ActionEvent event) {
    	initTableHelperSpreadsheet(new SpecialTable(), "Specials");
    }

    @FXML
    void openTableAnnulusDiameter(ActionEvent event) {
    	initTableHelperSpreadsheet(new AnnulusDiameterTable(), "Annulus Diameter");
    }
    
    

    @FXML
    void openTableFabricationTypes(ActionEvent event) {
    	initTableHelperSpreadsheet(new FabricationTypeTable(), "Fabrication Types");
    }

    @FXML
    void openTableLiteratureBases(ActionEvent event) {
    	initTableHelperSpreadsheet(new LiteratureBaseTable(), "Literature Bases");
    }
    
    @FXML
    void openTablePropertyFields(ActionEvent event) {
    	initTableHelperSpreadsheet(new PropertyFieldTable(), "Property Fields");
    }
    
    private <TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
	ITEM extends DatabasePersistent<ITEM, ID_TYPE>, 
	ID_TYPE> void initTableHelperSpreadsheet(
			FilterTable<TABLE, ITEM, ID_TYPE> table, String name) {
		
		
		StageTable<TABLE, ITEM, ID_TYPE> stageTable = new StageTable<TABLE, ITEM, ID_TYPE>();
		stageTable.setTable(table);
		SpreadsheetViewCrud<TABLE, ITEM, ID_TYPE> view = 
				new SpreadsheetViewCrud<TABLE, ITEM, ID_TYPE>();	
		stageTable.setView(view);
		Stage stage = stageTable.getStage();
		
		stage.setTitle(name);
		view.autoResizeColumns();
		stage.show();
	}
    
}
