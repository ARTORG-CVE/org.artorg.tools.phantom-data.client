package org.artorg.tools.phantomData.client.scene.control;

import java.util.List;

import org.artorg.tools.phantomData.client.controller.ISelector;
import org.artorg.tools.phantomData.client.table.FilterableTable;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.specification.DbPersistentUUID;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

public class TitledPaneTableViewSelector2<ITEM> extends TableViewSelector<ITEM, Object>
		implements ISelector<ITEM, Object> {
	private final TitledPane titledPane;
	private Class<?> subItemClass;
	
	{
		titledPane = new TitledPane();
	}
	
	
	@SuppressWarnings("unchecked")
	public TitledPaneTableViewSelector2(Class<?> subItemClass) {
		this.subItemClass = subItemClass;
		System.out.println(this.subItemClass);
		
		List<Class<?>> subClasses = Reflect.getSubclasses(DbFilterTable.class, "org");
		
		FilterableTable<Object> filterTable = null;
		try {
			filterTable = (FilterableTable<Object>) subClasses.get(0).newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		System.out.println(filterTable.toString());
		
		System.out.println();
		subClasses.forEach(System.out::println);
		
		System.out.println();
		
		
		this.setTable1(filterTable);
		
		
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
