package org.artorg.tools.phantomData.client.tables;

import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controllers.editTable.AddFabricationTypeController;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.client.tables.filterable.FabricationTypeFilterTable;
import org.artorg.tools.phantomData.server.model.FabricationType;

public class FabricationTypeTable extends TableViewSpring<FabricationType> {

	{
		this.setTable(new FabricationTypeFilterTable());
	}
	
	@Override
	public AddEditController<FabricationType> createAddEditController() {
		return new AddFabricationTypeController();
	}

}
