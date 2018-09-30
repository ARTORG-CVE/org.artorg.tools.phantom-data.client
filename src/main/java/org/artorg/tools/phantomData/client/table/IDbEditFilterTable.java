package org.artorg.tools.phantomData.client.table;

import org.artorg.tools.phantomData.server.specification.DbPersistent;

public interface IDbEditFilterTable<ITEM extends DbPersistent<ITEM,?>> extends IDbTable<ITEM>, IEditFilterTable<ITEM> {

}
