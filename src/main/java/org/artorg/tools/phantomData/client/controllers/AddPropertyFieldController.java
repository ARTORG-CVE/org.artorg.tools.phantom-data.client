package org.artorg.tools.phantomData.client.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.artorg.tools.phantomData.client.connectors.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class AddPropertyFieldController implements FXMLloadable<AnchorPane> {
	private static final PropertyFieldConnector connector = PropertyFieldConnector.get();

	@Override
	public AnchorPane loadFXML() {
		return FxUtil.loadFXML("fxml/AddPropertyField.fxml", this);
	}
	
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane pane;

    @FXML
    private TextField textFieldDescription;

    @FXML
    private TextField textFieldName;

    @FXML
    private Button buttonAdd;

    @FXML
    void add(ActionEvent event) {
    	String name = textFieldName.getText();
    	String description = textFieldDescription.getText();
    	if (connector.create(new PropertyField(name, description))) {
    		textFieldName.setText("");
    		textFieldDescription.setText("");
    	}
    }

    @FXML
    void initialize() {
        assert pane != null : "fx:id=\"pane\" was not injected: check your FXML file 'AddPropertyField.fxml'.";
        assert textFieldDescription != null : "fx:id=\"textFieldDescription\" was not injected: check your FXML file 'AddPropertyField.fxml'.";
        assert textFieldName != null : "fx:id=\"textFieldName\" was not injected: check your FXML file 'AddPropertyField.fxml'.";
        assert buttonAdd != null : "fx:id=\"buttonAdd\" was not injected: check your FXML file 'AddPropertyField.fxml'.";

    }

	
}