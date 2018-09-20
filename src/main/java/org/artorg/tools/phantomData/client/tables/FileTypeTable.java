package org.artorg.tools.phantomData.client.tables;

import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controllers.editTable.AddFileTypeController;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.client.tables.filterable.FileTypeFilterTable;
import org.artorg.tools.phantomData.server.model.FileType;

public class FileTypeTable extends TableViewSpring<FileType> {

	{
		this.setTable(new FileTypeFilterTable());
	}
	
	@Override
	public AddEditController<FileType> createAddEditController() {
		return new AddFileTypeController(this);
	}

}
