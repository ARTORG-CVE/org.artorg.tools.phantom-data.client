package org.artorg.tools.phantomData.client.controller;

import java.util.Set;

import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.collections.ObservableList;
import javafx.scene.Node;

public interface ISelector<ITEM extends DbPersistent<ITEM>> {
	
	void setSubItemClass(Class<?> subItemClass);
	
	Class<?> getSubItemClass();
	
	ObservableList<?> getSelectableItems();
	
	ObservableList<?> getSelectedItems();
	
	void setSelectableItems(Set<?> set); 
	
	void setSelectedItems(Set<?> set);
	
	void moveToSelected(Object item);

	void moveToSelectable(Object item);
	
	Node getGraphic();
	
	void init();
	
	default void setSelectedChildItems(ITEM item) {
		Class<?> paramTypeClass = getSelectedItems().getClass();
		Object arg = getSelectedItems();
		Reflect.invokeGenericSetter(item, paramTypeClass, getSubItemClass(), arg);
	}
	
}
