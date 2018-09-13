package org.artorg.tools.phantomData.client.controllers;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.artorg.tools.phantomData.client.scene.Scene3D;
import org.artorg.tools.phantomData.client.scene.control.MainTable;
import org.artorg.tools.phantomData.client.scene.control.SecondTable;
import org.artorg.tools.phantomData.client.scene.control.table.FilterTable;
import org.artorg.tools.phantomData.client.scene.control.table.Table;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class LayoutController {
	private MainTable mainTable;
	private SecondTable secondTable;
	private Scene3D scene3d;
	
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane parentPane, mainTablePane, bottomTablePane, pane3d;
    
	@FXML
    void initialize() {
        assert parentPane != null : "fx:id=\"parentPane\" was not injected: check your FXML file 'PhantomLayout.fxml'.";
        assert mainTablePane != null : "fx:id=\"mainTablePane\" was not injected: check your FXML file 'PhantomLayout.fxml'.";
        assert pane3d != null : "fx:id=\"pane3d\" was not injected: check your FXML file 'PhantomLayout.fxml'.";
        assert bottomTablePane != null : "fx:id=\"tableBottomPane\" was not injected: check your FXML file 'PhantomLayout.fxml'.";
        
        mainTable = new MainTable();
        mainTable.addTo(mainTablePane);
    	secondTable = new SecondTable();
    	secondTable.addTo(bottomTablePane);
    	scene3d = new Scene3D();
    	scene3d.addTo(pane3d);
    }
    
	public <TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
	ITEM extends DatabasePersistent<ID_TYPE>, 
	ID_TYPE> void setMainTable(FilterTable<TABLE, ITEM, ID_TYPE> table) {
		mainTable.setTable(table);
	}
	
	public <TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
	ITEM extends DatabasePersistent<ID_TYPE>, 
	ID_TYPE> void setSecondTable(FilterTable<TABLE, ITEM, ID_TYPE> table) {
		secondTable.setTable(table);
	}
	
	public void set3dFile(File file) {
		scene3d.loadFile(file);
	}
	
	public MainTable getMainTable() {
		return mainTable;
	}

	public SecondTable getSecondTable() {
		return secondTable;
	}

	public Scene3D getScene3d() {
		return scene3d;
	}
	
}
