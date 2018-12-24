package org.artorg.tools.phantomData.client.editor;

import javafx.scene.Node;

public interface FxFactory<T> {
	
	Node getGraphic();
	
	Node create(T item);
	
	Node edit(T item);

	Node create();

}
