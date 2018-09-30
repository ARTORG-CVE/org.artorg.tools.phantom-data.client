package org.artorg.tools.phantomData.client.tables;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.PropertyFieldEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoEditFilterTableView;
import org.artorg.tools.phantomData.client.tables.filterable.PropertyFieldFilterTable;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

public class PropertyFieldTable extends DbUndoRedoEditFilterTableView<PropertyField> {

	{
		this.setTable(new PropertyFieldFilterTable());
	}
	
	@Override
	public ItemEditFactoryController<PropertyField> createAddEditController() {
		return new PropertyFieldEditFactoryController(this);
	}

}
