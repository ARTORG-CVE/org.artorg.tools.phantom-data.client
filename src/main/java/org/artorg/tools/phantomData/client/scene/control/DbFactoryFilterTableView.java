package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.scene.DbFilterTableView;
import org.artorg.tools.phantomData.client.table.FxFactory;
import org.artorg.tools.phantomData.client.table.IDbTable;
import org.artorg.tools.phantomData.client.table.IFilterTable;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

@SuppressWarnings("unchecked")
public abstract class DbFactoryFilterTableView<ITEM extends DbPersistent<ITEM,?>, TABLE extends IDbTable<ITEM> & IFilterTable<ITEM>> extends DbFilterTableView<ITEM,TABLE> {
	private final Class<ITEM> itemClass;
	private final Class<?> factoryClass;
	
	{
		itemClass = (Class<ITEM>) Reflect.findSubClassParameterType(this, DbFilterTableView.class, 0);
		factoryClass = Reflect.getClassByGenericAndSuperClass(ItemEditFactoryController.class, this.itemClass, 0, Main.getReflections());
	}
	
	public FxFactory<ITEM> createFxFactory() {
		FxFactory<ITEM> fxFactory = null;
		try {
			fxFactory = (FxFactory<ITEM>) factoryClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		fxFactory.setTable((DbTableView<ITEM, TABLE>) this);
		return fxFactory;
	}
	
}
