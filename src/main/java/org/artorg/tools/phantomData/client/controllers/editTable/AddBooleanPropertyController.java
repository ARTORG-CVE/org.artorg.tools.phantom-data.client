package org.artorg.tools.phantomData.client.controllers.editTable;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.property.BooleanPropertyConnector;
import org.artorg.tools.phantomData.client.connectors.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

public class AddBooleanPropertyController extends AddEditController<BooleanProperty, Integer> {
	private ComboBox<PropertyField> comboBoxPropertyField;
	private CheckBox checkBoxValue;
	
	{
		comboBoxPropertyField = new ComboBox<PropertyField>();
		checkBoxValue = new CheckBox();
		
		super.addProperty("Property Field", comboBoxPropertyField);
		super.addProperty("Value", checkBoxValue);
		
		createComboBox(comboBoxPropertyField, PropertyFieldConnector.get(), d -> String.valueOf(d.getName()));
		
		super.init();
		
	}

	@Override
	public void initDefaultValues() {
		comboBoxPropertyField.getSelectionModel().clearSelection();
		checkBoxValue.setSelected(false);
	}

	@Override
	public BooleanProperty createItem() {
		PropertyField propertyField = comboBoxPropertyField.getSelectionModel().getSelectedItem();
		Boolean value = checkBoxValue.isSelected();
		
		return new BooleanProperty(propertyField, value);
	}

	@Override
	protected HttpConnectorSpring<BooleanProperty, Integer> getConnector() {
		return BooleanPropertyConnector.get();
	}

}