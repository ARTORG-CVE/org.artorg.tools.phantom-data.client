package org.artorg.tools.phantomData.client.tablesFilter;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;

public class AnnulusDiameterFilterTable extends DbUndoRedoFactoryEditFilterTable<AnnulusDiameter> {
	
	{
		List<AbstractColumn<AnnulusDiameter>> columns =
				new ArrayList<AbstractColumn<AnnulusDiameter>>();
		columns.add(new FilterColumn<AnnulusDiameter>(
				"shortcut", item -> item, 
				path -> String.valueOf(path.getShortcut()), 
				(path,value) -> path.setShortcut(Integer.valueOf(value))));
		columns.add(new FilterColumn<AnnulusDiameter>(
				"value", item -> item, 
				path -> String.valueOf(path.getValue()), 
				(path,value) -> path.setValue(Double.valueOf(value))));
		this.setColumns(columns);
		
		this.setTableName("Annulus Diameters");
		
	}

}