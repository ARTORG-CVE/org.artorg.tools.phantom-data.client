package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connectors.FileTypeConnector;
import org.artorg.tools.phantomData.client.scene.control.table.Column;
import org.artorg.tools.phantomData.client.scene.control.table.FilterTableSpringDb;
import org.artorg.tools.phantomData.client.scene.control.table.IColumn;
import org.artorg.tools.phantomData.server.model.FileType;

public class FileTypeFilterTable extends FilterTableSpringDb<FileType> {

	public FileTypeFilterTable() {
		super(FileType.class);
	}

	@Override
	public List<IColumn<FileType>> createColumns() {
		List<IColumn<FileType>> columns =
				new ArrayList<IColumn<FileType>>();
		columns.add(new Column<FileType, FileType>(
				"name", item -> item, 
				path -> path.getName(), 
				(path,value) -> path.setName((String) value),
				FileTypeConnector.get()));
		return columns;
	}

	@Override
	public String getTableName() {
		return "File Types";
	}
}
