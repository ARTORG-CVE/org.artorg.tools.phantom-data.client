package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.control.DbFilterTable;
import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.FabricationType;
import org.artorg.tools.phantomData.server.model.LiteratureBase;
import org.artorg.tools.phantomData.server.model.Phantom;
import org.artorg.tools.phantomData.server.model.Special;

public class PhantomFilterTable2 extends DbFilterTable<Phantom> {

	{
		List<AbstractColumn<Phantom>> columns =
				new ArrayList<AbstractColumn<Phantom>>();
		AbstractColumn<Phantom> column;
		columns.add(new FilterColumn<Phantom>(
				"PID", item -> item, 
				path -> path.getProductId(), 
				(path,value) -> path.setProductId(value)));
		columns.add(new FilterColumn<Phantom>(
				"annulus [mm]", item -> item.getAnnulusDiameter(), 
				path -> String.valueOf(path.getValue()), 
				(path,value) -> path.setValue(Double.valueOf(value))));
		columns.add(new FilterColumn<Phantom>(
				"type", item -> item.getFabricationType(), 
				path -> path.getValue(), 
				(path,value) -> path.setValue(value)));
		columns.add(new FilterColumn<Phantom>(
				"literature", item -> item.getLiteratureBase(), 
				path -> path.getValue(), 
				(path,value) -> path.setValue(value)));
		column = new FilterColumn<Phantom>(
				"special", item -> item.getSpecial(), 
				path -> path.getShortcut(), 
				(path,value) -> path.setShortcut(value));
		columns.add(column);
		columns.add(new FilterColumn<Phantom>(
				"number", item -> item, 
				path -> String.valueOf(path.getNumber()), 
				(path,value) -> path.setNumber(Integer.valueOf(value))));
		this.setColumns(columns);
		
		this.setTableName("Phantoms");
	}

}
