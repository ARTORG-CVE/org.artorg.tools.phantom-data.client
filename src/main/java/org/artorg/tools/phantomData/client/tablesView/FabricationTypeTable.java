package org.artorg.tools.phantomData.client.tablesView;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.FabricationTypeEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoAddEditControlFilterTableView;
import org.artorg.tools.phantomData.client.tablesFilter.FabricationTypeFilterTable;
import org.artorg.tools.phantomData.server.model.FabricationType;

public class FabricationTypeTable extends DbUndoRedoAddEditControlFilterTableView<FabricationType> {

	{
		this.setTable(new FabricationTypeFilterTable());
	}
	
//	@Override
//	public ItemEditFactoryController<FabricationType> createAddEditController() {
//		FabricationTypeEditFactoryController factory = new FabricationTypeEditFactoryController();
//		factory.setTable(this);
//		return factory;
//	}

}
