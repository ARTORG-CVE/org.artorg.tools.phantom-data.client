package org.artorg.tools.phantomData.client.table;

import java.util.List;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.connector.PersonalizedHttpConnectorSpring;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

@SuppressWarnings("unchecked")
public class DbTable<ITEM extends DbPersistent<ITEM,?>> extends TableBase<ITEM> implements IDbTable<ITEM> {
	private ICrudConnector<ITEM,?> connector;
	
	{
		connector = (ICrudConnector<ITEM, ?>) PersonalizedHttpConnectorSpring.getOrCreate(getItemClass());
	}

	@Override
	public ICrudConnector<ITEM,?> getConnector() {
		return this.connector;
	}
	
	@Override
	public void setColumns(List<AbstractColumn<ITEM>> columns) {
		columns.forEach(column -> column.setIdColumn(false));
		AbstractColumn<ITEM> idColumn = new Column<ITEM>(
			"ID", item -> item, 
			path -> path.getId().toString(), 
			(path,value) -> path.setId(value));
		idColumn.setIdColumn(true);
		idColumn.setVisibility(false);
		columns.add(0, idColumn);
		super.setColumns(columns);
	}

}
