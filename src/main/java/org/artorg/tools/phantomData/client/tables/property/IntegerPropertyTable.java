package org.artorg.tools.phantomData.client.tables.property;

import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controllers.editTable.property.AddIntegerPropertyController;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.client.tables.filterable.property.IntegerPropertyFilterTable;
import org.artorg.tools.phantomData.server.model.property.IntegerProperty;

public class IntegerPropertyTable extends TableViewSpring<IntegerProperty, Integer> {

	{
		this.setTable(new IntegerPropertyFilterTable());
	}
	
	@Override
	protected AddEditController<IntegerProperty, Integer> createAddEditController() {
		return new AddIntegerPropertyController();
	}

}
