package org.artorg.tools.phantomData.client.scene.layout;

import org.artorg.tools.phantomData.client.util.FxUtil;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

public interface AddableToPane {
	
	default void addTo(Pane pane) {
		FxUtil.addToPane(pane, (Node) this);
	}

}
