package org.artorg.tools.phantomData.client.controller;

import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class PropertyEntry {
	private Node leftNode;
	private Control rightNode;

	public PropertyEntry(String labelText, TextField textField, Runnable rc) {
		this(labelText, textField);
		textField.textProperty().addListener(event -> {
    		rc.run();
    	});
	}
	
	public PropertyEntry(String labelText, Control nodeRight) {
		this(new Label(labelText), nodeRight);
	}
	
	public PropertyEntry(Node leftNode, Control rightNode) {
		this.setLeftNode(leftNode);
		this.rightNode = rightNode;
	}

	public Node getLeftNode() {
		return leftNode;
	}

	public void setLeftNode(Node leftNode) {
		this.leftNode = leftNode;
	}
	
	public Control getRightNode() {
		return rightNode;
	}

	public void setRightNode(Control rightNode) {
		this.rightNode = rightNode;
	}

}
