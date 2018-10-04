package org.artorg.tools.phantomData.client.tablesView;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.FileEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoAddEditControlFilterTableView;
import org.artorg.tools.phantomData.client.tablesFilter.FileFilterTable;
import org.artorg.tools.phantomData.server.model.PhantomFile;

public class FileTable extends DbUndoRedoAddEditControlFilterTableView<PhantomFile> {

	{
		this.setTable(new FileFilterTable());
		
	}
	
	@Override
	public ItemEditFactoryController<PhantomFile> createAddEditController() {
		return new FileEditFactoryController(this);
	}

}
