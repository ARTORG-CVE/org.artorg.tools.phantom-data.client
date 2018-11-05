package org.artorg.tools.phantomData.client.beans;

import java.util.List;
import java.util.function.Function;

public class FactoryNode<T> extends Node<T> {
	private Function<Node<T>, List<Node<T>>> childrenFactory;
	
	public Function<Node<T>, List<Node<T>>> getChildrenFactory() {
		return childrenFactory;
	}
	
	public void setChildrenFactory(Function<Node<T>, List<Node<T>>> childrenFactory) {
		this.childrenFactory = childrenFactory;
	}
	
	public void updateChildren() {
		setChildren(getChildrenFactory().apply(this));
	}
	
	

}
