package org.artorg.tools.phantomData.client.tables;

import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controllers.editTable.AddAnnulusDiameterController;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.client.tables.filterable.AnnulusDiameterFilterTable;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;

public class AnnulusDiameterTable extends TableViewSpring<AnnulusDiameter, Integer> {

	{
		this.setTable(new AnnulusDiameterFilterTable());
	}
	
	@Override
	protected AddEditController<AnnulusDiameter, Integer> createAddEditController() {
		return new AddAnnulusDiameterController();
	}
	
}
