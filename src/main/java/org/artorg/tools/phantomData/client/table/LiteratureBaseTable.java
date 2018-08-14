package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.artorg.tools.phantomData.client.commandPattern.PropertyUndoable;
import org.artorg.tools.phantomData.client.connector.LiteratureBaseConnector;
import org.artorg.tools.phantomData.client.specification.Column;
import org.artorg.tools.phantomData.client.specification.Column2;
import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.specification.StageTable;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.LiteratureBase;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class LiteratureBaseTable extends StageTable<LiteratureBaseTable, LiteratureBase, Integer> {
	
	@Override
	public HttpDatabaseCrud<LiteratureBase, Integer> getConnector() {
		return LiteratureBaseConnector.get();
	}

	@Override
	public List<Column<LiteratureBase, ? extends DatabasePersistent<?, ?>, ?, ?>> createColumns2() {
		List<Column<LiteratureBase, ? extends DatabasePersistent<?, ?>, ?, ?>> columns =
				new ArrayList<Column<LiteratureBase, ? extends DatabasePersistent<?, ?>, ?, ?>>();
		columns.add(new Column<LiteratureBase, LiteratureBase, Integer, Integer>(
				"id", item -> item, 
				path -> path.getId(), 
				(path,value) -> path.setId(value)));
		columns.add(new Column<LiteratureBase, LiteratureBase, String, Integer>(
				"shortcut", item -> item, 
				path -> path.getShortcut(), 
				(path,value) -> path.setShortcut(value)));
		columns.add(new Column<LiteratureBase, LiteratureBase, String, Integer>(
				"value", item -> item, 
				path -> path.getValue(), 
				(path,value) -> path.setValue(value)));
		return columns;
	}

}
