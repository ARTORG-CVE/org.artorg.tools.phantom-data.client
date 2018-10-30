package org.artorg.tools.phantomData.client.tablesFilter;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.client.table.IBaseColumns;
import org.artorg.tools.phantomData.client.table.IPropertyColumns;
import org.artorg.tools.phantomData.server.model.phantom.Phantomina;

public class PhantominaFilterTable extends DbUndoRedoFactoryEditFilterTable<Phantomina> implements IPropertyColumns, IBaseColumns {

	{
		setTableName("Phantominas");

		setColumnCreator(items -> {
			List<AbstractColumn<Phantomina>> columns =
				new ArrayList<AbstractColumn<Phantomina>>();
			FilterColumn<Phantomina> column;
			column = new FilterColumn<Phantomina>(
				"PID", item -> item,
				path -> path.getProductId(),
				(path, value) -> path.setProductId(value));
			column.setAscendingSortComparator((p1, p2) -> Phantomina.comparePid(p1.getProductId(), p2.getProductId()));
			columns.add(column);
			columns.add(new FilterColumn<Phantomina>(
				"annulus [mm]", item -> item.getAnnulusDiameter(),
				path -> String.valueOf(path.getValue()),
				(path, value) -> path.setValue(Double.valueOf(value))));
			columns.add(new FilterColumn<Phantomina>(
				"type", item -> item.getFabricationType(),
				path -> path.getValue(),
				(path, value) -> path.setValue(value)));
			columns.add(new FilterColumn<Phantomina>(
				"literature", item -> item.getLiteratureBase(),
				path -> path.getValue(),
				(path, value) -> path.setValue(value)));
			column = new FilterColumn<Phantomina>(
				"special", item -> item.getSpecial(),
				path -> path.getShortcut(),
				(path, value) -> path.setShortcut(value));
			columns.add(column);
//			createPropertyColumns(columns, this.getItems());
			createBaseColumns(columns);
			return columns;
		});

	}
	
}
