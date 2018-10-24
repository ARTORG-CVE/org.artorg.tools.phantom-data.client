package org.artorg.tools.phantomData.client.tablesFilter;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.server.model.AcademicTitle;

public class AcademicTitleFilterTable
	extends DbUndoRedoFactoryEditFilterTable<AcademicTitle> {

	{
		setTableName("Academic Titles");

		setColumnCreator(items -> {
			List<AbstractColumn<AcademicTitle>> columns =
				new ArrayList<AbstractColumn<AcademicTitle>>();
			columns.add(new FilterColumn<AcademicTitle>(
				"prefix", item -> item,
				path -> path.getPrefix(),
				(path, value) -> path.setPrefix((String) value)));
			columns.add(new FilterColumn<AcademicTitle>(
				"descirption", item -> item,
				path -> path.getDescription(),
				(path, value) -> path.setDescription((String) value)));
			return columns;
		});

	}

}
