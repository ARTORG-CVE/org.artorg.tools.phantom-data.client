package org.artorg.tools.phantomData.client.itemEdit;

import javafx.scene.Node;

public interface FxFactory<ITEM> {
	
	Node getGraphic();
	
	Node create(ITEM item, Class<?> itemClass);
	
	Node edit(ITEM item, Class<?> itemClass);

	Node create(Class<?> itemClass);

}
