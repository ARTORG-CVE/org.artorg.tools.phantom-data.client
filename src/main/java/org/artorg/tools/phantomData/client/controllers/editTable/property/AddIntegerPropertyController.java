package org.artorg.tools.phantomData.client.controllers.editTable.property;

import java.util.List;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.property.IntegerPropertyConnector;
import org.artorg.tools.phantomData.client.connectors.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.server.model.property.DoubleProperty;
import org.artorg.tools.phantomData.server.model.property.IntegerProperty;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class AddIntegerPropertyController extends AddEditController<IntegerProperty> {
	private ComboBox<PropertyField> comboBoxPropertyField;
	private TextField textFieldValue;

	{
		comboBoxPropertyField = new ComboBox<PropertyField>();
		textFieldValue = new TextField();
	}
	
	@Override
	public IntegerProperty createItem() {
		PropertyField propertyField = comboBoxPropertyField.getSelectionModel().getSelectedItem();
		Integer value = Integer.valueOf(textFieldValue.getText());
		return new IntegerProperty(propertyField, value);
	}

	@Override
	protected HttpConnectorSpring<IntegerProperty> getConnector() {
		return IntegerPropertyConnector.get();
	}

	@Override
	protected void addPropertyEntries(List<PropertyEntry> entries) {
		createComboBox(comboBoxPropertyField, PropertyFieldConnector.get(), d -> String.valueOf(d.getName()));
		entries.add(new PropertyEntry("Property Field", comboBoxPropertyField));
		entries.add(new PropertyEntry("Value", textFieldValue));
	}

	@Override
	protected void setTemplate(IntegerProperty item) {
		super.selectComboBoxItem(comboBoxPropertyField, item.getPropertyField());
		textFieldValue.setText(Integer.toString(item.getValue()));
	}
	
	@Override
	protected void copy(IntegerProperty from, IntegerProperty to) {
		to.setPropertyField(from.getPropertyField());
		to.setValue(from.getValue());
	}
	
}