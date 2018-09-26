package org.artorg.tools.phantomData.client.controller;

import java.util.Set;

import org.artorg.tools.phantomData.client.util.Reflect;

import javafx.collections.ObservableList;
import javafx.scene.Node;

public interface ISelector<ITEM, SUB_ITEM> {
	
	void setSubItemClass(Class<?> subItemClass);
	
	Class<?> getSubItemClass();
	
	ObservableList<SUB_ITEM> getSelectableItems();
	
	ObservableList<SUB_ITEM> getSelectedItems();
	
	void setSelectableItems(Set<SUB_ITEM> set); 
	
	void setSelectedItems(Set<SUB_ITEM> set);
	
	void moveToSelected(SUB_ITEM item);

	void moveToSelectable(SUB_ITEM item);
	
	Node getGraphic();
	
	void init();
	
	default void setSelectedChildItems(ITEM item) {
		Class<?> paramTypeClass = getSelectedItems().getClass();
		Object arg = getSelectedItems();
		Reflect.invokeGenericSetter(item, paramTypeClass, getSubItemClass(), arg);
	}
	
}
