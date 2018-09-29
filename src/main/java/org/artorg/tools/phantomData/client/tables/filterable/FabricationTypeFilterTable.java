package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoEditFilterTable;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.IColumn;
import org.artorg.tools.phantomData.server.model.FabricationType;

public class FabricationTypeFilterTable extends DbUndoRedoEditFilterTable<FabricationType> {
	
	{
		setItemClass(FabricationType.class);
		
		List<IColumn<FabricationType>> columns =
				new ArrayList<IColumn<FabricationType>>();
		columns.add(new Column<FabricationType, FabricationType>(
				"shortcut", item -> item, 
				path -> path.getShortcut(), 
				(path,value) -> path.setShortcut(value)));
		columns.add(new Column<FabricationType, FabricationType>(
				"value", item -> item, 
				path -> path.getValue(), 
				(path,value) -> path.setValue(value)));
		this.setColumns(columns);
		
		this.setTableName("Fabrication Types");
	}

}
