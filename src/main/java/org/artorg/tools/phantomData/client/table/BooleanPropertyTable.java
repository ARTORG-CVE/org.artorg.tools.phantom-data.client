package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.commandPattern.PropertyUndoable;
import org.artorg.tools.phantomData.client.connector.property.BooleanPropertyConnector;
import org.artorg.tools.phantomData.client.specification.Column;
import org.artorg.tools.phantomData.client.specification.Column2;
import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.specification.StageTable;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;
import org.artorg.tools.phantomData.server.model.property.PropertyField;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class BooleanPropertyTable extends StageTable<BooleanPropertyTable, BooleanProperty, Integer> {

	@Override
	public HttpDatabaseCrud<BooleanProperty, Integer> getConnector() {
		return BooleanPropertyConnector.get();
	}

	@Override
	public List<Column<BooleanProperty, ? extends DatabasePersistent<?, ?>, ?, ?>> createColumns2() {
		List<Column<BooleanProperty, ? extends DatabasePersistent<?, ?>, ?, ?>> columns =
				new ArrayList<Column<BooleanProperty, ? extends DatabasePersistent<?, ?>, ?, ?>>();
		columns.add(new Column<BooleanProperty, BooleanProperty, Integer, Integer>(
				"id", item -> item, 
				path -> path.getId(), 
				(path,value) -> path.setId(value)));
		columns.add(new Column<BooleanProperty, PropertyField, String, Integer>(
				"property field", item -> item.getPropertyField(), 
				path -> path.getDescription(), 
				(path,value) -> path.setDescription(value)));
		columns.add(new Column<BooleanProperty, BooleanProperty, Boolean, Integer>(
				"value", item -> item, 
				path -> path.getBool(), 
				(path,value) -> path.setBool(value)));
		return columns;
	}

}
