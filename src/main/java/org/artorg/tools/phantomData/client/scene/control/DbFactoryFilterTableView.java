package org.artorg.tools.phantomData.client.scene.control;

import java.util.List;

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
		itemClass = (Class<ITEM>) Reflect.findGenericClasstype(this);
		List<Class<?>> factoryClasses = Reflect.getSubclasses(ItemEditFactoryController.class, Main.getReflections());
		factoryClass = factoryClasses.stream().filter(c -> {
			try {
				return Reflect.findSubClassParameterType(c.newInstance(), ItemEditFactoryController.class, 0) == this.itemClass;
			} catch (Exception e2) {
			}
			return false;
		}).findFirst().orElseThrow(() -> new IllegalArgumentException());
		if (factoryClass == null)
			throw new NullPointerException();
		
		
		
		
		
		System.out.println(factoryClass);
		System.out.println();
		
	}
	
	
	public FxFactory<ITEM> createFxFactory() {
		FxFactory<ITEM> fxFactory = null;
		try {
			fxFactory = (FxFactory<ITEM>) factoryClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		fxFactory.setTable((DbTableView<ITEM, TABLE>) this.getTable());
		return fxFactory;
	}

//	public abstract ItemEditFactoryController<ITEM> createAddEditController();
	
}
