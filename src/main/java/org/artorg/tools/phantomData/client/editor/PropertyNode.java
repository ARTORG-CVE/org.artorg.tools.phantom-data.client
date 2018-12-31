package org.artorg.tools.phantomData.client.editor;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.server.util.FxUtil;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public abstract class PropertyNode<T> extends AnchorPane implements IPropertyNode<T> {
	private final Class<T> itemClass;
	private final List<IPropertyNode<T>> propertyChildren;
	private final VBox vBox;
	
	{
		propertyChildren = new ArrayList<>();
		vBox = new VBox();
		FxUtil.addToPane(this, vBox);
	}
	
	public PropertyNode(Class<T> itemClass) {
		this.itemClass = itemClass;
	}
	
	@Override
	public List<IPropertyNode<T>> getPropertyChildren() {
		return propertyChildren;
	}
	
	@Override
	public Class<T> getItemClass() {
		return itemClass;
	}

	@Override
	public Pane getPane() {
		return this;
	}

	@Override
	public void addGraphically(Node propertyNode) {
		vBox.getChildren().add(propertyNode);
	}

	@Override
	public void addAsChild(IPropertyNode<T> propertyNode) {
		propertyChildren.add(propertyNode);
	}

	public VBox getvBox() {
		return vBox;
	}
	
}
