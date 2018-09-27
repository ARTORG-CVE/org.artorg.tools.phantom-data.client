package org.artorg.tools.phantomData.client.table;

import java.util.HashSet;
import java.util.Set;

import org.artorg.tools.phantomData.server.specification.DbPersistent;

public interface DatabasableTable<ITEM extends DbPersistent<ITEM,ID>, ID> extends Table<ITEM>, DbConnectable<ITEM,ID>{
	
	
	default void readAllData() {
		Set<ITEM> itemSet = new HashSet<ITEM>();
		itemSet.addAll(getConnector().readAllAsSet());
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
