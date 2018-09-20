package org.artorg.tools.phantomData.client.tables;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.FileEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.client.tables.filterable.FileFilterTable;
import org.artorg.tools.phantomData.server.model.PhantomFile;

public class FileTable extends TableViewSpring<PhantomFile> {

	{
		this.setTable(new FileFilterTable());
	}
	
	@Override
	public ItemEditFactoryController<PhantomFile> createAddEditController() {
		return new FileEditFactoryController(this);
	}

}
