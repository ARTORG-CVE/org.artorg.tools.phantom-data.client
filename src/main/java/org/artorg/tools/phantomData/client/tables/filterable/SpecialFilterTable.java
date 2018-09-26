package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.control.FilterTableSpringDb;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.IColumn;
import org.artorg.tools.phantomData.client.table.PropertyColumns;
import org.artorg.tools.phantomData.server.model.Special;

public class SpecialFilterTable extends FilterTableSpringDb<Special> implements PropertyColumns {

	{
		setItemClass(Special.class);
	}

	@Override
	public List<IColumn<Special>> createColumns() {
		List<IColumn<Special>> columns = new ArrayList<IColumn<Special>>();
		columns.add(new Column<Special, Special>("shortcut", item -> item, path -> path.getShortcut(),
				(path, value) -> path.setShortcut(value)));

		createPropertyColumns(columns, this.getItems());
		
		return columns;
		
	}

	@Override
	public String getTableName() {
		return "Specials";
	}

}
