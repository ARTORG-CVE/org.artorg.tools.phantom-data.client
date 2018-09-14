package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.scene.control.table.AddableToAnchorPane;
import org.artorg.tools.phantomData.client.util.FxUtil;

import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;

public class MainSplitPane extends SplitPane implements AddableToAnchorPane {

	
	
	
	
	
	
	
	
	
	@Override
	public void addTo(AnchorPane pane) {
		FxUtil.addToAnchorPane(pane, this);
	}

}
