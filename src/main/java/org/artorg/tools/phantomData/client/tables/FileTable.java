package org.artorg.tools.phantomData.client.tables;

import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controllers.editTable.AddFileController;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.client.tables.filterable.FileFilterTable;
import org.artorg.tools.phantomData.server.model.PhantomFile;

public class FileTable extends TableViewSpring<PhantomFile> {

	{
		this.setTable(new FileFilterTable());
	}
	
	@Override
	public AddEditController<PhantomFile> createAddEditController() {
		return new AddFileController();
	}

}
