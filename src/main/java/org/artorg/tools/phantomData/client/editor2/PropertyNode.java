package org.artorg.tools.phantomData.client.editor2;

import javafx.scene.Node;

public abstract class PropertyNode<T> {
	private final Class<T> itemClass;
	private final Node node;

	public PropertyNode(Class<T> itemClass, Node node) {
		this.itemClass = itemClass;
		this.node = node;
	}
	
	protected abstract void nodeToEntity(T item);
	
	protected abstract void entityToNodeEdit(T item);
	
	protected abstract void entityToNodeAdd(T item);
	
	protected abstract void defaultToNode();
	
	
	
	public void applyChange(T from, T to) {
		
	}

}
