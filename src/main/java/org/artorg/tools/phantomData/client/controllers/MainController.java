package org.artorg.tools.phantomData.client.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.CrudConnectors;
import org.artorg.tools.phantomData.client.io.IOutil;
import org.artorg.tools.phantomData.client.scene.control.ProTableView;
import org.artorg.tools.phantomData.client.scene.control.ProTreeTableView;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.FabricationType;
import org.artorg.tools.phantomData.server.model.FileType;
import org.artorg.tools.phantomData.server.model.LiteratureBase;
import org.artorg.tools.phantomData.server.model.Phantom;
import org.artorg.tools.phantomData.server.model.PhantomFile;
import org.artorg.tools.phantomData.server.model.Special;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;
import org.artorg.tools.phantomData.server.model.property.DoubleProperty;
import org.artorg.tools.phantomData.server.model.property.IntegerProperty;
import org.artorg.tools.phantomData.server.model.property.PropertyField;
import org.artorg.tools.phantomData.server.model.property.StringProperty;

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
    	menuItemTableFabricationTypes, menuItemTableSpecials, menuItemTableProperties,
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
        
        AnchorPane layout = FxUtil.loadFXML("fxml/PhantomLayout.fxml", layoutController);
        FxUtil.addToAnchorPane(contentPane, layout);
        layoutController.init();
        
        layoutController.openMainTableTab(Phantom.class);
//        layoutController.setSecondTable(PhantomFile.class);
        
        ProTreeTableView treeTableView = new ProTreeTableView();
        treeTableView.initTable();
        
        CrudConnectors<Phantom,?> connector = CrudConnectors.getConnector(Phantom.class);
        List<Phantom> phantoms = connector.readAllAsList();
        treeTableView.setItems(phantoms);
        layoutController.setSecondTreeTable(treeTableView);
        
        
		layoutController.set3dFile(IOutil.readResourceAsFile("model.stl"));
	}
    
	@FXML
    void openTableFileTypes(ActionEvent event) {
		layoutController.openMainTableTab(FileType.class);
    }
    
	@FXML
    void openTableFiles(ActionEvent event) {
		layoutController.openMainTableTab(PhantomFile.class);
    }
    
	@FXML
    void openTablePhantoms(ActionEvent event) {    	
		layoutController.openMainTableTab(Phantom.class);
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
		layoutController.openMainTableTab(BooleanProperty.class);
    }
    
	@FXML
    void openTableSpecials(ActionEvent event) {
		layoutController.openMainTableTab(Special.class);
    }
    
	@FXML
    void openTableAnnulusDiameter(ActionEvent event) {
		layoutController.openMainTableTab(AnnulusDiameter.class);
    }
    
	@FXML
    void openTableFabricationTypes(ActionEvent event) {
		layoutController.openMainTableTab(FabricationType.class);
    }
    
	@FXML
    void openTableLiteratureBases(ActionEvent event) {
		layoutController.openMainTableTab(LiteratureBase.class);
    }
    
	@FXML
    void openTablePropertyFields(ActionEvent event) {
		layoutController.openMainTableTab(PropertyField.class);
    }
	
	@FXML
    void openTableBooleanProperties(ActionEvent event) {
		layoutController.openMainTableTab(BooleanProperty.class);
    }

    @FXML
    void openTableDoubleProperties(ActionEvent event) {
    	layoutController.openMainTableTab(DoubleProperty.class);
    }
    
    @FXML
    void openTableIntegerProperties(ActionEvent event) {
    	layoutController.openMainTableTab(IntegerProperty.class);
    }

    @FXML
    void openTableStringProperties(ActionEvent event) {
    	layoutController.openMainTableTab(StringProperty.class);
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
