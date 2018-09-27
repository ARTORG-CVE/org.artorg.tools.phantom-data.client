package org.artorg.tools.phantomData.client.tables.property;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.property.StringPropertyEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.TableViewEditFilterable;
import org.artorg.tools.phantomData.client.tables.filterable.property.StringPropertyFilterTable;
import org.artorg.tools.phantomData.server.model.property.StringProperty;

public class StringPropertyTable extends TableViewEditFilterable<StringProperty> {

	{
		this.setTable(new StringPropertyFilterTable());
	}
	
	@Override
	public ItemEditFactoryController<StringProperty> createAddEditController() {
		return new StringPropertyEditFactoryController(this);
	}

}
