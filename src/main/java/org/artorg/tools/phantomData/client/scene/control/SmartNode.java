package org.artorg.tools.phantomData.client.scene.control;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;

public class SmartNode {
	private Node primaryNode;
	private Node secondaryNode;
	private Supplier<ObservableList<Node>> parentItemsSupplier;
	private Consumer<? extends Node> nodeAddPolicy;
	private Consumer<? extends Node> nodeRemovePolicy;
	
	{
		parentItemsSupplier = () -> FXCollections.observableArrayList(new ArrayList<Node>());
		nodeAddPolicy = node -> parentItemsSupplier.get().add(parentItemsSupplier.get().size(), node);
		nodeRemovePolicy = node -> parentItemsSupplier.get().remove(node);
	}
	
	public ObservableList<Node> getParentItems() {
		return parentItemsSupplier.get();
	}
	
	public Node getPrimaryNode() {
		return primaryNode;
	}
	
	public void setPrimaryNode(Node primaryNode) {
		this.primaryNode = primaryNode;
	}
	
	public Node getSecondaryNode() {
		return secondaryNode;
	}
	
	public void setSecondaryNode(Node secondaryNode) {
		this.secondaryNode = secondaryNode;
	}
	
	public Supplier<ObservableList<Node>> getParentItemsSupplier() {
		return parentItemsSupplier;
	}
	
	public void setParentItemsSupplier(Supplier<ObservableList<Node>> parentItemsSupplier) {
		this.parentItemsSupplier = parentItemsSupplier;
	}
	
	@SuppressWarnings("unchecked")
	public Consumer<Node> getNodeAddPolicy() {
		return (Consumer<Node>) nodeAddPolicy;
	}
	
	public void setNodeAddPolicy(Consumer<? extends Node> nodeAddPolicy) {
		this.nodeAddPolicy = nodeAddPolicy;
	}
	
	@SuppressWarnings("unchecked")
	public Consumer<Node> getNodeRemovePolicy() {
		return (Consumer<Node>) nodeRemovePolicy;
	}
	
	public void setNodeRemovePolicy(Consumer<? extends Node> nodeRemovePolicy) {
		this.nodeRemovePolicy = nodeRemovePolicy;
	}
	
}
