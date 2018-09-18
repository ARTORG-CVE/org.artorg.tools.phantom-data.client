package org.artorg.tools.phantomData.client.controllers.editTable.property;

import java.util.List;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.property.DoublePropertyConnector;
import org.artorg.tools.phantomData.client.connectors.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.server.model.property.DoubleProperty;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class AddDoublePropertyController extends AddEditController<DoubleProperty, Integer> {
	private ComboBox<PropertyField> comboBoxPropertyField;
	private TextField textFieldValue;
	
	@Override
	public DoubleProperty createItem() {
		PropertyField propertyField = comboBoxPropertyField.getSelectionModel().getSelectedItem();
		Double value = Double.valueOf(textFieldValue.getText());
		
		return new DoubleProperty(propertyField, value);
	}

	@Override
	protected HttpConnectorSpring<DoubleProperty, Integer> getConnector() {
		return DoublePropertyConnector.get();
	}

	@Override
	protected void addPropertyEntries(List<PropertyEntry> entries) {
		comboBoxPropertyField = new ComboBox<PropertyField>();
		textFieldValue = new TextField();
		
		createComboBox(comboBoxPropertyField, PropertyFieldConnector.get(), d -> String.valueOf(d.getName()));
		
		entries.add(new PropertyEntry("Property Field", comboBoxPropertyField));
		entries.add(new PropertyEntry("Value", textFieldValue));
	}

	@Override
	protected void setTemplate(DoubleProperty item) {
		comboBoxPropertyField.getSelectionModel().select(item.getPropertyField());
		textFieldValue.setText(Double.toString(item.getValue()));
	}

}
