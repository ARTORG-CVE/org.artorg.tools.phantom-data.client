package org.artorg.tools.phantomData.client.scene;

import javafx.scene.control.TextField;

public class SelectableLabel extends TextField {

	public SelectableLabel() {
		this("");
	}

	public SelectableLabel(String name) {
		super(name);
		setEditable(false);
		setStyle(
				"-fx-background-color: transparent; -fx-background-insets: 0; -fx-background-radius: 0; -fx-padding: 0;");
		
	}

}
