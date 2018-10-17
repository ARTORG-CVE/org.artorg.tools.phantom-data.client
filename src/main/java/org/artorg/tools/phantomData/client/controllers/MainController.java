package org.artorg.tools.phantomData.client.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.*;
import org.artorg.tools.phantomData.server.model.property.*;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainController {
	private static String urlLocalhost;
	private static String urlShutdownActuator;
	private LayoutController layoutController;
	private Stage stage;
	
	public MainController(Stage stage) {
		this.stage = stage;
	}
	
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane rootPane, contentPane;

    @FXML
    private MenuItem menuItemSave, menuItemRefresh, menuItemClose,
    	menuitemUndo, menuItemRedo, menuItemAbout;
    
    @FXML
    private MenuItem menuItemTablePhantoms, menuItemTableAnnulusDiameters, menuItemTableLiteratureBases, 
    	menuItemTableFabricationTypes, menuItemTableSpecials,
    	menuItemTableAcademicTitles, menuItemTablePersons, menuItemTableProperties,
    	menuItemTableFiles, menuItemTableFileTypes, menuItemTablePropertyFields;
    
    @FXML
    private MenuItem menuItemTableBooleanProperties, menuItemTableDoubleProperties,
    	menuItemTableIntegerProperties, menuItemTableStringProperties;
    
    @FXML
    void about(ActionEvent event) {

    }

    @FXML
    void close(ActionEvent event) {
    	close();
    }
    
    private void close() {
    	stage.hide();
    	
    	if (Main.getClientBooter().getServerBooter().isServerStartedEmbedded())
    		Main.getClientBooter().getServerBooter().shutdownSpringServer();
    	
    	Platform.exit();
    	
    	System.exit(0);
    }
    
    @FXML
    void refresh(ActionEvent event) {
    	
    }

    @FXML
    void save(ActionEvent event) {
//    	table.getTable().getUndoManager().save();
    }

    @FXML
    void undo(ActionEvent event) {
//    	table.getTable().getUndoManager().undo();
    }
    
    @FXML
    void redo(ActionEvent event) {
//    	table.getTable().getUndoManager().redo();
    }
    
	@FXML
    void initialize() {
        assert rootPane != null : "fx:id=\"rootPane\" was not injected: check your FXML file 'Table.fxml'.";
        assert menuItemSave != null : "fx:id=\"menuItemSave\" was not injected: check your FXML file 'Table.fxml'.";
        assert menuItemRefresh != null : "fx:id=\"menuItemRefresh\" was not injected: check your FXML file 'Table.fxml'.";
        assert menuItemClose != null : "fx:id=\"menuItemClose\" was not injected: check your FXML file 'Table.fxml'.";
        assert menuitemUndo != null : "fx:id=\"menuitemUndo\" was not injected: check your FXML file 'Table.fxml'.";
        assert menuItemRedo != null : "fx:id=\"menuItemRedo\" was not injected: check your FXML file 'Table.fxml'.";
        assert menuItemAbout != null : "fx:id=\"menuItemAbout\" was not injected: check your FXML file 'Table.fxml'.";
        assert menuItemTablePhantoms != null : "fx:id=\"menuItemTablePhantoms\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemTableAnnulusDiameters != null : "fx:id=\"menuItemTableAnnulusDiameter\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemTableLiteratureBases != null : "fx:id=\"menuItemTableLiteratureBase\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemTableFabricationTypes != null : "fx:id=\"menuItemTableFabricationType\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemTableSpecials != null : "fx:id=\"menuItemTableSpecials\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemTablePersons != null : "fx:id=\"menuitemTablePersons\" was not injected: check your FXML file 'Table.fxml'.";
        assert menuItemTableAcademicTitles != null : "fx:id=\"menuItemTableAcademicTitles\" was not injected: check your FXML file 'Table.fxml'.";
        assert menuItemTableProperties != null : "fx:id=\"menuItemTableProperties\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemTableFiles != null : "fx:id=\"menuItemTableFiles\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemTableFileTypes != null : "fx:id=\"menuItemTableFileTypes\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemTablePropertyFields != null : "fx:id=\"menuItemTablePropertyField\" was not injected: check your FXML file 'Main.fxml'.";
        assert contentPane != null : "fx:id=\"contentPane\" was not injected: check your FXML file 'Table.fxml'.";
    }
	
	public void init() {
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
            	close();
            }
        });
        
        layoutController = new LayoutController();
        layoutController.addTo(contentPane);
        layoutController.openTableTab(Phantom.class);
        layoutController.openBottomTreeTableTab(Phantom.class);

	}
    
	@FXML
    void openTableFileTypes(ActionEvent event) {
		layoutController.openTableTab(FileType.class);
    }
    
	@FXML
    void openTableFiles(ActionEvent event) {
		layoutController.openTableTab(PhantomFile.class);
    }
    
	@FXML
    void openTablePhantoms(ActionEvent event) {    	
		layoutController.openTableTab(Phantom.class);
    }
	
//	private Button createTabButton(String iconName) {
//        Button button = new Button();
//        ImageView imageView = new ImageView(new Image(IOutil.readResourceAsStream(iconName),
//                16, 16, false, true));
//        button.setGraphic(imageView);
//        button.getStyleClass().add("tab-button");
//        return button;
//    }
    
	@FXML
    void openTableProperties(ActionEvent event) {
		layoutController.openTableTab(BooleanProperty.class);
    }
    
	@FXML
    void openTableSpecials(ActionEvent event) {
		layoutController.openTableTab(Special.class);
    }
    
	@FXML
    void openTableAnnulusDiameter(ActionEvent event) {
		layoutController.openTableTab(AnnulusDiameter.class);
    }
    
	@FXML
    void openTableFabricationTypes(ActionEvent event) {
		layoutController.openTableTab(FabricationType.class);
    }
    
	@FXML
    void openTableLiteratureBases(ActionEvent event) {
		layoutController.openTableTab(LiteratureBase.class);
    }
    
	@FXML
    void openTablePropertyFields(ActionEvent event) {
		layoutController.openTableTab(PropertyField.class);
    }
	
	@FXML
    void openTableAcademicTitles(ActionEvent event) {
		layoutController.openTableTab(AcademicTitle.class);
    }
	
	@FXML
    void openTablePersons(ActionEvent event) {
		layoutController.openTableTab(Person.class);
    }
	
	@FXML
    void openTableBooleanProperties(ActionEvent event) {
		layoutController.openTableTab(BooleanProperty.class);
    }

    @FXML
    void openTableDoubleProperties(ActionEvent event) {
    	layoutController.openTableTab(DoubleProperty.class);
    }
    
    @FXML
    void openTableIntegerProperties(ActionEvent event) {
    	layoutController.openTableTab(IntegerProperty.class);
    }

    @FXML
    void openTableStringProperties(ActionEvent event) {
    	layoutController.openTableTab(StringProperty.class);
    }
    
    public static String getUrlLocalhost() {
		return urlLocalhost;
	}

	public static void setUrlLocalhost(String urlLocalhost) {
		System.out.println(urlLocalhost);
		MainController.urlLocalhost = urlLocalhost;
	}

	public static String getUrlShutdownActuator() {
		return urlShutdownActuator;
	}

	public static void setUrlShutdownActuator(String urlShutdownActuator) {
		MainController.urlShutdownActuator = urlShutdownActuator;
	}
    
}
