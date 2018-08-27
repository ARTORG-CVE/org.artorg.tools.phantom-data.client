package org.artorg.tools.phantomData.client.control;

import java.net.URL;
import java.util.ResourceBundle;

import org.artorg.tools.phantomData.client.table.FilterTable;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.client.table.TableGui;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;

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
    	refresh();
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
        assert contentPane != null : "fx:id=\"contentPane\" was not injected: check your FXML file 'Table.fxml'.";
    }
    
    public void setTable(FilterTable<TABLE, ITEM, ID_TYPE> table) {
		this.table = table;
	}
    
    public void setContent(TableGui<TABLE, ITEM, ID_TYPE> view) {
		view.setTable(table);
		
		view.refresh();
		this.view = view;
		refresh();
		view.addRefreshListener(() -> {
			refresh();
		});
		
		refresh();
	}
    
    public void refresh() {	
    	contentPane.getChildren().clear();
    	contentPane.getChildren().add(view.getGraphic());
    	
    	AnchorPane.setTopAnchor(view.getGraphic(), 0.0);
        AnchorPane.setLeftAnchor(view.getGraphic(), 0.0);
        AnchorPane.setRightAnchor(view.getGraphic(), 0.0);
        AnchorPane.setBottomAnchor(view.getGraphic(), 0.0);
	}
    
}
