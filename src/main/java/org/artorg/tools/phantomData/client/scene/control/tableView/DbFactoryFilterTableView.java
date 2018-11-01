package org.artorg.tools.phantomData.client.scene.control.tableView;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.table.FxFactory;
import org.artorg.tools.phantomData.client.table.IDbFactoryTableView;
import org.artorg.tools.phantomData.server.specification.DbPersistent;
import org.artorg.tools.phantomData.server.util.Reflect;

@SuppressWarnings("unchecked")
public class DbFactoryFilterTableView<ITEM extends DbPersistent<ITEM, ?>>
	extends DbFilterTableView<ITEM> implements IDbFactoryTableView<ITEM> {

	public DbFactoryFilterTableView() {
		super();
	}

	public DbFactoryFilterTableView(Class<ITEM> itemClass) {
		super(itemClass);
	}

	public FxFactory<ITEM> createFxFactory() {
		FxFactory<ITEM> fxFactory = null;

		Class<?> factoryClass = null;
		try {
			factoryClass =
				Reflect.getClassByGenericAndSuperClass(ItemEditFactoryController.class,
					super.getItemClass(), 0, Main.getReflections());
			fxFactory = (FxFactory<ITEM>) factoryClass.newInstance();
			fxFactory.setTableView(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return fxFactory;
	}

}
