package org.artorg.tools.phantomData.client.tables;

import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controllers.editTable.AddPropertyFieldController;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.client.tables.filterable.AnnulusDiameterFilterTable;
import org.artorg.tools.phantomData.client.tables.filterable.PropertyFieldFilterTable;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

public class PropertyFieldTable extends TableViewSpring<PropertyField, Integer> {

	{
		this.setTable(new PropertyFieldFilterTable());
	}
	
	@Override
	protected AddEditController<PropertyField, Integer> createAddEditController() {
		return new AddPropertyFieldController();
	}

}
