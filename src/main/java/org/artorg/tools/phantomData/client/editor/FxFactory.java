package org.artorg.tools.phantomData.client.editor;

import javafx.scene.Node;

public interface FxFactory<ITEM> {
	
	Node getGraphic();
	
	Node create(ITEM item, Class<?> itemClass);
	
	Node edit(ITEM item, Class<?> itemClass);

	Node create(Class<?> itemClass);

}
