package org.artorg.tools.phantomData.client.tables;

import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controllers.editTable.AddPhantomController;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.client.tables.filterable.PhantomFilterTable;
import org.artorg.tools.phantomData.server.model.Phantom;

public class PhantomTable extends TableViewSpring<Phantom> {

	{
		this.setTable(new PhantomFilterTable());
	}
	
	@Override
	public AddEditController<Phantom> createAddEditController() {
		return new AddPhantomController(this);
	}

}
