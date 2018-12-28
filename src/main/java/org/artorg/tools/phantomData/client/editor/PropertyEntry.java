package org.artorg.tools.phantomData.client.editor;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class PropertyEntry {
	private Node leftNode;
	private Node rightNode;

	public PropertyEntry(String labelText, TextField textField, Runnable rc) {
		this(labelText, textField);
		textField.textProperty().addListener(event -> {
    		rc.run();
    	});
	}
	
	public PropertyEntry(String labelText, Node nodeRight) {
		this(new Label(labelText), nodeRight);
	}
	
	public PropertyEntry(Node leftNode, Node rightNode) {
		this.setLeftNode(leftNode);
		this.rightNode = rightNode;
	}
	
	public boolean addOn(List<PropertyEntry> list) {
		return list.add(this);
	}

	public Node getLeftNode() {
		return leftNode;
	}

	public void setLeftNode(Node leftNode) {
		this.leftNode = leftNode;
	}
	
	public Node getRightNode() {
		return rightNode;
	}

	public void setRightNode(Control rightNode) {
		this.rightNode = rightNode;
	}

}
