package org.artorg.tools.phantomData.client.tables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.artorg.tools.phantomData.client.commandPattern.PropertyUndoable;
import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.connectors.FabricationTypeConnector;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.ColumnOptional;
import org.artorg.tools.phantomData.client.table.IColumn;
import org.artorg.tools.phantomData.client.table.StageTable;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.FabricationType;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class FabricationTypeTable extends StageTable<FabricationTypeTable, FabricationType, Integer> {

	@Override
	public HttpDatabaseCrud<FabricationType, Integer> getConnector() {
		return FabricationTypeConnector.get();
	}

	@Override
	public List<IColumn<FabricationType, ?, ?>> createColumns() {
		List<IColumn<FabricationType, ?, ?>> columns =
				new ArrayList<IColumn<FabricationType, ?, ?>>();
		columns.add(new Column<FabricationType, FabricationType, Integer, Integer>(
				"id", item -> item, 
				path -> path.getId(), 
				(path,value) -> path.setId(value)));
		columns.add(new Column<FabricationType, FabricationType, String, Integer>(
				"shortcut", item -> item, 
				path -> path.getShortcut(), 
				(path,value) -> path.setShortcut(value)));
		columns.add(new Column<FabricationType, FabricationType, String, Integer>(
				"value", item -> item, 
				path -> path.getValue(), 
				(path,value) -> path.setValue(value)));
		return columns;
	}

}
