package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.scene.control.table.AddableToAnchorPane;
import org.artorg.tools.phantomData.client.util.FxUtil;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;

public class MainSplitPane extends SplitPane implements AddableToAnchorPane {
	private MainTableTabPane mainTableTabPane;
	private MainAddItemTabPane mainAddItemTabPane;
	private SplitPane splitPane;
	
	{
		mainTableTabPane = new MainTableTabPane(this);
		mainAddItemTabPane = new MainAddItemTabPane();
		splitPane = this;
		splitPane.setOrientation(Orientation.HORIZONTAL);
		splitPane.getItems().add(mainTableTabPane);
	}
	
	
	
	
	
	
	@Override
	public void addTo(AnchorPane pane) {
		FxUtil.addToAnchorPane(pane, this);
	}
	
	
	
	
	
	public MainTableTabPane getMainTableTabPane() {
		return mainTableTabPane;
	}
	
	public void setMainTableTabPane(MainTableTabPane mainTableTabPane) {
		this.mainTableTabPane = mainTableTabPane;
	}

	public void addNewItemTab(Node node, String tabName) {
		Tab tab = new Tab(tabName);
		tab.setContent(node);
		tab.setOnClosed(closeEvent -> {
			if (mainAddItemTabPane.getTabs().size() == 0)
				splitPane.getItems().remove(mainAddItemTabPane);
		});
		
		if (mainAddItemTabPane.getTabs().size() == 0) {
			splitPane.getItems().add(mainAddItemTabPane);
		}
		mainAddItemTabPane.getTabs().add(tab);
		mainAddItemTabPane.getSelectionModel().select(tab);
	}

}
