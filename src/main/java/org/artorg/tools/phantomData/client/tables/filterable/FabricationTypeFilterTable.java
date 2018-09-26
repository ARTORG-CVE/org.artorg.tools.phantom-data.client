package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.control.FilterTableSpringDb;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.IColumn;
import org.artorg.tools.phantomData.server.model.FabricationType;

public class FabricationTypeFilterTable extends FilterTableSpringDb<FabricationType> {
	
	{
		setItemClass(FabricationType.class);
	}
	
	@Override
	public List<IColumn<FabricationType>> createColumns() {
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
		return columns;
	}

	@Override
	public String getTableName() {
		return "Fabrication Types";
	}

}
