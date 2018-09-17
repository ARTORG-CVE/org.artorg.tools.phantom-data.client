package org.artorg.tools.phantomData.client.controllers;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.artorg.tools.phantomData.client.scene.Scene3D;
import org.artorg.tools.phantomData.client.scene.control.MainTableTabPane;
import org.artorg.tools.phantomData.client.scene.control.SecondTable;
import org.artorg.tools.phantomData.client.scene.control.table.FilterTableSpringDb;
import org.artorg.tools.phantomData.client.scene.control.table.TableSpringDb;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class LayoutController {
	private MainTableTabPane mainTable;
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

        mainTable = new MainTableTabPane();
        mainTable.addTo(mainTablePane);
    	secondTable = new SecondTable();
    	secondTable.addTo(bottomTablePane);
    	scene3d = new Scene3D();
    	scene3d.addTo(pane3d);
    }
    
	public <ITEM extends DatabasePersistent<ID_TYPE>, 
	ID_TYPE> void openMainTableTab(TableViewSpring<ITEM, ID_TYPE> table) {
		openMainTableTab(table, table.getFilterTable().getTableName());
	}
	
	public <ITEM extends DatabasePersistent<ID_TYPE>, ID_TYPE> void openMainTableTab(
			TableViewSpring<ITEM, ID_TYPE> table, String name) {
		mainTable.openTableTab(table, name);
	}
	
	
	
	public <ITEM extends DatabasePersistent<ID_TYPE>, ID_TYPE> void setSecondTable(
			TableViewSpring<ITEM, ID_TYPE> table) {
		secondTable.setTable(table);
	}
	
	public void set3dFile(File file) {
		scene3d.loadFile(file);
	}

	public SecondTable getSecondTable() {
		return secondTable;
	}

	public Scene3D getScene3d() {
		return scene3d;
	}
	
}
