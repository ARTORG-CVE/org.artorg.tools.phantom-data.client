package org.artorg.tools.phantomData.client.tables;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.FabricationTypeEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.TableViewSpringEditFilterable;
import org.artorg.tools.phantomData.client.tables.filterable.FabricationTypeFilterTable;
import org.artorg.tools.phantomData.server.model.FabricationType;

public class FabricationTypeTable extends TableViewSpringEditFilterable<FabricationType> {

	{
		this.setTable(new FabricationTypeFilterTable());
	}
	
	@Override
	public ItemEditFactoryController<FabricationType> createAddEditController() {
		return new FabricationTypeEditFactoryController(this);
	}

}
