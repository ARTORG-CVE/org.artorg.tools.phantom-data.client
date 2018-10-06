package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

public class TitledPaneTableViewSelector<T extends DbPersistent<T,?>> extends TableViewSelectorSimple<T> {
	private final TitledPane titledPane;
	
	{
		titledPane = new TitledPane();
	}
	
	public TitledPaneTableViewSelector() {
		super();
		
		super.setTableView1(new TableView<Object>());
		super.setTableView2(new TableView<Object>());
		
	}
	
	public TitledPaneTableViewSelector(Class<Object> subItemClass) {
		super(subItemClass);
		
		super.setTableView1(new TableView<Object>());
		super.setTableView2(new TableView<Object>());
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
