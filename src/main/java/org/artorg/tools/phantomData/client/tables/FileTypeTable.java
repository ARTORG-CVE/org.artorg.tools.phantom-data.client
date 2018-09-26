package org.artorg.tools.phantomData.client.tables;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.FileTypeEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.TableViewSpringEditFilterable;
import org.artorg.tools.phantomData.client.tables.filterable.FileTypeFilterTable;
import org.artorg.tools.phantomData.server.model.FileType;

public class FileTypeTable extends TableViewSpringEditFilterable<FileType> {

	{
		this.setTable(new FileTypeFilterTable());
	}
	
	@Override
	public ItemEditFactoryController<FileType> createAddEditController() {
		return new FileTypeEditFactoryController(this);
	}

}
