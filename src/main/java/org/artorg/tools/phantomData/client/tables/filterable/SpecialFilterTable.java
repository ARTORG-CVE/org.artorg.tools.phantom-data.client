package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoEditFilterTable;
import org.artorg.tools.phantomData.client.table.LambdaColumn;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.IPropertyColumns;
import org.artorg.tools.phantomData.server.model.Special;

public class SpecialFilterTable extends DbUndoRedoEditFilterTable<Special> implements IPropertyColumns {

	{
		setItemClass(Special.class);
		
		List<Column<Special>> columns = new ArrayList<Column<Special>>();
		columns.add(new LambdaColumn<Special, Special>("shortcut", item -> item, path -> path.getShortcut(),
				(path, value) -> path.setShortcut(value)));
		createPropertyColumns(columns, this.getItems());
		this.setColumns(columns);
		
		this.setTableName("Specials");
	}

}
