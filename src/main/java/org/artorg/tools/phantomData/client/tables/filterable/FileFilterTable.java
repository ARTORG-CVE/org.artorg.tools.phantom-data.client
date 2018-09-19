package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connectors.FileConnector;
import org.artorg.tools.phantomData.client.connectors.FileTypeConnector;
import org.artorg.tools.phantomData.client.scene.control.table.Column;
import org.artorg.tools.phantomData.client.scene.control.table.FilterTableSpringDb;
import org.artorg.tools.phantomData.client.scene.control.table.IColumn;
import org.artorg.tools.phantomData.server.model.FileType;
import org.artorg.tools.phantomData.server.model.PhantomFile;

public class FileFilterTable extends FilterTableSpringDb<PhantomFile> {

	{
		this.setConnector(FileConnector.get());
	}

	@Override
	public List<IColumn<PhantomFile>> createColumns() {
		List<IColumn<PhantomFile>> columns =
				new ArrayList<IColumn<PhantomFile>>();
		columns.add(new Column<PhantomFile, PhantomFile>(
				"id", item -> item, 
				path -> String.valueOf(path.getId()), 
				(path,value) -> path.setId(value),
				FileConnector.get()));
		columns.add(new Column<PhantomFile, PhantomFile>(
				"path", item -> item, 
				path -> path.getPath(), 
				(path,value) -> path.setPath(value),
				FileConnector.get()));
		columns.add(new Column<PhantomFile, PhantomFile>(
				"name", item -> item, 
				path -> path.getName(), 
				(path,value) -> path.setName(value),
				FileConnector.get()));
		columns.add(new Column<PhantomFile, PhantomFile>(
				"extension", item -> item, 
				path -> path.getExtension(), 
				(path,value) -> path.setExtension(value),
				FileConnector.get()));
		columns.add(new Column<PhantomFile, FileType>(
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
