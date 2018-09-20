package org.artorg.tools.phantomData.client.tables.property;

import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controllers.editTable.property.AddDoublePropertyController;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.client.tables.filterable.property.DoublePropertyFilterTable;
import org.artorg.tools.phantomData.server.model.property.DoubleProperty;

public class DoublePropertyTable extends TableViewSpring<DoubleProperty> {

	{
		this.setTable(new DoublePropertyFilterTable());
	}
	
	@Override
	public AddEditController<DoubleProperty> createAddEditController() {
		return new AddDoublePropertyController(this);
	}

}
