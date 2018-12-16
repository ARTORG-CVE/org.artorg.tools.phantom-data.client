package org.artorg.tools.phantomData.client.editor;

import javafx.scene.Node;

public interface FxFactory<T> {
	
	Node getGraphic();
	
	Node create(T item, Class<?> itemClass);
	
	Node edit(T item, Class<?> itemClass);

	Node create(Class<?> itemClass);

}
