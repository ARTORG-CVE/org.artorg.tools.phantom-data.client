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

public class AddBooleanPropertyController extends AddEditController<BooleanProperty> {
	private ComboBox<PropertyField> comboBoxPropertyField;
	private CheckBox checkBoxValue;

	{
		comboBoxPropertyField = new ComboBox<PropertyField>();
		checkBoxValue = new CheckBox();
	}
	
	@Override
	public BooleanProperty createItem() {
		PropertyField propertyField = comboBoxPropertyField.getSelectionModel().getSelectedItem();
		Boolean value = checkBoxValue.isSelected();
		return new BooleanProperty(propertyField, value);
	}

	@Override
	protected HttpConnectorSpring<BooleanProperty> getConnector() {
		return BooleanPropertyConnector.get();
	}

	@Override
	protected void addPropertyEntries(List<PropertyEntry> entries) {
		createComboBox(comboBoxPropertyField, PropertyFieldConnector.get(), d -> String.valueOf(d.getName()));
		entries.add(new PropertyEntry("Property Field", comboBoxPropertyField));
		entries.add(new PropertyEntry("Value", checkBoxValue));
	}
	
	@Override
	protected void setTemplate(BooleanProperty item) {
		super.selectComboBoxItem(comboBoxPropertyField, item.getPropertyField());
		checkBoxValue.setSelected(item.getValue());
	}

	@Override
	protected void copy(BooleanProperty from, BooleanProperty to) {
		to.setPropertyField(from.getPropertyField());
		to.setValue(from.getValue());
	}

}