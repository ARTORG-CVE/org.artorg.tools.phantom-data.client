package org.artorg.tools.phantomData.client.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class AboutController {
	private final Stage stage;
	
	public AboutController(Stage stage) {
		this.stage = stage;
	}
	
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextArea textPane;

    @FXML
    void close(MouseEvent event) {
    	stage.close();
    }

    @FXML
    void minimize(MouseEvent event) {
    	stage.setIconified(true);
    }

    @FXML
    void initialize() {
        assert textPane != null : "fx:id=\"textPane\" was not injected: check your FXML file 'About.fxml'.";
    }
    
}

