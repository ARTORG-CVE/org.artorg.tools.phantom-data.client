package org.artorg.tools.phantomData.client.tables.property;

import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controllers.editTable.property.AddBooleanPropertyController;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.client.tables.filterable.property.BooleanPropertyFilterTable;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;

public class BooleanPropertyTable extends TableViewSpring<BooleanProperty> {

	{
		this.setTable(new BooleanPropertyFilterTable());
	}
	
	@Override
	public AddEditController<BooleanProperty> createAddEditController() {
		return new AddBooleanPropertyController();
	}

}
