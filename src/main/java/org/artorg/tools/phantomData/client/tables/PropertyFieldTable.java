package org.artorg.tools.phantomData.client.tables;

import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controllers.editTable.AddPropertyFieldController;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.client.tables.filterable.PropertyFieldFilterTable;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

public class PropertyFieldTable extends TableViewSpring<PropertyField> {

	{
		this.setTable(new PropertyFieldFilterTable());
	}
	
	@Override
	public AddEditController<PropertyField> createAddEditController() {
		return new AddPropertyFieldController(this);
	}

}
