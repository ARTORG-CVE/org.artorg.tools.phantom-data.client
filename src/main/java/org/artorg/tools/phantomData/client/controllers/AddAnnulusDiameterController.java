package org.artorg.tools.phantomData.client.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.artorg.tools.phantomData.client.util.FxUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class AddAnnulusDiameterController implements FXMLloadable<AnchorPane> {

	@Override
	public AnchorPane loadFXML() {
		return FxUtil.loadFXML("fxml/AddAnnulusDiameter.fxml", this);
	}
	
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane pane;

    @FXML
    private TextField valueTextField;

    @FXML
    private Label shortcutLabel;
    
    @FXML
    private Button addButton;

    @FXML
    void add(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert pane != null : "fx:id=\"pane\" was not injected: check your FXML file 'AddAnnulusDiameter.fxml'.";
        assert valueTextField != null : "fx:id=\"valueTextField\" was not injected: check your FXML file 'AddAnnulusDiameter.fxml'.";
        assert shortcutLabel != null : "fx:id=\"shortcutLabel\" was not injected: check your FXML file 'AddAnnulusDiameter.fxml'.";
        assert addButton != null : "fx:id=\"addButton\" was not injected: check your FXML file 'AddAnnulusDiameter.fxml'.";

    }

	
}