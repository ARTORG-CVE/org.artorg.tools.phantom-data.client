package org.artorg.tools.phantomData.client.tables;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connectors.FileTypeConnector;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.FilterTable;
import org.artorg.tools.phantomData.client.table.IColumn;
import org.artorg.tools.phantomData.server.model.FileType;

public class FileTypeTable extends FilterTable<FileTypeTable, FileType, Integer> {

	{
		this.setConnector(FileTypeConnector.get());
	}

	@Override
	public List<IColumn<FileType, ?>> createColumns() {
		List<IColumn<FileType, ?>> columns =
				new ArrayList<IColumn<FileType, ?>>();
		columns.add(new Column<FileType, FileType, Integer>(
				"id", item -> item, 
				path -> String.valueOf(path.getId()), 
				(path,value) -> path.setId(Integer.valueOf(value)),
				FileTypeConnector.get()));
		columns.add(new Column<FileType, FileType, Integer>(
				"name", item -> item, 
				path -> path.getName(), 
				(path,value) -> path.setName((String) value),
				FileTypeConnector.get()));
		return columns;
	}
}
