package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.artorg.tools.phantomData.client.connectors.SpecialConnector;
import org.artorg.tools.phantomData.client.scene.control.table.Column;
import org.artorg.tools.phantomData.client.scene.control.table.FilterTableSpringDb;
import org.artorg.tools.phantomData.client.scene.control.table.IColumn;
import org.artorg.tools.phantomData.client.scene.control.table.PropertyColumns;
import org.artorg.tools.phantomData.server.model.Special;

public class SpecialFilterTable extends FilterTableSpringDb<Special> implements PropertyColumns {

	{
		this.setConnector(SpecialConnector.get());
	}

	@Override
	public List<IColumn<Special>> createColumns() {
		List<IColumn<Special>> columns = new ArrayList<IColumn<Special>>();
		columns.add(new Column<Special, Special>("id", item -> item, path -> String.valueOf(path.getId()),
				(path, value) -> path.setId(UUID.fromString((String) value)), SpecialConnector.get()));
		columns.add(new Column<Special, Special>("shortcut", item -> item, path -> path.getShortcut(),
				(path, value) -> path.setShortcut(value), SpecialConnector.get()));

		createPropertyColumns(columns, this.getItems(), item -> item.getPropertyContainer());
		
		return columns;
		
	}

	@Override
	public String getTableName() {
		return "Specials";
	}

}
