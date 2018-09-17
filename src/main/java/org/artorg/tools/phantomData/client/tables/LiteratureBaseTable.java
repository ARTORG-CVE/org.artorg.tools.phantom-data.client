package org.artorg.tools.phantomData.client.tables;

import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controllers.editTable.AddLiteratureBaseController;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.client.tables.filterable.AnnulusDiameterFilterTable;
import org.artorg.tools.phantomData.client.tables.filterable.LiteratureBaseFilterTable;
import org.artorg.tools.phantomData.server.model.LiteratureBase;

public class LiteratureBaseTable extends TableViewSpring<LiteratureBase, Integer> {

	{
		this.setTable(new LiteratureBaseFilterTable());
	}
	
	@Override
	protected AddEditController<LiteratureBase, Integer> createAddEditController() {
		return new AddLiteratureBaseController();
	}
	

}
