package org.artorg.tools.phantomData.client.table;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.table.column.AbstractColumn;
import org.artorg.tools.phantomData.client.table.column.Column;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class DbTable<ITEM extends DbPersistent<ITEM, ?>>
	extends TableBase<ITEM> implements IDbTable<ITEM,Object> {
	private ICrudConnector<ITEM> connector;

	{
		connector = Connectors
			.getConnector(getItemClass());
	}

	@Override
	public ICrudConnector<ITEM> getConnector() {
		return this.connector;
	}

	@Override
	public void updateColumns() {
		getColumns().forEach(column -> column.setIdColumn(false));
		AbstractColumn<ITEM, ? extends Object> idColumn =
			(AbstractColumn<ITEM, ? extends Object>) new Column<ITEM, String>("ID",
				item -> item, path -> path.getId().toString(),
				(path, value) -> path.setId(value));
		idColumn.setIdColumn(true);
		idColumn.setVisibility(false);
		getColumns().add(0, idColumn);
		super.updateColumns();
	}

}
