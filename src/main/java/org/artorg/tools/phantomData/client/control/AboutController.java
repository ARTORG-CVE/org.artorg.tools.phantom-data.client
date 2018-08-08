package org.artorg.tools.phantomData.client.control;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

public class AboutController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private TextArea textArea;

    @FXML
    void initialize() {
        assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file 'About.fxml'.";
        assert textArea != null : "fx:id=\"textArea\" was not injected: check your FXML file 'About.fxml'.";
        
        textArea.setText(
        		"Version 1.0.0\n"
        		+ "Build Information\n"
        		+ "Version 1.0.0\n"
        		+ "Date: 2018-07-11\n"
        		+ "Java Version SE10\n"
        		+ "\n"
        		+ "Logging\n"
        		+ "The default file path is ...\n"
        		+ "\n"
        		+ "ARTORT Center, Biomedical Engineering\n"
        		
        		);
        textArea.setEditable(false);

    }
}
