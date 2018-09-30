package org.artorg.tools.phantomData.client.tables.property;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.property.BooleanPropertyEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoEditFilterTableView;
import org.artorg.tools.phantomData.client.tables.filterable.property.BooleanPropertyFilterTable;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;

public class BooleanPropertyTable extends DbUndoRedoEditFilterTableView<BooleanProperty> {

	{
		this.setTable(new BooleanPropertyFilterTable());
	}
	
	@Override
	public ItemEditFactoryController<BooleanProperty> createAddEditController() {
		return new BooleanPropertyEditFactoryController(this);
	}

}
