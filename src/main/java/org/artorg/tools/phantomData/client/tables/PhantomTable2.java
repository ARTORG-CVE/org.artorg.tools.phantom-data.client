package org.artorg.tools.phantomData.client.tables;

import org.artorg.tools.phantomData.client.scene.control.TableViewSpringReadOnly;
import org.artorg.tools.phantomData.client.tables.filterable.PhantomFilterTable2;
import org.artorg.tools.phantomData.server.model.Phantom;

public class PhantomTable2 extends TableViewSpringReadOnly<Phantom> {

	{
		this.setTable(new PhantomFilterTable2());
	}

}
