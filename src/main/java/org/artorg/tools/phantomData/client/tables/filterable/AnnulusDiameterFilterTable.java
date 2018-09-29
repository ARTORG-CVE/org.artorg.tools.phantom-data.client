package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoEditFilterTable;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.IColumn;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;

public class AnnulusDiameterFilterTable extends DbUndoRedoEditFilterTable<AnnulusDiameter> {
	
	{
		setItemClass(AnnulusDiameter.class);
		
		List<IColumn<AnnulusDiameter>> columns =
				new ArrayList<IColumn<AnnulusDiameter>>();
		columns.add(new Column<AnnulusDiameter, AnnulusDiameter>(
				"shortcut", item -> item, 
				path -> String.valueOf(path.getShortcut()), 
				(path,value) -> path.setShortcut(Integer.valueOf(value))));
		columns.add(new Column<AnnulusDiameter, AnnulusDiameter>(
				"value", item -> item, 
				path -> String.valueOf(path.getValue()), 
				(path,value) -> path.setValue(Double.valueOf(value))));
		this.setColumns(columns);
		
		this.setTableName("Annulus Diameters");
		
	}

}