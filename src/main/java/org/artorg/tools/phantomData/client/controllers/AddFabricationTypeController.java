package org.artorg.tools.phantomData.client.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.artorg.tools.phantomData.client.util.FxUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class AddFabricationTypeController implements FXMLloadable<AnchorPane> {

	@Override
	public AnchorPane loadFXML() {
		return FxUtil.loadFXML("fxml/AddFabricationType.fxml", this);
	}
	
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane pane;

    @FXML
    private TextField textFieldName;

    @FXML
    private TextField textFieldShortcut;

    @FXML
    private Button buttonAdd;

    @FXML
    void add(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert pane != null : "fx:id=\"pane\" was not injected: check your FXML file 'AddFabricationType.fxml'.";
        assert textFieldName != null : "fx:id=\"textFieldName\" was not injected: check your FXML file 'AddFabricationType.fxml'.";
        assert textFieldShortcut != null : "fx:id=\"textFieldShortcut\" was not injected: check your FXML file 'AddFabricationType.fxml'.";
        assert buttonAdd != null : "fx:id=\"buttonAdd\" was not injected: check your FXML file 'AddFabricationType.fxml'.";

    }
}