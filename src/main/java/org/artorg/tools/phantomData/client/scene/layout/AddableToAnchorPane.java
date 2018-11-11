package org.artorg.tools.phantomData.client.scene.layout;

import org.artorg.tools.phantomData.client.util.FxUtil;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public interface AddableToAnchorPane {
	
	default void addTo(AnchorPane pane) {
		FxUtil.addToPane(pane, (Node) this);
	}

}
