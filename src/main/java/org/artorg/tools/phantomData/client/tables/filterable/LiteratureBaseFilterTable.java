package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connectors.LiteratureBaseConnector;
import org.artorg.tools.phantomData.client.scene.control.table.Column;
import org.artorg.tools.phantomData.client.scene.control.table.FilterTableSpringDb;
import org.artorg.tools.phantomData.client.scene.control.table.IColumn;
import org.artorg.tools.phantomData.server.model.LiteratureBase;

public class LiteratureBaseFilterTable extends FilterTableSpringDb<LiteratureBase> {

	{
		this.setConnector(LiteratureBaseConnector.get());
	}

	@Override
	public List<IColumn<LiteratureBase>> createColumns() {
		List<IColumn<LiteratureBase>> columns =
				new ArrayList<IColumn<LiteratureBase>>();
//		columns.add(new Column<LiteratureBase, LiteratureBase>(
//				"id", item -> item, 
//				path -> String.valueOf(path.getId()), 
//				(path,value) -> path.setId(value),
//				LiteratureBaseConnector.get()));
		columns.add(new Column<LiteratureBase, LiteratureBase>(
				"shortcut", item -> item, 
				path -> path.getShortcut(), 
				(path,value) -> path.setShortcut((String) value),
				LiteratureBaseConnector.get()));
		columns.add(new Column<LiteratureBase, LiteratureBase>(
				"value", item -> item, 
				path -> path.getValue(), 
				(path,value) -> path.setValue((String) value),
				LiteratureBaseConnector.get()));
		return columns;
	}

	@Override
	public String getTableName() {
		return "Literature Bases";
	}

}
