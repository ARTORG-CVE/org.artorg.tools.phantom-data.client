package org.artorg.tools.phantomData.client.tablesFilter.base.person;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.FilterColumn;
import org.artorg.tools.phantomData.client.table.DbTable;
import org.artorg.tools.phantomData.server.model.base.person.AcademicTitle;

public class AcademicTitleFilterTable
	extends DbTable<AcademicTitle> {

	{
		setTableName("Academic Titles");

		setColumnCreator(items -> {
			List<AbstractColumn<AcademicTitle,?>> columns =
				new ArrayList<AbstractColumn<AcademicTitle,?>>();
			columns.add(new FilterColumn<AcademicTitle,String>(
				"Prefix", item -> item,
				path -> path.getPrefix(),
				(path, value) -> path.setPrefix((String) value)));
			columns.add(new FilterColumn<AcademicTitle,String>(
				"Descirption", item -> item,
				path -> path.getDescription(),
				(path, value) -> path.setDescription((String) value)));
			return columns;
		});

	}

}
