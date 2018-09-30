package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoEditFilterTable;
import org.artorg.tools.phantomData.client.table.LambdaColumn;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.server.model.FileType;
import org.artorg.tools.phantomData.server.model.PhantomFile;

public class FileFilterTable extends DbUndoRedoEditFilterTable<PhantomFile> {

	{
		setItemClass(PhantomFile.class);
		
		List<Column<PhantomFile>> columns =
				new ArrayList<Column<PhantomFile>>();
		columns.add(new LambdaColumn<PhantomFile, PhantomFile>(
				"path", item -> item, 
				path -> path.getPath(), 
				(path,value) -> path.setPath(value)));
		columns.add(new LambdaColumn<PhantomFile, PhantomFile>(
				"name", item -> item, 
				path -> path.getName(), 
				(path,value) -> path.setName(value)));
		columns.add(new LambdaColumn<PhantomFile, PhantomFile>(
				"extension", item -> item, 
				path -> path.getExtension(), 
				(path,value) -> path.setExtension(value)));
		columns.add(new LambdaColumn<PhantomFile, FileType>(
				"file type", item -> item.getFileType(), 
				path -> path.getName(), 
				(path,value) -> path.setName(value)));
		this.setColumns(columns);
		
		this.setTableName("Files");
	}

}
