package org.artorg.tools.phantomData.client.tablesFilter.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.columns.AbstractColumn;
import org.artorg.tools.phantomData.client.table.columns.FilterColumn;
import org.artorg.tools.phantomData.client.tables.IPersonifiedColumns;
import org.artorg.tools.phantomData.client.tables.IPropertyColumns;
import org.artorg.tools.phantomData.server.model.phantom.Phantomina;

public class PhantominaFilterTable extends DbUndoRedoFactoryEditFilterTable<Phantomina> implements IPropertyColumns, IPersonifiedColumns {

	{
		setTableName("Phantominas");

		setColumnCreator(items -> {
			List<AbstractColumn<Phantomina,?>> columns =
				new ArrayList<AbstractColumn<Phantomina,?>>();
			FilterColumn<Phantomina,?> column;
			column = new FilterColumn<Phantomina,String>(
				"PID", item -> item,
				path -> path.getProductId(),
				(path, value) -> path.setProductId(value));
			column.setAscendingSortComparator((p1, p2) -> Phantomina.comparePid(p1.getProductId(), p2.getProductId()));
			columns.add(column);
			columns.add(new FilterColumn<Phantomina,String>(
				"Annulus [mm]", item -> item.getAnnulusDiameter(),
				path -> String.valueOf(path.getValue()),
				(path, value) -> path.setValue(Double.valueOf(value))));
			columns.add(new FilterColumn<Phantomina,String>(
				"Type", item -> item.getFabricationType(),
				path -> path.getValue(),
				(path, value) -> path.setValue(value)));
			columns.add(new FilterColumn<Phantomina,String>(
				"Literature", item -> item.getLiteratureBase(),
				path -> path.getValue(),
				(path, value) -> path.setValue(value)));
			column = new FilterColumn<Phantomina,String>(
				"Special", item -> item.getSpecial(),
				path -> path.getShortcut(),
				(path, value) -> path.setShortcut(value));
			columns.add(column);
//			createPropertyColumns(columns, this.getItems());
			createPersonifiedColumns(columns);
			return columns;
		});

	}
	
}
