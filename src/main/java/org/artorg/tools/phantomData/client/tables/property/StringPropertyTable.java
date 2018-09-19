package org.artorg.tools.phantomData.client.tables.property;

import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controllers.editTable.property.AddStringPropertyController;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.client.tables.filterable.property.StringPropertyFilterTable;
import org.artorg.tools.phantomData.server.model.property.StringProperty;

public class StringPropertyTable extends TableViewSpring<StringProperty> {

	{
		this.setTable(new StringPropertyFilterTable());
	}
	
	@Override
	public AddEditController<StringProperty> createAddEditController() {
		return new AddStringPropertyController();
	}

}
