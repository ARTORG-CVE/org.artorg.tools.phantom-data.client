package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connector.AnnulusDiameterConnector;
import org.artorg.tools.phantomData.client.specification.Column;
import org.artorg.tools.phantomData.client.specification.Column2;
import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.specification.StageTable;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class AnnulusDiameterTable extends StageTable<AnnulusDiameterTable, AnnulusDiameter, Integer> {
	
	@Override
	public HttpDatabaseCrud<AnnulusDiameter, Integer> getConnector() {
		return AnnulusDiameterConnector.get();
	}

	@Override
	public List<Column<AnnulusDiameter, ? extends DatabasePersistent<?, ?>, ?, ?>> createColumns2() {
		List<Column<AnnulusDiameter, ? extends DatabasePersistent<?, ?>, ?, ?>> columns =
				new ArrayList<Column<AnnulusDiameter, ? extends DatabasePersistent<?, ?>, ?, ?>>();
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
