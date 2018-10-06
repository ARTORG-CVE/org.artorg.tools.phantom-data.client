package org.artorg.tools.phantomData.client.scene.control;

import java.util.List;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.controller.AbstractTableViewSelector;
import org.artorg.tools.phantomData.client.table.DbFilterTable;
import org.artorg.tools.phantomData.client.table.IFilterTable;
import org.artorg.tools.phantomData.client.table.TableViewFactory;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.specification.DbPersistent;
import org.reflections.Reflections;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;

public class TitledPaneTableViewSelector2<ITEM extends DbPersistent<ITEM,?>> extends TableViewSelector<ITEM>
{
	private final TitledPane titledPane;
	private Class<?> subItemClass;
	
	{
		titledPane = new TitledPane();
	}
	
	
	@SuppressWarnings("unchecked")
	public TitledPaneTableViewSelector2(Class<?> subItemClass) {
		super(subItemClass);
		this.subItemClass = subItemClass;
		System.out.println(this.subItemClass);
		
		List<Class<?>> subClasses = Reflect.getSubclasses(DbFilterTable.class, Main.getReflections());
		
		IFilterTable<Object> filterTable = null;
		try {
			filterTable = (IFilterTable<Object>) subClasses.get(0).newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		System.out.println(filterTable.toString());
		
		System.out.println();
		subClasses.forEach(System.out::println);
		
		System.out.println();
		
		
		DbFilterTableView<?> tableView1 =  (DbFilterTableView<?>) TableViewFactory.createTable(subItemClass, DbFilterTable.class, DbFilterTableView.class);
		DbFilterTableView<?> tableView2 =  (DbFilterTableView<?>) TableViewFactory.createTable(subItemClass, DbFilterTable.class, DbFilterTableView.class);
		
//		TableView<SUB_ITEM> tableView = createInstanceByGenericAndSuperClass(Class<T> superClass, Class<?> genericClass, Reflections reflections)
		
		
		this.setTableView1(tableView1);
		this.setTableView2(tableView2);
		
		
		System.out.println();
	}
	
	@Override
	public void init() {
		
//		super.init();
//		AnchorPane pane = new AnchorPane();
//		FxUtil.addToAnchorPane(pane, super.getGraphic());
//		titledPane.setContent(pane);
	}

	@Override
	public Node getGraphic() {
		return titledPane;
	}
	
	public TitledPane getTitledPane() {
		return titledPane;
	}

}
