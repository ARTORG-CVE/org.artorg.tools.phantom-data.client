package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connectors.SpecialConnector;
import org.artorg.tools.phantomData.client.scene.control.table.Column;
import org.artorg.tools.phantomData.client.scene.control.table.FilterTableSpringDb;
import org.artorg.tools.phantomData.client.scene.control.table.IColumn;
import org.artorg.tools.phantomData.client.scene.control.table.PropertyColumns;
import org.artorg.tools.phantomData.server.model.Special;

public class SpecialFilterTable extends FilterTableSpringDb<Special> implements PropertyColumns {

	public SpecialFilterTable() {
		super(Special.class);
	}

	@Override
	public List<IColumn<Special>> createColumns() {
		List<IColumn<Special>> columns = new ArrayList<IColumn<Special>>();
		columns.add(new Column<Special, Special>("shortcut", item -> item, path -> path.getShortcut(),
				(path, value) -> path.setShortcut(value), SpecialConnector.get()));

		createPropertyColumns(columns, this.getItems());
		
		return columns;
		
	}

	@Override
	public String getTableName() {
		return "Specials";
	}

}
