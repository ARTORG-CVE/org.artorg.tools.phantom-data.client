package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.controller.ISelector;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

public class TitledPaneTableViewSelector<T extends DbPersistent<T,?>> extends TableViewSelectorSimple<T>
		implements ISelector<T, Object> {
	private final TitledPane titledPane;
	
	{
		titledPane = new TitledPane();
	}
	
	@Override
	public void init() {		
		super.init();
		AnchorPane pane = new AnchorPane();
		FxUtil.addToAnchorPane(pane, super.getGraphic());
		titledPane.setContent(pane);
	}

	@Override
	public Node getGraphic() {
		return titledPane;
	}
	
	public TitledPane getTitledPane() {
		return titledPane;
	}

}
