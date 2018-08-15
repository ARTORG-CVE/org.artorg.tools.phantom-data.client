package org.artorg.tools.phantomData.client.tables;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connectors.FileConnector;
import org.artorg.tools.phantomData.client.connectors.FileTypeConnector;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.IColumn;
import org.artorg.tools.phantomData.client.table.StageTable;
import org.artorg.tools.phantomData.server.model.FileType;
import org.artorg.tools.phantomData.server.model.PhantomFile;

public class FileTable extends StageTable<FileTable, PhantomFile, Integer> {

	{
		this.setConnector(FileConnector.get());
	}

	@Override
	public List<IColumn<PhantomFile, ?, ?>> createColumns() {
		List<IColumn<PhantomFile, ?, ?>> columns =
				new ArrayList<IColumn<PhantomFile, ?, ?>>();
		columns.add(new Column<PhantomFile, PhantomFile, Integer, Integer>(
				"id", item -> item, 
				path -> path.getId(), 
				(path,value) -> path.setId((Integer) value),
				FileConnector.get()));
		columns.add(new Column<PhantomFile, PhantomFile, String, Integer>(
				"path", item -> item, 
				path -> path.getPath(), 
				(path,value) -> path.setPath((String) value),
				FileConnector.get()));
		columns.add(new Column<PhantomFile, PhantomFile, String, Integer>(
				"name", item -> item, 
				path -> path.getName(), 
				(path,value) -> path.setName((String) value),
				FileConnector.get()));
		columns.add(new Column<PhantomFile, PhantomFile, String, Integer>(
				"extension", item -> item, 
				path -> path.getExtension(), 
				(path,value) -> path.setExtension((String) value),
				FileConnector.get()));
		columns.add(new Column<PhantomFile, FileType, String, Integer>(
				"file type", item -> item.getFileType(), 
				path -> path.getName(), 
				(path,value) -> path.setName((String) value),
				FileTypeConnector.get()));
		return columns;
	}

}
