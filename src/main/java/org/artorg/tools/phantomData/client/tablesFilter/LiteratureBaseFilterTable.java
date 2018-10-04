package org.artorg.tools.phantomData.client.tablesFilter;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.server.model.LiteratureBase;

public class LiteratureBaseFilterTable extends DbUndoRedoEditFilterTable<LiteratureBase> {

	{
		List<AbstractColumn<LiteratureBase>> columns =
				new ArrayList<AbstractColumn<LiteratureBase>>();
		columns.add(new FilterColumn<LiteratureBase>(
				"shortcut", item -> item, 
				path -> path.getShortcut(), 
				(path,value) -> path.setShortcut((String) value)));
		columns.add(new FilterColumn<LiteratureBase>(
				"value", item -> item, 
				path -> path.getValue(), 
				(path,value) -> path.setValue((String) value)));
		setColumns(columns);
		
		setTableName("Literature Bases");
	}

}