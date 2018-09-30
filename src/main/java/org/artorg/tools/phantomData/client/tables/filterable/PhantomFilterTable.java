package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoEditFilterTable;
import org.artorg.tools.phantomData.client.table.LambdaColumn;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.FabricationType;
import org.artorg.tools.phantomData.server.model.LiteratureBase;
import org.artorg.tools.phantomData.server.model.Phantom;
import org.artorg.tools.phantomData.server.model.Special;

public class PhantomFilterTable extends DbUndoRedoEditFilterTable<Phantom> {

	{
		setItemClass(Phantom.class);
		
		List<Column<Phantom>> columns =
				new ArrayList<Column<Phantom>>();
		Column<Phantom> column;
		columns.add(new LambdaColumn<Phantom, Phantom>(
				"PID", item -> item, 
				path -> path.getProductId(), 
				(path,value) -> path.setProductId(value)));
		columns.add(new LambdaColumn<Phantom, AnnulusDiameter>(
				"annulus [mm]", item -> item.getAnnulusDiameter(), 
				path -> String.valueOf(path.getValue()), 
				(path,value) -> path.setValue(Double.valueOf(value))));
		columns.add(new LambdaColumn<Phantom, FabricationType>(
				"type", item -> item.getFabricationType(), 
				path -> path.getValue(), 
				(path,value) -> path.setValue(value)));
		columns.add(new LambdaColumn<Phantom, LiteratureBase>(
				"literature", item -> item.getLiteratureBase(), 
				path -> path.getValue(), 
				(path,value) -> path.setValue(value)));
		column = new LambdaColumn<Phantom, Special>(
				"special", item -> item.getSpecial(), 
				path -> path.getShortcut(), 
				(path,value) -> path.setShortcut(value));
		columns.add(column);
		columns.add(new LambdaColumn<Phantom, Phantom>(
				"number", item -> item, 
				path -> String.valueOf(path.getNumber()), 
				(path,value) -> path.setNumber(Integer.valueOf(value))));
		this.setColumns(columns);
		
		this.setTableName("Phantoms");
		
	}

}
