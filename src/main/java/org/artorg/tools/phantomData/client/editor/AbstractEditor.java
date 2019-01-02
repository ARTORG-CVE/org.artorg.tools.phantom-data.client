package org.artorg.tools.phantomData.client.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.scene.Node;

public abstract class AbstractEditor<T, U> implements IPropertyNode {
	private BiConsumer<T, U> valueToEntitySetter;
	private Function<T, U> entityToValueEditGetter;
	private Function<T, U> entityToValueAddGetter;
	private Consumer<U> valueToNodeSetter;
	private Supplier<U> nodeToValueGetter;
	private Runnable defaultSetterRunnable;
	private final List<IPropertyNode> propertyNodes;
	private Class<T> itemClass;
	private Node node;

	public AbstractEditor(Class<T> itemClass, Node node) {
		valueToEntitySetter = this::valueToEntitySetterImpl;
		entityToValueEditGetter = this::entityToValueEditGetterImpl;
		entityToValueAddGetter = this::entityToValueAddGetterImpl;
		valueToNodeSetter = this::valueToNodeSetterImpl;
		nodeToValueGetter = this::nodeToValueGetterImpl;
		defaultSetterRunnable = this::defaultSetterRunnableImpl;
		propertyNodes = new ArrayList<>();
		this.itemClass = itemClass;
		this.node = node;
	}

	protected abstract U entityToValueEditGetterImpl(T item);

	protected abstract U entityToValueAddGetterImpl(T item);

	protected abstract void valueToEntitySetterImpl(T item, U value);

	protected abstract U nodeToValueGetterImpl();

	protected abstract void valueToNodeSetterImpl(U value);

	protected abstract void defaultSetterRunnableImpl();
	
	@Override
	public final Node getNode() {
		return node;
	}
	
	@Override
	public List<IPropertyNode> getChildrenProperties() {
		return propertyNodes;
	}
	
	public Class<T> getItemClass() {
		return itemClass;
	}

	public void setItemClass(Class<T> itemClass) {
		this.itemClass = itemClass;
	}

	public final void setNode(Node node) {
		this.node = node;
	}

	public <V> AbstractEditor<V, U> map(Class<V> superClass, Function<V, T> editGetter, Function<V,T> addGetter, BiConsumer<V,T> setter) {
		return new AbstractEditor<V, U>(superClass, node) {

			@Override
			protected U entityToValueEditGetterImpl(V item) {
				T t = editGetter.apply(item);
				return AbstractEditor.this.getEntityToValueEditGetter().apply(t);
			}

			@Override
			protected U entityToValueAddGetterImpl(V item) {
				T t = addGetter.apply(item);
				return AbstractEditor.this.getEntityToValueAddGetter().apply(t);
			}

			@Override
			protected void valueToEntitySetterImpl(V item, U value) {
				T t = editGetter.apply(item);
				if (t == null)  {
					try {
						t = AbstractEditor.this.getItemClass().newInstance();
					} catch (InstantiationException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				
				AbstractEditor.this.getValueToEntitySetter().accept(t, value);
				setter.accept(item, t);
			}

			@Override
			protected U nodeToValueGetterImpl() {
				return AbstractEditor.this.getNodeToValueGetter().get();
			}

			@Override
			protected void valueToNodeSetterImpl(U value) {
				AbstractEditor.this.getValueToNodeSetter().accept(value);
			}

			@Override
			protected void defaultSetterRunnableImpl() {
			}

		};
	}

	public void setDefaultValue(U value) {
		setDefaultSetterRunnable(() -> valueToNodeSetter.accept(value));
	}

//	public PropertyEntry toPropertyEntry(String labelName) {
//		return toPropertyEntry(new Label(labelName));
//	}
//
//	public PropertyEntry toPropertyEntry(Label label) {
//		return new PropertyEntry(label, this);
//	}

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

	public AbstractEditor<T, U> setValueToEntitySetter(BiConsumer<T, U> valueToEntitySetter) {
		this.valueToEntitySetter = valueToEntitySetter;
		return this;
	}

	public Function<T, U> getEntityToValueEditGetter() {
		return entityToValueEditGetter;
	}

	public AbstractEditor<T, U> setEntityToValueEditGetter(Function<T, U> entityToValueEditGetter) {
		this.entityToValueEditGetter = entityToValueEditGetter;
		return this;
	}

	public Function<T, U> getEntityToValueAddGetter() {
		return entityToValueAddGetter;
	}

	public AbstractEditor<T, U> setEntityToValueAddGetter(Function<T, U> entityToValueAddGetter) {
		this.entityToValueAddGetter = entityToValueAddGetter;
		return this;
	}

	public Consumer<U> getValueToNodeSetter() {
		return valueToNodeSetter;
	}

	public AbstractEditor<T, U> setValueToNodeSetter(Consumer<U> valueToNodeSetter) {
		this.valueToNodeSetter = valueToNodeSetter;
		return this;
	}

	public Supplier<U> getNodeToValueGetter() {
		return nodeToValueGetter;
	}

	public AbstractEditor<T, U> setNodeToValueGetter(Supplier<U> nodeToValueGetter) {
		this.nodeToValueGetter = nodeToValueGetter;
		return this;
	}

	public Runnable getDefaultSetterRunnable() {
		return defaultSetterRunnable;
	}

	public AbstractEditor<T, U> setDefaultSetterRunnable(Runnable defaultSetterRunnable) {
		this.defaultSetterRunnable = defaultSetterRunnable;
		return this;
	}

	
	

}
