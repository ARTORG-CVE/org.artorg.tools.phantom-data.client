package org.artorg.tools.phantomData.client.editor2;

import java.util.function.Consumer;

import javafx.scene.Node;

public abstract class PropertyNode<T> {
	private final Class<T> itemClass;
	private final Node node;
	private Consumer<T> consumerNodeToEntity;
	private Consumer<T> consumerEntityToNodeEdit;
	private Consumer<T> consumerEntityToNodeAdd;
	private Runnable runnableSetDefault;

	public PropertyNode(Class<T> itemClass, Node node) {
		this.itemClass = itemClass;
		this.node = node;
		
		consumerNodeToEntity = item -> nodeToEntityImpl(item);
		consumerEntityToNodeEdit = item -> entityToNodeEditImpl(item);
		consumerEntityToNodeAdd = item -> entityToNodeAddImpl(item);
		runnableSetDefault = () -> setDefaultImpl();
	}
	
	protected abstract void nodeToEntityImpl(T item);
	
	protected abstract void entityToNodeEditImpl(T item);
	
	protected abstract void entityToNodeAddImpl(T item);
	
	protected abstract void setDefaultImpl();
	
	public final void nodeToEntity(T item) {
		if (getConsumerNodeToEntity() != null)
			getConsumerNodeToEntity().accept(item);
	}
	
	public final void entityToNodeEdit(T item) {
		if (getConsumerEntityToNodeEdit() != null)
			getConsumerEntityToNodeEdit().accept(item);
	}
	
	public final void entityToNodeAdd(T item) {
		if (getConsumerEntityToNodeAdd() != null)
			getConsumerEntityToNodeAdd().accept(item);
	}
	
	public final void setDefault() {
		if (getRunnableSetDefault() != null)
			getRunnableSetDefault().run();
	}
	
	
	public void applyChange(T from, T to) {
		
	}

	public Node getNode() {
		return node;
	}

	

	public Class<T> getItemClass() {
		return itemClass;
	}

	public Consumer<T> getConsumerNodeToEntity() {
		return consumerNodeToEntity;
	}

	public void setConsumerNodeToEntity(Consumer<T> consumerNodeToEntity) {
		this.consumerNodeToEntity = consumerNodeToEntity;
	}

	public Consumer<T> getConsumerEntityToNodeEdit() {
		return consumerEntityToNodeEdit;
	}

	public void setConsumerEntityToNodeEdit(Consumer<T> consumerEntityToNodeEdit) {
		this.consumerEntityToNodeEdit = consumerEntityToNodeEdit;
	}

	public Consumer<T> getConsumerEntityToNodeAdd() {
		return consumerEntityToNodeAdd;
	}

	public void setConsumerEntityToNodeAdd(Consumer<T> consumerEntityToNodeAdd) {
		this.consumerEntityToNodeAdd = consumerEntityToNodeAdd;
	}

	public Runnable getRunnableSetDefault() {
		return runnableSetDefault;
	}

	public void setRunnableSetDefault(Runnable runnableSetDefault) {
		this.runnableSetDefault = runnableSetDefault;
	}

}
