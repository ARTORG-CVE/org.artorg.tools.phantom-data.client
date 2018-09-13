package org.artorg.tools.phantomData.client.tables;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connectors.FileConnector;
import org.artorg.tools.phantomData.client.connectors.FileTypeConnector;
import org.artorg.tools.phantomData.client.scene.control.table.Column;
import org.artorg.tools.phantomData.client.scene.control.table.FilterTable;
import org.artorg.tools.phantomData.client.scene.control.table.IColumn;
import org.artorg.tools.phantomData.server.model.FileType;
import org.artorg.tools.phantomData.server.model.PhantomFile;

public class FileTable extends FilterTable<FileTable, PhantomFile, Integer> {

	{
		this.setConnector(FileConnector.get());
	}

	@Override
	public List<IColumn<PhantomFile, ?>> createColumns() {
		List<IColumn<PhantomFile, ?>> columns =
				new ArrayList<IColumn<PhantomFile, ?>>();
		columns.add(new Column<PhantomFile, PhantomFile, Integer>(
				"id", item -> item, 
				path -> String.valueOf(path.getId()), 
				(path,value) -> path.setId(Integer.valueOf(value)),
				FileConnector.get()));
		columns.add(new Column<PhantomFile, PhantomFile, Integer>(
				"path", item -> item, 
				path -> path.getPath(), 
				(path,value) -> path.setPath(value),
				FileConnector.get()));
		columns.add(new Column<PhantomFile, PhantomFile, Integer>(
				"name", item -> item, 
				path -> path.getName(), 
				(path,value) -> path.setName(value),
				FileConnector.get()));
		columns.add(new Column<PhantomFile, PhantomFile, Integer>(
				"extension", item -> item, 
				path -> path.getExtension(), 
				(path,value) -> path.setExtension(value),
				FileConnector.get()));
		columns.add(new Column<PhantomFile, FileType, Integer>(
				"file type", item -> item.getFileType(), 
				path -> path.getName(), 
				(path,value) -> path.setName(value),
				FileTypeConnector.get()));
		return columns;
	}

	@Override
	public String getTableName() {
		return "Files";
	}
	
	

}
