package org.artorg.tools.phantomData.client.tables;

import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controllers.editTable.AddSpecialController;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.client.tables.filterable.AnnulusDiameterFilterTable;
import org.artorg.tools.phantomData.client.tables.filterable.SpecialFilterTable;
import org.artorg.tools.phantomData.server.model.Special;

public class SpecialTable extends TableViewSpring<Special, Integer> {

	{
		this.setTable(new SpecialFilterTable());
	}
	
	@Override
	protected AddEditController<Special, Integer> createAddEditController() {
		return new AddSpecialController();
	}

}
