package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connectors.SpecialConnector;
import org.artorg.tools.phantomData.client.scene.control.table.Column;
import org.artorg.tools.phantomData.client.scene.control.table.FilterTableSpringDb;
import org.artorg.tools.phantomData.client.scene.control.table.IColumn;
import org.artorg.tools.phantomData.client.scene.control.table.PropertyColumns;
import org.artorg.tools.phantomData.server.model.Special;

public class SpecialFilterTable extends FilterTableSpringDb<Special, Integer> implements PropertyColumns {

	{
		this.setConnector(SpecialConnector.get());
	}

	@Override
	public List<IColumn<Special, ?>> createColumns() {
		List<IColumn<Special, ?>> columns = new ArrayList<IColumn<Special, ?>>();
		columns.add(new Column<Special, Special, Integer>("id", item -> item, path -> String.valueOf(path.getId()),
				(path, value) -> path.setId(Integer.valueOf((String) value)), SpecialConnector.get()));
		columns.add(new Column<Special, Special, Integer>("shortcut", item -> item, path -> path.getShortcut(),
				(path, value) -> path.setShortcut(value), SpecialConnector.get()));

		createPropertyColumns(columns, this.getItems(), item -> item.getPropertyContainer());
		
		return columns;
		
	}

	@Override
	public String getTableName() {
		return "Specials";
	}

}
