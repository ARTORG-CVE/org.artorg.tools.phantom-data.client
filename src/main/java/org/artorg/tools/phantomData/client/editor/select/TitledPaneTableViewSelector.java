package org.artorg.tools.phantomData.client.editor.select;

import org.artorg.tools.phantomData.client.util.FxUtil;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

public class TitledPaneTableViewSelector<T> extends TableViewSelector<T> {
	private final TitledPane titledPane;

	{
		titledPane = new TitledPane();
	}

	public TitledPaneTableViewSelector(Class<T> subItemClass) {
		super(subItemClass);
		
		this.setName(super.getName());
		
		AnchorPane pane = new AnchorPane();
		FxUtil.addToPane(pane, this);
		titledPane.setContent(pane);
		titledPane.setExpanded(false);
		
	}
	
	@Override
	public void setName(String name) {
		super.setName(name);
		titledPane.setText(name);
	}

}
