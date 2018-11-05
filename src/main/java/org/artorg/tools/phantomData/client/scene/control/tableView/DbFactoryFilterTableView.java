package org.artorg.tools.phantomData.client.scene.control.tableView;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.table.FxFactory;
import org.artorg.tools.phantomData.client.table.IDbFactoryTableView;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

@SuppressWarnings("unchecked")
public class DbFactoryFilterTableView<ITEM extends DbPersistent<ITEM, ?>>
	extends DbFilterTableView<ITEM> implements IDbFactoryTableView<ITEM> {
	private Class<?> factoryClass;

	{
		factoryClass = null;
	}

	public DbFactoryFilterTableView() {
		super();
	}

	public DbFactoryFilterTableView(Class<ITEM> itemClass) {
		super(itemClass);
	}
	
	@Override
	public void initTable() {
		super.initTable();
		findFactoryClass();
	}

	public FxFactory<ITEM> createFxFactory() {
		FxFactory<ITEM> fxFactory = null;

		findFactoryClass();
		try {
			fxFactory = (FxFactory<ITEM>) factoryClass.newInstance();
			fxFactory.setTableView(this);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return fxFactory;
	}
	
	private void findFactoryClass() {
		if (factoryClass == null) {
			try {
				factoryClass = Reflect.getClassByGenericAndSuperClass(
					ItemEditFactoryController.class, super.getItemClass(), 0,
					Main.getReflections());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
