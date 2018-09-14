package org.artorg.tools.phantomData.client.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.artorg.tools.phantomData.client.connectors.AnnulusDiameterConnector;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class AddAnnulusDiameterController implements FXMLloadable<AnchorPane> {
	private static final AnnulusDiameterConnector connector = AnnulusDiameterConnector.get();
	
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
    	String s = valueTextField.getText();
    	Double value = Double.valueOf(s);
    	int shortcut = value.intValue();
    	if (connector.create(new AnnulusDiameter(shortcut, value))) {
    		valueTextField.setText("0.0");
    		shortcutLabel.setText("0");
    	}
    }
    
    @FXML
    void keyReleasedValue(KeyEvent event) {
    	updateShortcut();
    }
    
    private void updateShortcut() {
    	try {
    		Double d = Double.valueOf(valueTextField.getText());
    		shortcutLabel.setText(String.valueOf(d.intValue()));
    	} catch (Exception e) {}
    }

    @FXML
    void initialize() {
        assert pane != null : "fx:id=\"pane\" was not injected: check your FXML file 'AddAnnulusDiameter.fxml'.";
        assert valueTextField != null : "fx:id=\"valueTextField\" was not injected: check your FXML file 'AddAnnulusDiameter.fxml'.";
        assert shortcutLabel != null : "fx:id=\"shortcutLabel\" was not injected: check your FXML file 'AddAnnulusDiameter.fxml'.";
        assert addButton != null : "fx:id=\"addButton\" was not injected: check your FXML file 'AddAnnulusDiameter.fxml'.";

    }

	
}