package org.artorg.tools.phantomData.client.editor2;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.artorg.tools.phantomData.client.editor.PropertyEntry;

import javafx.scene.Node;
import javafx.scene.control.Label;

public abstract class PropertyNode<T,U> {
	private final Class<T> itemClass;
	private final Node controlNode;
	private BiConsumer<T,U> valueToEntitySetter;
	private Function<T,U> entityToValueEditGetter;
	private Function<T,U> entityToValueAddGetter;
	private Consumer<U> valueToNodeSetter;
	private Supplier<U> nodeToValueGetter;
	private Runnable defaultSetterRunnable;
	private Node parentNode;

	public PropertyNode(Class<T> itemClass, Node controlNode) {
		this.itemClass = itemClass;
		this.controlNode = controlNode;
		this.parentNode = controlNode;
		valueToEntitySetter = this::valueToEntitySetterImpl;
		entityToValueEditGetter = this::entityToValueEditGetterImpl;
		entityToValueAddGetter = this::entityToValueAddGetterImpl;
		valueToNodeSetter = this::valueToNodeSetterImpl;
		nodeToValueGetter = this::nodeToValueGetterImpl;
		defaultSetterRunnable = this::defaultSetterRunnableImpl;
	}
	
	protected abstract U entityToValueEditGetterImpl(T item);
	
	protected abstract U entityToValueAddGetterImpl(T item);
	
	protected abstract void valueToEntitySetterImpl(T item, U value);
	
	protected abstract U nodeToValueGetterImpl();
	
	protected abstract void valueToNodeSetterImpl(U value);
	
	protected abstract void defaultSetterRunnableImpl();
	
	public boolean addLabeled(String labelName, List<PropertyEntry> entries) {
		return entries.add(toPropertyEntry(labelName));
	}
	
	public PropertyEntry toPropertyEntry(String labelName) {
		return toPropertyEntry(new Label(labelName));
	}
	
	public PropertyEntry toPropertyEntry(Label label) {
		return new PropertyEntry(label, this.getParentNode());
	}
	
	
	public final void nodeToEntity(T item) {
		valueToEntitySetter.accept(item, nodeToValueGetter.get());
	}
	
	public final void entityToNodeEdit(T item) {
		valueToNodeSetter.accept(entityToValueEditGetter.apply(item));
	}
	
	public final void entityToNodeAdd(T item) {
		valueToNodeSetter.accept(entityToValueAddGetter.apply(item));
	}
	
	public final void setDefault() {
		defaultSetterRunnable.run();
	}

	public BiConsumer<T, U> getValueToEntitySetter() {
		return valueToEntitySetter;
	}

	public PropertyNode<T,U> setValueToEntitySetter(BiConsumer<T, U> valueToEntitySetter) {
		this.valueToEntitySetter = valueToEntitySetter;
		return this;
	}

	public Function<T, U> getEntityToValueEditGetter() {
		return entityToValueEditGetter;
	}

	public PropertyNode<T,U> setEntityToValueEditGetter(Function<T, U> entityToValueEditGetter) {
		this.entityToValueEditGetter = entityToValueEditGetter;
		return this;
	}

	public Function<T, U> getEntityToValueAddGetter() {
		return entityToValueAddGetter;
	}

	public PropertyNode<T,U> setEntityToValueAddGetter(Function<T, U> entityToValueAddGetter) {
		this.entityToValueAddGetter = entityToValueAddGetter;
		return this;
	}

	public Consumer<U> getValueToNodeSetter() {
		return valueToNodeSetter;
	}

	public PropertyNode<T,U> setValueToNodeSetter(Consumer<U> valueToNodeSetter) {
		this.valueToNodeSetter = valueToNodeSetter;
		return this;
	}

	public Supplier<U> getNodeToValueGetter() {
		return nodeToValueGetter;
	}

	public PropertyNode<T,U> setNodeToValueGetter(Supplier<U> nodeToValueGetter) {
		this.nodeToValueGetter = nodeToValueGetter;
		return this;
	}

	public Runnable getDefaultSetterRunnable() {
		return defaultSetterRunnable;
	}

	public PropertyNode<T,U> setDefaultSetterRunnable(Runnable defaultSetterRunnable) {
		this.defaultSetterRunnable = defaultSetterRunnable;
		return this;
	}

	public Node getParentNode() {
		return this.parentNode;
	}

	public PropertyNode<T,U> setParentNode(Node node) {
		this.parentNode = node;
		return this;
	}
	
	

	public Node getControlNode() {
		return controlNode;
	}

	public Class<T> getItemClass() {
		return itemClass;
	}

//	public Class<?> getValueClass() {
//		return valueClass;
//	}

}
