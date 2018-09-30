package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoEditFilterTable;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.IColumn;
import org.artorg.tools.phantomData.server.model.LiteratureBase;

public class LiteratureBaseFilterTable extends DbUndoRedoEditFilterTable<LiteratureBase> {

	{
		setItemClass(LiteratureBase.class);
		
		List<IColumn<LiteratureBase>> columns =
				new ArrayList<IColumn<LiteratureBase>>();
		columns.add(new Column<LiteratureBase, LiteratureBase>(
				"shortcut", item -> item, 
				path -> path.getShortcut(), 
				(path,value) -> path.setShortcut((String) value)));
		columns.add(new Column<LiteratureBase, LiteratureBase>(
				"value", item -> item, 
				path -> path.getValue(), 
				(path,value) -> path.setValue((String) value)));
		setColumns(columns);
		
		setTableName("Literature Bases");
	}

}
