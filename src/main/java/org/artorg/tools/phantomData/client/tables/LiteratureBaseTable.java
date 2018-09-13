package org.artorg.tools.phantomData.client.tables;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connectors.LiteratureBaseConnector;
import org.artorg.tools.phantomData.client.scene.control.table.Column;
import org.artorg.tools.phantomData.client.scene.control.table.FilterTable;
import org.artorg.tools.phantomData.client.scene.control.table.IColumn;
import org.artorg.tools.phantomData.server.model.LiteratureBase;

public class LiteratureBaseTable extends FilterTable<LiteratureBaseTable, LiteratureBase, Integer> {

	{
		this.setConnector(LiteratureBaseConnector.get());
	}

	@Override
	public List<IColumn<LiteratureBase, ?>> createColumns() {
		List<IColumn<LiteratureBase, ?>> columns =
				new ArrayList<IColumn<LiteratureBase, ?>>();
		columns.add(new Column<LiteratureBase, LiteratureBase, Integer>(
				"id", item -> item, 
				path -> String.valueOf(path.getId()), 
				(path,value) -> path.setId(Integer.valueOf(value)),
				LiteratureBaseConnector.get()));
		columns.add(new Column<LiteratureBase, LiteratureBase, Integer>(
				"shortcut", item -> item, 
				path -> path.getShortcut(), 
				(path,value) -> path.setShortcut((String) value),
				LiteratureBaseConnector.get()));
		columns.add(new Column<LiteratureBase, LiteratureBase, Integer>(
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
