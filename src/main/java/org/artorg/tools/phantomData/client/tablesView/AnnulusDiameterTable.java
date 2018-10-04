package org.artorg.tools.phantomData.client.tablesView;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.AnnulusDiameterEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoAddEditControlFilterTableView;
import org.artorg.tools.phantomData.client.tablesFilter.AnnulusDiameterFilterTable;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;

public class AnnulusDiameterTable extends DbUndoRedoAddEditControlFilterTableView<AnnulusDiameter> {

	{
		this.setTable(new AnnulusDiameterFilterTable());
	}
	
	@Override
	public ItemEditFactoryController<AnnulusDiameter> createAddEditController() {
		return new AnnulusDiameterEditFactoryController(this);
	}
	
}
