package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.table.DbFilterTable;
import org.artorg.tools.phantomData.client.table.TableViewFactory;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

public class TitledPaneTableViewSelector<ITEM extends DbPersistent<ITEM, ?>> extends TableViewSelector<ITEM> {
	private final TitledPane titledPane;

	{
		titledPane = new TitledPane();
	}

	@SuppressWarnings("unchecked")
	public TitledPaneTableViewSelector(Class<?> subItemClass) {
		super(subItemClass);
		
		DbFilterTableView<?> tableView1 = (DbFilterTableView<?>) TableViewFactory.createTable(subItemClass,
				DbFilterTable.class, DbFilterTableView.class);
		DbFilterTableView<?> tableView2 = (DbFilterTableView<?>) TableViewFactory.createTable(subItemClass,
				DbFilterTable.class, DbFilterTableView.class);

		this.setTableView1(tableView1);
		this.setTableView2(tableView2);
		
		this.setName(super.getName());
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
	
	@Override
	public void setName(String name) {
		super.setName(name);
		titledPane.setText(name);
	}

}
