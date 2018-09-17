package org.artorg.tools.phantomData.client.tables;

import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controllers.editTable.AddBooleanPropertyController;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.client.tables.filterable.BooleanPropertyFilterTable;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;

public class BooleanPropertyTable extends TableViewSpring<BooleanProperty, Integer> {

	{
		this.setTable(new BooleanPropertyFilterTable());
	}
	
	@Override
	protected AddEditController<BooleanProperty, Integer> createAddEditController() {
		return new AddBooleanPropertyController();
	}

}
