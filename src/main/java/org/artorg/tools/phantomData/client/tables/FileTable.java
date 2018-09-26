package org.artorg.tools.phantomData.client.tables;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.FileEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.TableViewSpringEditFilterable;
import org.artorg.tools.phantomData.client.tables.filterable.FileFilterTable;
import org.artorg.tools.phantomData.server.model.PhantomFile;

public class FileTable extends TableViewSpringEditFilterable<PhantomFile> {

	{
		this.setTable(new FileFilterTable());
	}
	
	@Override
	public ItemEditFactoryController<PhantomFile> createAddEditController() {
		return new FileEditFactoryController(this);
	}

}
