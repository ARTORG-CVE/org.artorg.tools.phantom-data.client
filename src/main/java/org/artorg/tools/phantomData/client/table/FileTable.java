package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connector.FileConnector;
import org.artorg.tools.phantomData.client.specification.Column;
import org.artorg.tools.phantomData.client.specification.Column2;
import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.specification.StageTable;
import org.artorg.tools.phantomData.server.model.FileType;
import org.artorg.tools.phantomData.server.model.PhantomFile;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class FileTable extends StageTable<FileTable, PhantomFile, Integer> {

	@Override
	public HttpDatabaseCrud<PhantomFile, Integer> getConnector() {
		return FileConnector.get();
	}

	@Override
	public List<Column<PhantomFile, ? extends DatabasePersistent<?, ?>, ?, ?>> createColumns2() {
		List<Column<PhantomFile, ? extends DatabasePersistent<?, ?>, ?, ?>> columns =
				new ArrayList<Column<PhantomFile, ? extends DatabasePersistent<?, ?>, ?, ?>>();
		columns.add(new Column<PhantomFile, PhantomFile, Integer, Integer>(
				"id", item -> item, 
				path -> path.getId(), 
				(path,value) -> path.setId(value)));
		columns.add(new Column<PhantomFile, PhantomFile, String, Integer>(
				"path", item -> item, 
				path -> path.getPath(), 
				(path,value) -> path.setPath(value)));
		columns.add(new Column<PhantomFile, PhantomFile, String, Integer>(
				"name", item -> item, 
				path -> path.getName(), 
				(path,value) -> path.setName(value)));
		columns.add(new Column<PhantomFile, PhantomFile, String, Integer>(
				"extension", item -> item, 
				path -> path.getExtension(), 
				(path,value) -> path.setExtension(value)));
		columns.add(new Column<PhantomFile, FileType, String, Integer>(
				"file type", item -> item.getFileType(), 
				path -> path.getName(), 
				(path,value) -> path.setName(value)));
		return columns;
	}

}
