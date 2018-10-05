package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.table.DbFxFactory;
import org.artorg.tools.phantomData.client.table.IDbTable;
import org.artorg.tools.phantomData.client.table.IFilterTable;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

@SuppressWarnings("unchecked")
public class DbFactoryFilterTableView<ITEM extends DbPersistent<ITEM,?>, TABLE_TYPE extends IDbTable<ITEM> & IFilterTable<ITEM>> extends DbFilterTableView<ITEM,TABLE_TYPE> {
	private final Class<ITEM> itemClass;
	private final Class<?> factoryClass;
	
	public DbFactoryFilterTableView() {
		itemClass = (Class<ITEM>) Reflect.findSubClassParameterType(this, DbFilterTableView.class, 0);
		factoryClass = Reflect.getClassByGenericAndSuperClass(ItemEditFactoryController.class, this.itemClass, 0, Main.getReflections());
	}
	
	public DbFactoryFilterTableView(Class<ITEM> itemClass) {
		this.itemClass = itemClass;
		factoryClass = Reflect.getClassByGenericAndSuperClass(ItemEditFactoryController.class, this.itemClass, 0, Main.getReflections());
	}
	
	public DbFactoryFilterTableView(Class<ITEM> itemClass, Class<?> factoryClass) {
		this.itemClass = itemClass;
		this.factoryClass = factoryClass;
	}
	
	
	
	public DbFxFactory<ITEM> createFxFactory() {
		DbFxFactory<ITEM> fxFactory = null;
		try {
			fxFactory = (DbFxFactory<ITEM>) factoryClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		fxFactory.setTable((DbTableView<ITEM, TABLE_TYPE>) this);
		return fxFactory;
	}
	
}
