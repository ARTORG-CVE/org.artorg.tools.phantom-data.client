package org.artorg.tools.phantomData.client.table;

import java.util.List;

import org.artorg.tools.phantomData.server.model.FabricationType;

public interface ITableSpringDb<ITEM> {
	
	List<IColumn<FabricationType>> createColumns();
	
	void setItemClass(Class<?> itemClass);
	
	String getTableName();
	
	void setTableName();

}
