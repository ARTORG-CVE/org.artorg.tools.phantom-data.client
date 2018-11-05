package org.artorg.tools.phantomData.client.beans;

import java.util.List;

public abstract class Node<T> {
	private List<Node<T>> children;
	
	public boolean isLeaf() {
		if (getChildren() == null) return true;
		return getChildren().size()==0;
	}
	
	public List<Node<T>> getChildren() {
		return children;
	}
	
	public void setChildren(List<Node<T>> children) {
		this.children = children;
	}

}
