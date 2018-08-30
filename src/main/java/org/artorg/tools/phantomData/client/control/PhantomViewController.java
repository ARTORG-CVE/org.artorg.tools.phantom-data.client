package org.artorg.tools.phantomData.client.control;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class PhantomViewController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane parentPane;

    @FXML
    private AnchorPane mainTablePane;

    @FXML
    private AnchorPane pane3d;

    public AnchorPane getPane3d() {
		return pane3d;
	}

	public void setPane3d(AnchorPane pane3d) {
		this.pane3d = pane3d;
	}

	@FXML
    private AnchorPane bottomTablePane;

    public AnchorPane getBottomTablePane() {
		return bottomTablePane;
	}

	public void setBottomTablePane(AnchorPane bottomTablePane) {
		this.bottomTablePane = bottomTablePane;
	}

	@FXML
    void initialize() {
        assert parentPane != null : "fx:id=\"parentPane\" was not injected: check your FXML file 'PhantomLayout.fxml'.";
        assert mainTablePane != null : "fx:id=\"mainTablePane\" was not injected: check your FXML file 'PhantomLayout.fxml'.";
        assert pane3d != null : "fx:id=\"pane3d\" was not injected: check your FXML file 'PhantomLayout.fxml'.";
        assert bottomTablePane != null : "fx:id=\"tableBottomPane\" was not injected: check your FXML file 'PhantomLayout.fxml'.";

    }
    
    public void setMainTablePane(Node n) {
    	mainTablePane.getChildren().add(n);
    	AnchorPane.setBottomAnchor(n, 0.0);
    	AnchorPane.setLeftAnchor(n, 0.0);
    	AnchorPane.setRightAnchor(n, 0.0);
    	AnchorPane.setTopAnchor(n, 0.0);
    }
}
