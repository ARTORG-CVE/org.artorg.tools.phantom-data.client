package org.artorg.tools.phantomData.client.tables;

import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controllers.editTable.AddAnnulusDiameterController;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.client.tables.filterable.AnnulusDiameterFilterTable;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;

public class AnnulusDiameterTable extends TableViewSpring<AnnulusDiameter> {

	{
		this.setTable(new AnnulusDiameterFilterTable());
	}
	
	@Override
	public AddEditController<AnnulusDiameter> createAddEditController() {
		return new AddAnnulusDiameterController(this);
	}
	
}
