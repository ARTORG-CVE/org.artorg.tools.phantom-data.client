package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.control.FilterTableSpringDbEditable;
import org.artorg.tools.phantomData.client.scene.control.FilterTableSpringDb2;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.IColumn;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.FabricationType;
import org.artorg.tools.phantomData.server.model.LiteratureBase;
import org.artorg.tools.phantomData.server.model.Phantom;
import org.artorg.tools.phantomData.server.model.Special;

public class PhantomFilterTable2 extends FilterTableSpringDb2<Phantom> {

	{
		setItemClass(Phantom.class);
	}
	
	@Override
	public List<IColumn<Phantom>> createColumns() {
		List<IColumn<Phantom>> columns =
				new ArrayList<IColumn<Phantom>>();
		IColumn<Phantom> column;
		columns.add(new Column<Phantom, Phantom>(
				"PID", item -> item, 
				path -> path.getProductId(), 
				(path,value) -> path.setProductId(value)));
		columns.add(new Column<Phantom, AnnulusDiameter>(
				"annulus [mm]", item -> item.getAnnulusDiameter(), 
				path -> String.valueOf(path.getValue()), 
				(path,value) -> path.setValue(Double.valueOf(value))));
		columns.add(new Column<Phantom, FabricationType>(
				"type", item -> item.getFabricationType(), 
				path -> path.getValue(), 
				(path,value) -> path.setValue(value)));
		columns.add(new Column<Phantom, LiteratureBase>(
				"literature", item -> item.getLiteratureBase(), 
				path -> path.getValue(), 
				(path,value) -> path.setValue(value)));
		column = new Column<Phantom, Special>(
				"special", item -> item.getSpecial(), 
				path -> path.getShortcut(), 
				(path,value) -> path.setShortcut(value));
		columns.add(column);
		columns.add(new Column<Phantom, Phantom>(
				"number", item -> item, 
				path -> String.valueOf(path.getNumber()), 
				(path,value) -> path.setNumber(Integer.valueOf(value))));
		return columns;
	}

	@Override
	public String getTableName() {
		return "Phantoms";
	}

}
