package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connectors.AnnulusDiameterConnector;
import org.artorg.tools.phantomData.client.scene.control.table.Column;
import org.artorg.tools.phantomData.client.scene.control.table.FilterTableSpringDb;
import org.artorg.tools.phantomData.client.scene.control.table.IColumn;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;

public class AnnulusDiameterFilterTable extends FilterTableSpringDb<AnnulusDiameter, Integer> {
	
	{
		this.setConnector(AnnulusDiameterConnector.get());
	}
	
	@Override
	public List<IColumn<AnnulusDiameter, ?>> createColumns() {
		List<IColumn<AnnulusDiameter, ?>> columns =
				new ArrayList<IColumn<AnnulusDiameter, ?>>();
		columns.add(new Column<AnnulusDiameter, AnnulusDiameter, Integer>(
				"id", item -> item, 
				path -> String.valueOf(path.getId()), 
				(path,value) -> path.setId(Integer.valueOf(value)),
				AnnulusDiameterConnector.get()));
		columns.add(new Column<AnnulusDiameter, AnnulusDiameter, Integer>(
				"shortcut", item -> item, 
				path -> String.valueOf(path.getShortcut()), 
				(path,value) -> path.setShortcut(Integer.valueOf(value)),
				AnnulusDiameterConnector.get()));
		columns.add(new Column<AnnulusDiameter, AnnulusDiameter, Integer>(
				"value", item -> item, 
				path -> String.valueOf(path.getValue()), 
				(path,value) -> path.setValue(Double.valueOf(value)),
				AnnulusDiameterConnector.get()));
		return columns;
	}

	@Override
	public String getTableName() {
		return "Annulus Diameters";
	}

}
