package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connectors.FabricationTypeConnector;
import org.artorg.tools.phantomData.client.scene.control.table.Column;
import org.artorg.tools.phantomData.client.scene.control.table.FilterTableSpringDb;
import org.artorg.tools.phantomData.client.scene.control.table.IColumn;
import org.artorg.tools.phantomData.server.model.FabricationType;

public class FabricationTypeFilterTable extends FilterTableSpringDb<FabricationType, Integer> {
	
	{
		this.setConnector(FabricationTypeConnector.get());
	}
	
	@Override
	public List<IColumn<FabricationType, ?>> createColumns() {
		List<IColumn<FabricationType, ?>> columns =
				new ArrayList<IColumn<FabricationType, ?>>();
		columns.add(new Column<FabricationType, FabricationType, Integer>(
				"id", item -> item, 
				path -> String.valueOf(path.getId()), 
				(path,value) -> path.setId(Integer.valueOf(value)),
				FabricationTypeConnector.get()));
		columns.add(new Column<FabricationType, FabricationType, Integer>(
				"shortcut", item -> item, 
				path -> path.getShortcut(), 
				(path,value) -> path.setShortcut(value),
				FabricationTypeConnector.get()));
		columns.add(new Column<FabricationType, FabricationType, Integer>(
				"value", item -> item, 
				path -> path.getValue(), 
				(path,value) -> path.setValue(value),
				FabricationTypeConnector.get()));
		return columns;
	}

	@Override
	public String getTableName() {
		return "Fabrication Types";
	}

}
