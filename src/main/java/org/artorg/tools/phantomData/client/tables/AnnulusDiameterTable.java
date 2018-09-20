package org.artorg.tools.phantomData.client.tables;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.AnnulusDiameterEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.client.tables.filterable.AnnulusDiameterFilterTable;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;

public class AnnulusDiameterTable extends TableViewSpring<AnnulusDiameter> {

	{
		this.setTable(new AnnulusDiameterFilterTable());
	}
	
	@Override
	public ItemEditFactoryController<AnnulusDiameter> createAddEditController() {
		return new AnnulusDiameterEditFactoryController(this);
	}
	
}
