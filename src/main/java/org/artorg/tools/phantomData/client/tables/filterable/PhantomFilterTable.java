package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoEditFilterTable;
import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.server.model.Phantom;

public class PhantomFilterTable extends DbUndoRedoEditFilterTable<Phantom> {

	{
		setItemClass(Phantom.class);
		
		List<AbstractColumn<Phantom>> columns =
				new ArrayList<AbstractColumn<Phantom>>();
		FilterColumn<Phantom> column;
		column = new FilterColumn<Phantom>(
				"PID", item -> item, 
				path -> path.getProductId(), 
				(path,value) -> path.setProductId(value));
		column.setAscendingSortComparator((p1,p2) -> comparePid(p1,p2));
		columns.add(column);
		columns.add(new FilterColumn<Phantom>(
				"annulus [mm]", item -> item.getAnnulusDiameter(), 
				path -> String.valueOf(path.getValue()), 
				(path,value) -> path.setValue(Double.valueOf(value))));
		columns.add(new FilterColumn<Phantom>(
				"type", item -> item.getFabricationType(), 
				path -> path.getValue(), 
				(path,value) -> path.setValue(value)));
		columns.add(new FilterColumn<Phantom>(
				"literature", item -> item.getLiteratureBase(), 
				path -> path.getValue(), 
				(path,value) -> path.setValue(value)));
		column = new FilterColumn<Phantom>(
				"special", item -> item.getSpecial(), 
				path -> path.getShortcut(), 
				(path,value) -> path.setShortcut(value));
		columns.add(column);
		column = new FilterColumn<Phantom>(
				"number", item -> item, 
				path -> String.valueOf(path.getNumber()), 
				(path,value) -> path.setNumber(Integer.valueOf(value)));
				columns.add(column);
		column.setAscendingSortComparator((p1,p2) -> ((Integer)p1.getNumber()).compareTo((Integer)p2.getNumber()));
		this.setColumns(columns);
		
		this.setTableName("Phantoms");
		
	}
	
	private int comparePid(Phantom phantom1, Phantom phantom2) {
		String[] splits1 = phantom1.getProductId().split("-");
		String[] splits2 = phantom2.getProductId().split("-");
		int n = Math.min(splits1.length, splits2.length);
		int result;
		for (int i=0; i<n-1; i++) {
			result = splits1[i].compareTo(splits2[i]); 
			if (result != 0)
				return result;
		}
		return ((Integer)phantom1.getNumber()).compareTo(((Integer)phantom2.getNumber()));
	}

}
