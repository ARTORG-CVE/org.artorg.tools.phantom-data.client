package org.artorg.tools.phantomData.client.tablesView;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.FileTypeEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoAddEditControlFilterTableView;
import org.artorg.tools.phantomData.client.tablesFilter.FileTypeFilterTable;
import org.artorg.tools.phantomData.server.model.FileType;

public class FileTypeTable extends DbUndoRedoAddEditControlFilterTableView<FileType> {

	{
		this.setTable(new FileTypeFilterTable());
	}
	
	@Override
	public ItemEditFactoryController<FileType> createAddEditController() {
		return new FileTypeEditFactoryController(this);
	}

}
