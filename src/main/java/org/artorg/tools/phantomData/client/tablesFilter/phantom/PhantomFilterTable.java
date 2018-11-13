package org.artorg.tools.phantomData.client.tablesFilter.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.client.table.IBaseColumns;
import org.artorg.tools.phantomData.client.table.IPersonifiedColumns;
import org.artorg.tools.phantomData.client.table.IPropertyColumns;
import org.artorg.tools.phantomData.server.model.phantom.Phantom;
import org.artorg.tools.phantomData.server.model.phantom.Phantomina;

public class PhantomFilterTable extends DbUndoRedoFactoryEditFilterTable<Phantom> implements IPropertyColumns, IBaseColumns {

	{
		setTableName("Phantoms");

		setColumnCreator(items -> {
			List<AbstractColumn<Phantom,?>> columns =
				new ArrayList<AbstractColumn<Phantom,?>>();
			FilterColumn<Phantom,?> column;
			column = new FilterColumn<Phantom,String>(
				"PID", item -> item,
				path -> path.getProductId(),
				(path, value) -> path.setProductId(value));
			column.setAscendingSortComparator((p1, p2) -> Phantomina.comparePid(p1.getProductId(), p2.getProductId()));
			columns.add(column);
			columns.add(new FilterColumn<Phantom,String>(
				"Annulus [mm]", item -> item.getPhantomina().getAnnulusDiameter(),
				path -> String.valueOf(path.getValue()),
				(path, value) -> path.setValue(Double.valueOf(value))));
			columns.add(new FilterColumn<Phantom,String>(
				"Type", item -> item.getPhantomina().getFabricationType(),
				path -> path.getValue(),
				(path, value) -> path.setValue(value)));
			columns.add(new FilterColumn<Phantom,String>(
				"Literature", item -> item.getPhantomina().getLiteratureBase(),
				path -> path.getValue(),
				(path, value) -> path.setValue(value)));
			column = new FilterColumn<Phantom,String>(
				"Special", item -> item.getPhantomina().getSpecial(),
				path -> path.getShortcut(),
				(path, value) -> path.setShortcut(value));
			columns.add(column);
			column = new FilterColumn<Phantom,String>(
				"Number", item -> item,
				path -> String.valueOf(path.getNumber()),
				(path, value) -> path.setNumber(Integer.valueOf(value)));
			columns.add(column);
			column.setAscendingSortComparator((p1, p2) -> ((Integer) p1.getNumber())
				.compareTo((Integer) p2.getNumber()));
//			createPropertyColumns(columns, this.getItems());
			createPersonifiedColumns(columns);
			createFilesColumns(columns);
			return columns;
		});

	}

}
