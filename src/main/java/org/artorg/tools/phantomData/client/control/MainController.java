package org.artorg.tools.phantomData.client.control;

import java.net.URL;
import java.util.ResourceBundle;

import org.artorg.tools.phantomData.client.table.FilterTable;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.client.table.TableGui;
import org.artorg.tools.phantomData.client.table.TableViewCrud;
import org.artorg.tools.phantomData.client.tables.AnnulusDiameterTable;
import org.artorg.tools.phantomData.client.tables.BooleanPropertyTable;
import org.artorg.tools.phantomData.client.tables.FabricationTypeTable;
import org.artorg.tools.phantomData.client.tables.FileTable;
import org.artorg.tools.phantomData.client.tables.FileTypeTable;
import org.artorg.tools.phantomData.client.tables.LiteratureBaseTable;
import org.artorg.tools.phantomData.client.tables.PhantomTable;
import org.artorg.tools.phantomData.client.tables.PropertyFieldTable;
import org.artorg.tools.phantomData.client.tables.SpecialTable;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainController

//<TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
//		ITEM extends DatabasePersistent<ITEM, ID_TYPE>, 
//		ID_TYPE> 
		{
	private FilterTable<?, ?, ?> table;
	private TableGui<?, ?, ?> view;
	private Stage stage;

	public MainController(Stage stage) {
		this.stage = stage;
	}
	
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private MenuItem menuItemSave, menuItemRefresh, menuItemClose,
    	menuitemUndo, menuItemRedo, menuItemAbout;
    
    @FXML
    private MenuItem menuItemTablePhantoms, menuItemTableAnnulusDiameters, menuItemTableLiteratureBases, 
    	menuItemTableFabricationTypes, menuItemTableSpecials, menuItemTableProperties,
    	menuItemTableFiles, menuItemTableFileTypes, menuItemTablePropertyFields;
    
    @FXML
    private TabPane tabPane;
    
    @FXML
    void about(ActionEvent event) {

    }

    @FXML
    void close(ActionEvent event) {
    	Platform.exit();
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

    @SuppressWarnings("unchecked")
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
        assert tabPane != null : "fx:id=\"tabPane\" was not injected: check your FXML file 'Table.fxml'.";
        
        initTableHelperTableView((FilterTable<TABLE, ITEM, ID_TYPE>) new PhantomTable(), "Phantoms");
        
    }
    
    public <TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
	ITEM extends DatabasePersistent<ITEM, ID_TYPE>, 
	ID_TYPE> void setTable(FilterTable<TABLE, ITEM, ID_TYPE> table) {
		this.table = table;
	}
    
    public <TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
	ITEM extends DatabasePersistent<ITEM, ID_TYPE>, 
	ID_TYPE> void setContent(TableGui<TABLE, ITEM, ID_TYPE> view) {
		view.setTable(table);
		
		view.refresh();
		this.view = view;
		
	}
    
    @SuppressWarnings("unchecked")
	@FXML
    void openTableFileTypes(ActionEvent event) {
    	initTableHelperTableView<FileTable,File, Integer>((FilterTable<FileTable, File, Integer>) new FileTypeTable(), "Files");
    }
    
    @SuppressWarnings("unchecked")
	@FXML
    void openTableFiles(ActionEvent event) {
    	initTableHelperTableView((FilterTable<TABLE, ITEM, ID_TYPE>) new FileTable(), "Files");
    }

    @SuppressWarnings("unchecked")
	@FXML
    void openTablePhantoms(ActionEvent event) {    	
    	initTableHelperTableView((FilterTable<TABLE, ITEM, ID_TYPE>) new PhantomTable(), "Phantoms");
    }

    @SuppressWarnings("unchecked")
	@FXML
    void openTableProperties(ActionEvent event) {
    	initTableHelperTableView((FilterTable<TABLE, ITEM, ID_TYPE>) new BooleanPropertyTable(), "Boolean Properties");
    }

    @SuppressWarnings("unchecked")
	@FXML
    void openTableSpecials(ActionEvent event) {
    	initTableHelperTableView((FilterTable<TABLE, ITEM, ID_TYPE>) new SpecialTable(), "Specials");
    }

    @SuppressWarnings("unchecked")
	@FXML
    void openTableAnnulusDiameter(ActionEvent event) {
    	initTableHelperTableView((FilterTable<TABLE, ITEM, ID_TYPE>) new AnnulusDiameterTable(), "Annulus Diameter");
    }
    
    @SuppressWarnings("unchecked")
	@FXML
    void openTableFabricationTypes(ActionEvent event) {
    	initTableHelperTableView((FilterTable<TABLE, ITEM, ID_TYPE>) new FabricationTypeTable(), "Fabrication Types");
    }

    @SuppressWarnings("unchecked")
	@FXML
    void openTableLiteratureBases(ActionEvent event) {
    	initTableHelperTableView((FilterTable<TABLE, ITEM, ID_TYPE>) new LiteratureBaseTable(), "Literature Bases");
    }
    
    @SuppressWarnings("unchecked")
	@FXML
    void openTablePropertyFields(ActionEvent event) {
    	initTableHelperTableView((FilterTable<TABLE, ITEM, ID_TYPE>) new PropertyFieldTable(), "Property Fields");
    }
	
	 private <TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
		ITEM extends DatabasePersistent<ITEM, ID_TYPE>, 
		ID_TYPE> void initTableHelperTableView(
				FilterTable<TABLE, ITEM, ID_TYPE> table, 
				String name) {
			TableViewCrud<TABLE, ITEM, ID_TYPE> view = new TableViewCrud<TABLE, ITEM, ID_TYPE>();
			initTableHelper(view, table, name);
	}
		
	private <TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
	ITEM extends DatabasePersistent<ITEM, ID_TYPE>, 
	ID_TYPE> void initTableHelper(
				TableGui<TABLE, ITEM , ID_TYPE> view,
				FilterTable<TABLE, ITEM, ID_TYPE> table, 
				String name) {
			this.setTable(table);
			view.setTable(table);
			this.setContent(view);
			
			final Tab tab = new Tab(name);
			tab.setContent(view.getGraphic());
			tabPane.getTabs().add(tab);
			tabPane.getSelectionModel().select(tab);
		}
    
}
