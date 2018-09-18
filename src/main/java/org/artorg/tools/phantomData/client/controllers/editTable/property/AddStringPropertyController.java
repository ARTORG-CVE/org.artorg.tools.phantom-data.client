package org.artorg.tools.phantomData.client.controllers.editTable.property;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.connectors.property.StringPropertyConnector;
import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.server.model.property.PropertyField;
import org.artorg.tools.phantomData.server.model.property.StringProperty;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class AddStringPropertyController extends AddEditController<StringProperty, Integer> {
	private ComboBox<PropertyField> comboBoxPropertyField;
	private TextField textFieldValue;
	
	{
		comboBoxPropertyField = new ComboBox<PropertyField>();
		textFieldValue = new TextField();
		
		super.addProperty("Property Field", comboBoxPropertyField);
		super.addProperty("Value", textFieldValue);
		
		createComboBox(comboBoxPropertyField, PropertyFieldConnector.get(), d -> String.valueOf(d.getName()));
		
		super.init();
	}
	
	@Override
	public void initDefaultValues() {
		comboBoxPropertyField.getSelectionModel().clearSelection();
		textFieldValue.setText("");
	}

	@Override
	public StringProperty createItem() {
		PropertyField propertyField = comboBoxPropertyField.getSelectionModel().getSelectedItem();
		String value = textFieldValue.getText();
		
		return new StringProperty(propertyField, value);
	}

	@Override
	protected HttpConnectorSpring<StringProperty, Integer> getConnector() {
		return StringPropertyConnector.get();
	}

}