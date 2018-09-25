package org.artorg.tools.phantomData.client.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.io.IOutil;
import org.artorg.tools.phantomData.client.scene.control.table.FilterTableSpringDb;
import org.artorg.tools.phantomData.client.tables.AnnulusDiameterTable;
import org.artorg.tools.phantomData.client.tables.FabricationTypeTable;
import org.artorg.tools.phantomData.client.tables.FileTable;
import org.artorg.tools.phantomData.client.tables.FileTypeTable;
import org.artorg.tools.phantomData.client.tables.LiteratureBaseTable;
import org.artorg.tools.phantomData.client.tables.PhantomTable;
import org.artorg.tools.phantomData.client.tables.PropertyFieldTable;
import org.artorg.tools.phantomData.client.tables.SpecialTable;
import org.artorg.tools.phantomData.client.tables.property.BooleanPropertyTable;
import org.artorg.tools.phantomData.client.tables.property.DoublePropertyTable;
import org.artorg.tools.phantomData.client.tables.property.IntegerPropertyTable;
import org.artorg.tools.phantomData.client.tables.property.StringPropertyTable;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainController {
	private LayoutController layoutController;
	
	private FilterTableSpringDb<?> table;
	private Stage stage;
	
	private static String urlLocalhost;
	private static String urlShutdownActuator;
	

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
    	table.getUndoManager().save();
    }

    @FXML
    void undo(ActionEvent event) {
    	table.getUndoManager().undo();
    }
    
    @FXML
    void redo(ActionEvent event) {
    	table.getUndoManager().redo();
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
        
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
            	close();
            }
        });
        
        layoutController = new LayoutController();
        
        AnchorPane layout = FxUtil.loadFXML("fxml/PhantomLayout.fxml", layoutController);
        FxUtil.addToAnchorPane(contentPane, layout);
        layoutController.openMainTableTab(new PhantomTable(), "Phantoms");
        try {
        	layoutController.setSecondTable(new FileTable());
        } catch (Exception e) {
        	e.printStackTrace();
        }
		layoutController.set3dFile(IOutil.readResourceAsFile("model.stl"));
        
    }
    
    public <ITEM extends DbPersistent> void setTable(FilterTableSpringDb<ITEM> table) {
		this.table = table;
	}
    
	@FXML
    void openTableFileTypes(ActionEvent event) {
		layoutController.openMainTableTab(new FileTypeTable());
    }
    
	@FXML
    void openTableFiles(ActionEvent event) {
		layoutController.openMainTableTab(new FileTable());
    }
    
	@FXML
    void openTablePhantoms(ActionEvent event) {    	
		layoutController.openMainTableTab(new PhantomTable());
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
		layoutController.openMainTableTab(new BooleanPropertyTable());
    }
    
	@FXML
    void openTableSpecials(ActionEvent event) {
		layoutController.openMainTableTab(new SpecialTable());
    }
    
	@FXML
    void openTableAnnulusDiameter(ActionEvent event) {
		layoutController.openMainTableTab(new AnnulusDiameterTable());
    }
    
	@FXML
    void openTableFabricationTypes(ActionEvent event) {
		layoutController.openMainTableTab(new FabricationTypeTable());
    }
    
	@FXML
    void openTableLiteratureBases(ActionEvent event) {
		layoutController.openMainTableTab(new LiteratureBaseTable());
    }
    
	@FXML
    void openTablePropertyFields(ActionEvent event) {
		layoutController.openMainTableTab(new PropertyFieldTable());
    }
	
	@FXML
    void openTableBooleanProperties(ActionEvent event) {
		layoutController.openMainTableTab(new BooleanPropertyTable());
    }

    @FXML
    void openTableDoubleProperties(ActionEvent event) {
    	layoutController.openMainTableTab(new DoublePropertyTable());
    }
    
    @FXML
    void openTableIntegerProperties(ActionEvent event) {
    	layoutController.openMainTableTab(new IntegerPropertyTable());
    }

    @FXML
    void openTableStringProperties(ActionEvent event) {
    	layoutController.openMainTableTab(new StringPropertyTable());
    }
    
}
