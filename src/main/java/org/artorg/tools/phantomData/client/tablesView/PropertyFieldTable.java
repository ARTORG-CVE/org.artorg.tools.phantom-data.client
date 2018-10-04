package org.artorg.tools.phantomData.client.tablesView;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.PropertyFieldEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoAddEditControlFilterTableView;
import org.artorg.tools.phantomData.client.tablesFilter.PropertyFieldFilterTable;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

public class PropertyFieldTable extends DbUndoRedoAddEditControlFilterTableView<PropertyField> {

	{
		this.setTable(new PropertyFieldFilterTable());
	}
	
	@Override
	public ItemEditFactoryController<PropertyField> createAddEditController() {
		return new PropertyFieldEditFactoryController(this);
	}

}
