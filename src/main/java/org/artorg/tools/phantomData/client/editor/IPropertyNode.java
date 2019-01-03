package org.artorg.tools.phantomData.client.editor;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;

public interface IPropertyNode {

	Node getNode();

	List<IPropertyNode> getChildrenProperties();
	
	default void addPropertyNode(IPropertyNode propertyNode) {
		getChildrenProperties().add(propertyNode);
	}
	
	default List<IPropertyNode> flatToLeafs() {
		List<IPropertyNode> children = getChildrenProperties();
		List<IPropertyNode> leafs = new ArrayList<>();
		if (children.isEmpty()) return leafs;
		for (IPropertyNode child : children) {
			if (child.getChildrenProperties().isEmpty())
				leafs.add(child);
			else
				leafs.addAll(child.flatToLeafs());
		}
		return leafs;
	}
	
	default List<IPropertyNode> flatToList() {
		List<IPropertyNode> list = new ArrayList<>();
		List<IPropertyNode> children = getChildrenProperties();
		if (children.isEmpty()) return list;
		for (IPropertyNode child : children) {
			list.add(child);
			if (!child.getChildrenProperties().isEmpty())
				list.addAll(child.flatToList());
		}
		return list;
	}

}
