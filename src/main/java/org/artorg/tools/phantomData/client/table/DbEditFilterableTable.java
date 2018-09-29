package org.artorg.tools.phantomData.client.table;

import org.artorg.tools.phantomData.server.specification.DbPersistent;

public interface DbEditFilterableTable<ITEM extends DbPersistent<ITEM,?>> extends DatabaseableTable<ITEM>, TableEditFilterable<ITEM> {

}
