package org.artorg.tools.phantomData.client.tables.property;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.property.IntegerPropertyEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoEditFilterTableView;
import org.artorg.tools.phantomData.client.tables.filterable.property.IntegerPropertyFilterTable;
import org.artorg.tools.phantomData.server.model.property.IntegerProperty;

public class IntegerPropertyTable extends DbUndoRedoEditFilterTableView<IntegerProperty> {

	{
		this.setTable(new IntegerPropertyFilterTable());
	}
	
	@Override
	public ItemEditFactoryController<IntegerProperty> createAddEditController() {
		return new IntegerPropertyEditFactoryController(this);
	}

}
