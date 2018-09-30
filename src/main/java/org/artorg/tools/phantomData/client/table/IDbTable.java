package org.artorg.tools.phantomData.client.table;

import java.util.HashSet;
import java.util.Set;

import org.artorg.tools.phantomData.client.connector.CrudConnectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public interface IDbTable<ITEM extends DbPersistent<ITEM,?>> extends ITable<ITEM>, IDbConnectable<ITEM>{

	default void readAllData() {
		Set<ITEM> itemSet = new HashSet<ITEM>();
		Class<ITEM> itemClass = getItemClass();
		if (itemClass == null)
			throw new NullPointerException();
		ICrudConnector<ITEM,?> connector = getConnector();
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