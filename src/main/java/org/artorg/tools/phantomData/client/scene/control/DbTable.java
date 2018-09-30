package org.artorg.tools.phantomData.client.scene.control;

import java.util.List;

import org.artorg.tools.phantomData.client.connector.CrudConnectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.IDbTable;
import org.artorg.tools.phantomData.client.table.LambdaColumn;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class DbTable<ITEM extends DbPersistent<ITEM,?>> extends Table<ITEM> implements IDbTable<ITEM> {
	private ICrudConnector<ITEM,?> connector;

	@Override
	public void setItemClass(Class<ITEM> itemClass) {
		super.setItemClass(itemClass);
		setConnector(CrudConnectors.<ITEM>getConnector(itemClass));
	}

	@Override
	public ICrudConnector<ITEM,?> getConnector() {
		return this.connector;
	}

	@Override
	public void setConnector(ICrudConnector<ITEM,?> connector) {
		this.connector = connector;
	}
	
	@Override
	public void setColumns(List<Column<ITEM>> columns) {
		columns.forEach(column -> column.setIdColumn(false));
		LambdaColumn<ITEM,ITEM> idColumn = new LambdaColumn<ITEM, ITEM>(
			"ID", item -> item, 
			path -> path.getId().toString(), 
			(path,value) -> path.setId(value));
		idColumn.setIdColumn(true);
		idColumn.setVisibility(false);
		columns.add(0, idColumn);
		super.setColumns(columns);
	}

}
