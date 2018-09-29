package org.artorg.tools.phantomData.client.table;

import java.util.HashSet;
import java.util.Set;

import org.artorg.tools.phantomData.client.connector.CrudConnector;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public interface DatabaseableTable<ITEM extends DbPersistent<ITEM,?>> extends Table<ITEM>, DbConnectable<ITEM>{
	
	
	default void readAllData() {
		Set<ITEM> itemSet = new HashSet<ITEM>();
		CrudConnector<ITEM,?> connector = getConnector();
		if (connector == null)
			throw new NullPointerException();
		itemSet.addAll(connector.readAllAsSet());
		getItems().clear();
		getItems().addAll(itemSet);
	}
	
	@Override
	default String getItemName() {
		return getItemClass().getSimpleName();
	}
	
	default void createItem(ITEM item) {
		getConnector().create(item);
		getItems().add(item);
	}
	
}
