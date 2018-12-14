package org.artorg.tools.phantomData.client.select;

import org.artorg.tools.phantomData.client.scene.control.tableView.DbEditFilterTableView;
import org.artorg.tools.phantomData.client.scene.control.tableView.DbFilterTableView;
import org.artorg.tools.phantomData.client.table.TableBase;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.client.util.TableViewFactory;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.collections.ListChangeListener;
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
		
		DbFilterTableView<ITEM> tableView1 = (DbFilterTableView<ITEM>) TableViewFactory.createInitializedTableView(subItemClass,
			TableBase.class, DbEditFilterTableView.class);
		DbFilterTableView<ITEM> tableView2 = (DbFilterTableView<ITEM>) TableViewFactory.createInitializedTableView(subItemClass,
			TableBase.class, DbEditFilterTableView.class);

		this.setTableView1(tableView1);
		this.setTableView2(tableView2);
		
		((DbFilterTableView<?>)this.getTableView1()).refresh();
//		((DbFilterTableView<?>)this.getTableView1()).reload();
		((DbFilterTableView<?>)this.getTableView2()).refresh();
//		((DbFilterTableView<?>)this.getTableView2()).reload();
		
		getTableView2().getItems().addListener(new ListChangeListener<Object>() {

			@Override
			public void onChanged(Change<? extends Object> c) {
				// TODO Auto-generated method stub
				System.out.println("list changed: size = " +c.getList().size());
			}
			
		});
		
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
