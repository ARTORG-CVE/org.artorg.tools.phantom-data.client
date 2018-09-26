package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.control.FilterTableSpringDb;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.IColumn;
import org.artorg.tools.phantomData.server.model.LiteratureBase;

public class LiteratureBaseFilterTable extends FilterTableSpringDb<LiteratureBase> {

	{
		setItemClass(LiteratureBase.class);
	}
	
	@Override
	public List<IColumn<LiteratureBase>> createColumns() {
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
		return columns;
	}

	@Override
	public String getTableName() {
		return "Literature Bases";
	}

}
