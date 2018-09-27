package org.artorg.tools.phantomData.client.table;

import org.artorg.tools.phantomData.server.specification.DbPersistent;

public interface ITableSpringDbEditable<ITEM extends DbPersistent<ITEM,ID>, ID> extends DatabasableTable<ITEM,ID>, EditableTable<ITEM> {
	
	

}
