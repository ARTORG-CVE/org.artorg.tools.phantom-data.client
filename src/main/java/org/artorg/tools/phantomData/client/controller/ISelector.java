package org.artorg.tools.phantomData.client.controller;

import java.util.Set;

import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.collections.ObservableList;
import javafx.scene.Node;

public interface ISelector<ITEM extends DatabasePersistent, SUB_ITEM extends DatabasePersistent> {
	
	ITEM getItem();
	
	void setItem(ITEM item);
	
	Class<ITEM> getItemClass();
	
	Class<SUB_ITEM> getSubItemClass();
	
	void setSubItemClass(Class<SUB_ITEM> subItemClass);
	
	void setItemClass(Class<ITEM> itemClass);
	
	ObservableList<SUB_ITEM> getSelectableItems();
	
	ObservableList<SUB_ITEM> getSelectedItems();
	
	void setSelectableItems(Set<SUB_ITEM> set); 
	
	void setSelectedItems(Set<SUB_ITEM> set);
	
	void moveToSelected(SUB_ITEM item);

	void moveToSelectable(SUB_ITEM item);
	
	Node getGraphic();
	
	void init();
	
}
