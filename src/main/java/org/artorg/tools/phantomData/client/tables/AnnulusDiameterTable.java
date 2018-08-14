package org.artorg.tools.phantomData.client.tables;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.connectors.AnnulusDiameterConnector;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.IColumn;
import org.artorg.tools.phantomData.client.table.StageTable;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;

public class AnnulusDiameterTable extends StageTable<AnnulusDiameterTable, AnnulusDiameter, Integer> {
	
	@Override
	public HttpDatabaseCrud<AnnulusDiameter, Integer> getConnector() {
		return AnnulusDiameterConnector.get();
	}

	@Override
	public List<IColumn<AnnulusDiameter, ?, ?>> createColumns() {
		List<IColumn<AnnulusDiameter, ?, ?>> columns =
				new ArrayList<IColumn<AnnulusDiameter, ?, ?>>();
		columns.add(new Column<AnnulusDiameter, AnnulusDiameter, Integer, Integer>(
				"id", item -> item, 
				path -> path.getId(), 
				(path,value) -> path.setId(value)));
		columns.add(new Column<AnnulusDiameter, AnnulusDiameter, Integer, Integer>(
				"shortcut", item -> item, 
				path -> path.getShortcut(), 
				(path,value) -> path.setShortcut(value)));
		columns.add(new Column<AnnulusDiameter, AnnulusDiameter, Double, Integer>(
				"value", item -> item, 
				path -> path.getValue(), 
				(path,value) -> path.setValue(value)));
		return columns;
	}

}