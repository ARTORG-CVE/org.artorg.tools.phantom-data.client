package org.artorg.tools.phantomData.client.select;

import org.artorg.tools.phantomData.client.scene.control.tableView.DbTableView;
import org.artorg.tools.phantomData.client.table.TableBase;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.client.util.TableViewFactory;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

public class TitledPaneTableViewSelector<ITEM> extends TableViewSelector<ITEM> {
	private final TitledPane titledPane;

	{
		titledPane = new TitledPane();
	}

	@SuppressWarnings("unchecked")
	public TitledPaneTableViewSelector(Class<?> subItemClass) {
		super(subItemClass);
		
		DbTableView<ITEM> tableView1 = (DbTableView<ITEM>) TableViewFactory.createInitializedTableView(subItemClass,
			TableBase.class, DbTableView.class);
		DbTableView<ITEM> tableView2 = (DbTableView<ITEM>) TableViewFactory.createInitializedTableView(subItemClass,
			TableBase.class, DbTableView.class);

		this.setTableView1(tableView1);
		this.setTableView2(tableView2);

		this.setName(super.getName());
	}

	@Override
	public void init() {
		super.init();
		AnchorPane pane = new AnchorPane();
		FxUtil.addToPane(pane, super.getGraphic());
		titledPane.setContent(pane);
		titledPane.setExpanded(false);
		
		
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
