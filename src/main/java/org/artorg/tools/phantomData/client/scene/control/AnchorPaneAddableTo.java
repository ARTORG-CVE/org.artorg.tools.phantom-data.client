package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.scene.control.table.AddableToAnchorPane;

import javafx.scene.layout.AnchorPane;

public class AnchorPaneAddableTo extends AnchorPane implements AddableToAnchorPane {
	
	public void addTo(AnchorPane pane) {
		pane.getChildren().add(this);
    	AnchorPane.setBottomAnchor(this, 0.0);
    	AnchorPane.setLeftAnchor(this, 0.0);
    	AnchorPane.setRightAnchor(this, 0.0);
    	AnchorPane.setTopAnchor(this, 0.0);
		
	}

}
