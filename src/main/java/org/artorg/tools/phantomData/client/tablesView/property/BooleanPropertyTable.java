package org.artorg.tools.phantomData.client.tablesView.property;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.SpecialEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.property.BooleanPropertyEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoAddEditControlFilterTableView;
import org.artorg.tools.phantomData.client.tablesFilter.property.BooleanPropertyFilterTable;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;

public class BooleanPropertyTable extends DbUndoRedoAddEditControlFilterTableView<BooleanProperty> {

	{
		this.setTable(new BooleanPropertyFilterTable());
	}
	
//	@Override
//	public ItemEditFactoryController<BooleanProperty> createAddEditController() {
//		BooleanPropertyEditFactoryController factory = new BooleanPropertyEditFactoryController();
//		factory.setTable(this);
//		return factory;
//	}

}
