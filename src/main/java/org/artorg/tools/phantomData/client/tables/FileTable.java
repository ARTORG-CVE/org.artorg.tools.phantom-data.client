package org.artorg.tools.phantomData.client.tables;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.FileEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.DbEditFilterTableView;
import org.artorg.tools.phantomData.client.tables.filterable.FileFilterTable;
import org.artorg.tools.phantomData.server.model.PhantomFile;

public class FileTable extends DbEditFilterTableView<PhantomFile> {

	{
		this.setTable(new FileFilterTable());
		
	}
	
	@Override
	public ItemEditFactoryController<PhantomFile> createAddEditController() {
		return new FileEditFactoryController(this);
	}

}
