package org.artorg.tools.phantomData.client.tables.property;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.property.DoublePropertyEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.TableViewSpring;
import org.artorg.tools.phantomData.client.tables.filterable.property.DoublePropertyFilterTable;
import org.artorg.tools.phantomData.server.model.property.DoubleProperty;

public class DoublePropertyTable extends TableViewSpring<DoubleProperty> {

	{
		this.setTable(new DoublePropertyFilterTable());
	}
	
	@Override
	public ItemEditFactoryController<DoubleProperty> createAddEditController() {
		return new DoublePropertyEditFactoryController(this);
	}

}
