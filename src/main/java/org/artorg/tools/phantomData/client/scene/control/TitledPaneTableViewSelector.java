package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.controller.ISelector;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.specification.DbPersistentUUID;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

public class TitledPaneTableViewSelector<ITEM extends DbPersistentUUID<ITEM>> extends TableViewSelectorSimple<ITEM>
		implements ISelector<ITEM, Object> {
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
