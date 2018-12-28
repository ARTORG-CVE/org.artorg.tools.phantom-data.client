package org.artorg.tools.phantomData.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.server.model.AbstractProperty;
import org.artorg.tools.phantomData.server.models.base.property.PropertyField;

import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

public abstract class DbPropertySelector<T, P extends AbstractProperty<P, V>, V>
		extends ItemEditor<T> {
	private final Class<T> parentItemClass;
	private final Class<P> propertyClass;

	public abstract PropertyNode<P, V> getValuePropertyNode();

	public abstract Collection<P> getProperties(T item);

	public abstract void setProperties(T item, Collection<P> properties);

	public DbPropertySelector(Class<T> parentItemClass, Class<P> propertyClass) {
		super(parentItemClass);
		this.parentItemClass = parentItemClass;
		this.propertyClass = propertyClass;

		List<PropertyEntry> entries = new ArrayList<>();

		ComboBox<PropertyField> comboBoxPropertyField = new ComboBox<>();

		ItemEditor<P> propertyEditor = new ItemEditor<>(propertyClass);

		propertyEditor.createComboBox(PropertyField.class, comboBoxPropertyField)
				.of(item -> item.getPropertyField(), (item, value) -> item.setPropertyField(value))
				.addLabeled("Property field", entries);
		PropertyNode<P, V> valuePropertyNode = getValuePropertyNode();
		valuePropertyNode.addLabeled("Value", entries);

		ICrudConnector<P> propertyConnector = Connectors.get(propertyClass);

		List<P> addableItems = new ArrayList<>();
		List<P> changedItems = new ArrayList<>();
		List<P> removableItems = new ArrayList<>();

		ProTableView<P> table = Main.getUIEntity(propertyClass).createProTableView();

		AbstractProperty<P, V> property = null;
		UUID id = property.getId();
		PropertyNode<T, P> tableEditNode = new PropertyNode<T, P>(parentItemClass, table) {

			@Override
			protected P entityToValueEditGetterImpl(T item) {
				return getProperties(item).stream().filter(p -> p.getId().equals(id)).findFirst()
						.orElse(null);
			}

			@Override
			protected P entityToValueAddGetterImpl(T item) {
				return null;
			}

			@Override
			protected void valueToEntitySetterImpl(T item, P newProperty) {
				Collection<P> properties = getProperties(item);
				UUID id = newProperty.getId();
				Optional<P> persistedProperty = findElement(properties, id);
				if (addableItems.contains(newProperty)) {
					if (persistedProperty.isPresent())
						throw new RuntimeException();
					properties.add(newProperty);
				} else if (removableItems.contains(newProperty)) {
					if (!persistedProperty.isPresent())
						throw new RuntimeException();
					properties.remove(persistedProperty.get());
				} else if (changedItems.contains(newProperty)) {
					if (!persistedProperty.isPresent()) 
						throw new RuntimeException();
					if (persistedProperty.get() != newProperty) {
						properties.remove(persistedProperty.get());
						properties.add(newProperty);
					}
				}
			}

			@Override
			protected P nodeToValueGetterImpl() {
				Optional<P> p = findElement(table.getTable().getItems(), id);
				if (!p.isPresent())
					throw new RuntimeException();
				return p.get();
			}

			@Override
			protected void valueToNodeSetterImpl(P value) {
				// TODO Auto-generated method stub

			}

			@Override
			protected void defaultSetterRunnableImpl() {
				// TODO Auto-generated method stub

			}

		};

		PropertyNode<T, Collection<P>> selector =
				createSelector(propertyClass).of(this::getProperties, this::setProperties);

		addNodes(propertyEditor);

	}

	private Optional<P> findElement(Collection<P> properties, UUID id) {
		return properties.stream().filter(p -> p.getId().equals(id)).findFirst();
	}

	public Class<T> getParentItemClass() {
		return parentItemClass;
	}

	public Class<P> getPropertyClass() {
		return propertyClass;
	}

}
