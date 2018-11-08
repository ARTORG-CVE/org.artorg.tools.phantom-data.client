package org.artorg.tools.phantomData.client.table;

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
	public void updateColumns() {
		getColumns().forEach(column -> column.setIdColumn(false));
		AbstractColumn<ITEM,String> idColumn = new Column<ITEM,String>(
			"ID", item -> item, 
			path -> path.getId().toString(), 
			(path,value) -> path.setId(value));
		idColumn.setIdColumn(true);
		idColumn.setVisibility(false);
		getColumns().add(0, idColumn);
		super.updateColumns();
	}

}
