package org.artorg.tools.phantomData.client.tablesView.property;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.property.DoublePropertyEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoAddEditControlFilterTableView;
import org.artorg.tools.phantomData.client.tablesFilter.property.DoublePropertyFilterTable;
import org.artorg.tools.phantomData.server.model.property.DoubleProperty;

public class DoublePropertyTable extends DbUndoRedoAddEditControlFilterTableView<DoubleProperty> {

	{
		this.setTable(new DoublePropertyFilterTable());
	}
	
	@Override
	public ItemEditFactoryController<DoubleProperty> createAddEditController() {
		return new DoublePropertyEditFactoryController(this);
	}

}
