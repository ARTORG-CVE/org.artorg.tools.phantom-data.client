package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.control.FilterTableSpringDbEditable;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.IColumn;
import org.artorg.tools.phantomData.server.model.FileType;

public class FileTypeFilterTable extends FilterTableSpringDbEditable<FileType> {

	{
		setItemClass(FileType.class);
	}

	@Override
	public List<IColumn<FileType>> createColumns() {
		List<IColumn<FileType>> columns =
				new ArrayList<IColumn<FileType>>();
		columns.add(new Column<FileType, FileType>(
				"name", item -> item, 
				path -> path.getName(), 
				(path,value) -> path.setName((String) value)));
		return columns;
	}

	@Override
	public String getTableName() {
		return "File Types";
	}
}
