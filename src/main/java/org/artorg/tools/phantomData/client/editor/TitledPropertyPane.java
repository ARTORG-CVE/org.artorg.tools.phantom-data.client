package org.artorg.tools.phantomData.client.editor;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;

public class TitledPropertyPane extends TitledPane implements IPropertyNode {
	private final List<IPropertyNode> propertyChildren;
	private final IPropertyNode propertyNode;
	
	{
		propertyChildren = new ArrayList<>();
	}
	
	public TitledPropertyPane(String title, IPropertyNode propertyNode) {
		this.propertyNode = propertyNode;
		addPropertyNode(propertyNode);
		setText(title);
		setContent(propertyNode.getNode());
	}

	public IPropertyNode getIPropertyNode() {
		return propertyNode;
	}
	
	@Override
	public Node getNode() {
		return this;
	}

	@Override
	public List<IPropertyNode> getChildrenProperties() {
		return propertyChildren;
	}

}
