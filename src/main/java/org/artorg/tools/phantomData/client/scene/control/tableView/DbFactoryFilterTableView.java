package org.artorg.tools.phantomData.client.scene.control.tableView;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.table.FxFactory;
import org.artorg.tools.phantomData.client.table.IDbFactoryTableView;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

@SuppressWarnings("unchecked")
public class DbFactoryFilterTableView<ITEM extends DbPersistent<ITEM,?>> extends DbFilterTableView<ITEM> implements IDbFactoryTableView<ITEM> {
	private final Class<?> factoryClass;
	
	public DbFactoryFilterTableView() {
		super();
		factoryClass = Reflect.getClassByGenericAndSuperClass(ItemEditFactoryController.class, super.getItemClass(), 0, Main.getReflections());
	}
	
	public DbFactoryFilterTableView(Class<ITEM> itemClass) {
		super(itemClass);
		factoryClass = Reflect.getClassByGenericAndSuperClass(ItemEditFactoryController.class, super.getItemClass(), 0, Main.getReflections());
	}
	
	public DbFactoryFilterTableView(Class<ITEM> itemClass, Class<?> factoryClass) {
		super(itemClass);
		this.factoryClass = factoryClass;
	}
	
	public FxFactory<ITEM> createFxFactory() {
		FxFactory<ITEM> fxFactory = null;
		try {
			fxFactory = (FxFactory<ITEM>) factoryClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		fxFactory.setTableView(this);
		return fxFactory;
	}
	
}
