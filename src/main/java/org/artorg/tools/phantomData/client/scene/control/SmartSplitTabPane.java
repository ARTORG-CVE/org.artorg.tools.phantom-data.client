package org.artorg.tools.phantomData.client.scene.control;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class SmartSplitTabPane extends SmartNode {
	private final SplitPane splitPane;
	private final List<SmartTabPane> tabPanes;
	
	{
		splitPane = new SplitPane();
		tabPanes = new ArrayList<SmartTabPane>();
	}
	
	protected void addTab(TabPane tabePane, Node node, String tabName) {
		Tab tab = new Tab(tabName);
		tab.setContent(node);
		tab.setOnClosed(closeEvent -> {
			if (tabePane.getTabs().size() == 0)
				splitPane.getItems().remove(tabePane);
		});

		tabePane.getTabs().add(tab);
		tabePane.getSelectionModel().select(tab);
	}
	
	
	public SplitPane getSplitPane() {
		return splitPane;
	}

	public List<SmartTabPane> getTabPanes() {
		return tabPanes;
	}
	
}
