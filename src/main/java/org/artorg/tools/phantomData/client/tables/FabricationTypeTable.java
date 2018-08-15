package org.artorg.tools.phantomData.client.tables;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connectors.FabricationTypeConnector;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.IColumn;
import org.artorg.tools.phantomData.client.table.StageTable;
import org.artorg.tools.phantomData.server.model.FabricationType;

public class FabricationTypeTable extends StageTable<FabricationTypeTable, FabricationType, Integer> {
	
	{
		this.setConnector(FabricationTypeConnector.get());
	}
	
	@Override
	public List<IColumn<FabricationType, ?, ?>> createColumns() {
		List<IColumn<FabricationType, ?, ?>> columns =
				new ArrayList<IColumn<FabricationType, ?, ?>>();
		columns.add(new Column<FabricationType, FabricationType, Integer, Integer>(
				"id", item -> item, 
				path -> path.getId(), 
				(path,value) -> path.setId((Integer) value),
				FabricationTypeConnector.get()));
		columns.add(new Column<FabricationType, FabricationType, String, Integer>(
				"shortcut", item -> item, 
				path -> path.getShortcut(), 
				(path,value) -> path.setShortcut((String) value),
				FabricationTypeConnector.get()));
		columns.add(new Column<FabricationType, FabricationType, String, Integer>(
				"value", item -> item, 
				path -> path.getValue(), 
				(path,value) -> path.setValue((String) value),
				FabricationTypeConnector.get()));
		return columns;
	}

}
