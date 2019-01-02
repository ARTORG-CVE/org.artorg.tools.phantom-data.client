package org.artorg.tools.phantomData.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.util.CollectionUtil;
import org.artorg.tools.phantomData.client.util.FxUtil;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;

public interface IPropertyNode {

//	Node getControlNode();
//
//	Pane getPane();

//	void addGraphically(Node node);

	Node getNode();

	List<IPropertyNode> getChildrenProperties();
	
	default void addPropertyNode(IPropertyNode propertyNode) {
		getChildrenProperties().add(propertyNode);
	}
	
	default List<IPropertyNode> findLeafs() {
		List<IPropertyNode> children = getChildrenProperties();
		List<IPropertyNode> leafs = new ArrayList<>();
		if (children.isEmpty()) return leafs;
		for (IPropertyNode child : children) {
			if (child.getChildrenProperties().isEmpty())
				leafs.add(child);
			else
				leafs.addAll(child.findLeafs());
		}
		return leafs;
	}

//	Class<T> getItemClass();


//	@SuppressWarnings("unchecked")
//	default void addOn(PropertyGridPane<T> propertyPane, String name) {
//		propertyPane.addEntry(name, (AbstractEditor<T, ?>) this);
////		
////		addOn(new Label(name), propertyPane);
//	}

//	default void addOn(Node leftNode, PropertyGridPane<T> propertyGridPane) {
//		propertyGridPane.addEntry(new PropertyEntry(leftNode, getPane()));
//	}

//	default void addGraphically(IPropertyNode<T> propertyNode) {
//		addGraphically(propertyNode.getPane());
//	}

//	default void add(IPropertyNode<T> propertyNode) {
//		addAsChild(propertyNode);
////		addGraphically(propertyNode);
//	}

//	default void addOn(IPropertyNode<T> propertyNode) {
//		propertyNode.add(this);
//	}

//	default IPropertyNode<T> setTitled(String title) {
//		if (getControlNode() == getPane())
//			throw new IllegalArgumentException();
//		TitledPane titledPane = new TitledPane();
//		titledPane.setText(title);
//		titledPane.setExpanded(false);
//		titledPane.setContent(getControlNode());
//		getPane().getChildren().clear();
//		FxUtil.addToPane(getPane(), titledPane);
//		return this;
//	}
//
//	default IPropertyNode<T> setUntitled() {
//		getPane().getChildren().clear();
//		FxUtil.addToPane(getPane(), getControlNode());
//		return this;
//	}

}
