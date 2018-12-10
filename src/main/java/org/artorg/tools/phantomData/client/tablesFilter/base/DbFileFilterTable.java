package org.artorg.tools.phantomData.client.tablesFilter.base;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.columns.AbstractColumn;
import org.artorg.tools.phantomData.client.table.columns.Column;
import org.artorg.tools.phantomData.client.table.columns.FilterColumn;
import org.artorg.tools.phantomData.client.tables.IPersonifiedColumns;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.base.DbFile;

import javafx.scene.image.ImageView;

public class DbFileFilterTable extends DbUndoRedoFactoryEditFilterTable<DbFile>
	implements IPersonifiedColumns {

	{
		setTableName("Files");
		
		setColumnCreator(items -> {
			List<AbstractColumn<DbFile,?>> columns =
				new ArrayList<AbstractColumn<DbFile,?>>();
			columns.add(new Column<DbFile,ImageView>("", item -> item,
				path -> FxUtil.getFxFileIcon(path.getFile()), (path, value) -> {}));
			
			columns.add(new FilterColumn<DbFile,String>("Name", item -> item,
				path -> path.getName(), (path, value) -> path.setName(value)));
			columns.add(new FilterColumn<DbFile,String>("Extension", item -> item,
				path -> path.getExtension(), (path, value) -> path.setExtension(value)));
			columns.add(new FilterColumn<DbFile,String>("File Tags", item -> item,
				path -> path.getFileTags().stream().map(fileTag -> fileTag.getName()).collect(Collectors.joining(", ")), 
				(path, value) -> {}));
			createPersonifiedColumns(columns);
			return columns;
		});

	}

}
