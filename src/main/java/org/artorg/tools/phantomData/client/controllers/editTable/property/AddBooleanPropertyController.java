package org.artorg.tools.phantomData.client.controllers.editTable.property;

import java.util.List;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.property.BooleanPropertyConnector;
import org.artorg.tools.phantomData.client.connectors.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

public class AddBooleanPropertyController extends AddEditController<BooleanProperty, Integer> {
	private ComboBox<PropertyField> comboBoxPropertyField;
	private CheckBox checkBoxValue;

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

	@Override
	protected void setTemplate(BooleanProperty item) {
		comboBoxPropertyField.getSelectionModel().select(item.getPropertyField());
		checkBoxValue.setSelected(item.getValue());
	}

	@Override
	protected void addPropertyEntries(List<PropertyEntry> entries) {
		comboBoxPropertyField = new ComboBox<PropertyField>();
		checkBoxValue = new CheckBox();
		
		createComboBox(comboBoxPropertyField, PropertyFieldConnector.get(), d -> String.valueOf(d.getName()));
		
		entries.add(new PropertyEntry("Property Field", comboBoxPropertyField));
		entries.add(new PropertyEntry("Value", checkBoxValue));
	}

}