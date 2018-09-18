package org.artorg.tools.phantomData.client.controller;

import java.util.List;
import java.util.function.Consumer;

import org.artorg.tools.phantomData.client.connectors.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.server.model.property.Property;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;

public abstract class AddEditPropertyController<ITEM extends Property<VALUE, ID_TYPE>, VALUE extends Comparable<VALUE>, ID_TYPE> extends AddEditController<ITEM, ID_TYPE> {
	private ComboBox<PropertyField> comboBoxPropertyField;
	private Node rightNode;
	private Consumer<Node> templateSetter;
	
	
	public AddEditPropertyController(Node rightNode, Consumer<Node> templateSetter) {
		this.rightNode = rightNode;
		this.templateSetter = templateSetter;
	}
	
	@Override
	protected void addPropertyEntries(List<PropertyEntry> entries) {
		comboBoxPropertyField = new ComboBox<PropertyField>();
		
		createComboBox(comboBoxPropertyField, PropertyFieldConnector.get(), d -> String.valueOf(d.getName()));
		
		entries.add(new PropertyEntry("Property Field", comboBoxPropertyField));
		entries.add(new PropertyEntry("Value", rightNode));
	}

	@Override
	protected void setTemplate(ITEM item) {
		comboBoxPropertyField.getSelectionModel().select(item.getPropertyField());
		templateSetter.accept(rightNode);
	}

}
