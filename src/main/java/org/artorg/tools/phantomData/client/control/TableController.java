package org.artorg.tools.phantomData.client.control;

import java.net.URL;
import java.util.ResourceBundle;

import org.artorg.tools.phantomData.client.table.FilterTable;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.client.table.TableGui;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class TableController<TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
		ITEM extends DatabasePersistent<ITEM, ID_TYPE>, 
		ID_TYPE> {
	private FilterTable<TABLE, ITEM, ID_TYPE> table;
	private TableGui<TABLE, ITEM, ID_TYPE> view;

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
    void about(ActionEvent event) {

    }

    @FXML
    void close(ActionEvent event) {
//    	Platform.exit();
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
    private VBox vbox;

    @FXML
    void initialize() {
        assert rootPane != null : "fx:id=\"rootPane\" was not injected: check your FXML file 'Table.fxml'.";
        assert menuItemSave != null : "fx:id=\"menuItemSave\" was not injected: check your FXML file 'Table.fxml'.";
        assert menuItemRefresh != null : "fx:id=\"menuItemRefresh\" was not injected: check your FXML file 'Table.fxml'.";
        assert menuItemClose != null : "fx:id=\"menuItemClose\" was not injected: check your FXML file 'Table.fxml'.";
        assert menuitemUndo != null : "fx:id=\"menuitemUndo\" was not injected: check your FXML file 'Table.fxml'.";
        assert menuItemRedo != null : "fx:id=\"menuItemRedo\" was not injected: check your FXML file 'Table.fxml'.";
        assert menuItemAbout != null : "fx:id=\"menuItemAbout\" was not injected: check your FXML file 'Table.fxml'.";
        assert contentPane != null : "fx:id=\"contentPane\" was not injected: check your FXML file 'Table.fxml'.";
        assert vbox != null : "fx:id=\"vbox\" was not injected: check your FXML file 'Table.fxml'.";

    }
    
    public void setTable(FilterTable<TABLE, ITEM, ID_TYPE> table) {
		this.table = table;
	}
    
    public void setContent(Control view) {
//    	contentPane = new AnchorPane();
//    	contentPane.setPrefWidth(300);
//    	contentPane.setPrefHeight(300);
    	contentPane.getChildren().add(view);
    	
    	System.out.println("<<<<< Content of " +table.getClass().getSimpleName() +"<<<<<");
		System.out.println(view.toString());
		System.out.println(">>>>> Content of " +table.getClass().getSimpleName() +">>>>>");
    	
//		AnchorPane.setTopAnchor(contentPane, 0.0);
//        AnchorPane.setLeftAnchor(contentPane, 0.0);
//        AnchorPane.setRightAnchor(contentPane, 0.0);
//        AnchorPane.setBottomAnchor(contentPane, 0.0);
        
    	
    	AnchorPane.setTopAnchor(view, 0.0);
        AnchorPane.setLeftAnchor(view, 0.0);
        AnchorPane.setRightAnchor(view, 0.0);
        AnchorPane.setBottomAnchor(view, 0.0);
        
        contentPane.setMinWidth(300);
        contentPane.setMinHeight(300);
    }
}
