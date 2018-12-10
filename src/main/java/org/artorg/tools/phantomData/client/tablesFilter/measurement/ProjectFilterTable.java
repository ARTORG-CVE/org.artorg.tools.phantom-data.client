package org.artorg.tools.phantomData.client.tablesFilter.measurement;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.FilterColumn;
import org.artorg.tools.phantomData.client.columns.IPersonifiedColumns;
import org.artorg.tools.phantomData.client.columns.IPropertyColumns;
import org.artorg.tools.phantomData.client.table.DbTable;
import org.artorg.tools.phantomData.server.model.measurement.Project;

public class ProjectFilterTable extends DbTable<Project> implements IPropertyColumns, IPersonifiedColumns {
	
	{
		setTableName("Projects");

		setColumnCreator(items -> {
			List<AbstractColumn<Project,?>> columns =
				new ArrayList<AbstractColumn<Project,?>>();
			columns.add(new FilterColumn<Project,String>(
				"Name", item -> item,
				path -> path.getName(),
				(path, value) -> path.setName(value)
				));
			columns.add(new FilterColumn<Project,String>(
				"Description", item -> item,
				path -> path.getDescription(),
				(path, value) -> path.setDescription(value)
				));
			columns.add(new FilterColumn<Project,String>(
				"Project", item -> item,
				path -> Short.toString(path.getStartYear()),
				(path, value) -> path.setStartYear(Short.valueOf(value))
				));
			columns.add(new FilterColumn<Project, String>(
				"Leader", item -> item.getLeader(),
				path -> path.toName(),
				(path, value) -> {}
				));
			columns.add(new FilterColumn<Project,String>(
				"Members", item -> item,
				path -> String.valueOf(path.getMembers().size()),
				(path, value) -> {}));
			columns.add(new FilterColumn<Project,String>(
				"Files", item -> item,
				path -> String.valueOf(path.getFiles().size()),
				(path, value) -> {}));
			columns.add(new FilterColumn<Project,String>(
				"Notes", item -> item,
				path -> String.valueOf(path.getNotes().size()),
				(path, value) -> {}));
			createPersonifiedColumns(columns);
			return columns;
		});

	}

}
