package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoEditFilterTable;
import org.artorg.tools.phantomData.client.table.LambdaColumn;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;

public class AnnulusDiameterFilterTable extends DbUndoRedoEditFilterTable<AnnulusDiameter> {
	
	{
		setItemClass(AnnulusDiameter.class);
		
		List<Column<AnnulusDiameter>> columns =
				new ArrayList<Column<AnnulusDiameter>>();
		columns.add(new LambdaColumn<AnnulusDiameter, AnnulusDiameter>(
				"shortcut", item -> item, 
				path -> String.valueOf(path.getShortcut()), 
				(path,value) -> path.setShortcut(Integer.valueOf(value))));
		columns.add(new LambdaColumn<AnnulusDiameter, AnnulusDiameter>(
				"value", item -> item, 
				path -> String.valueOf(path.getValue()), 
				(path,value) -> path.setValue(Double.valueOf(value))));
		this.setColumns(columns);
		
		this.setTableName("Annulus Diameters");
		
	}

}