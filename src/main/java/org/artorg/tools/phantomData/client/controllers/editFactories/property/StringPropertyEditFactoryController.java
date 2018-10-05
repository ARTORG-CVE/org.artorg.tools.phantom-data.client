package org.artorg.tools.phantomData.client.controllers.editFactories.property;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.server.model.property.PropertyField;
import org.artorg.tools.phantomData.server.model.property.StringProperty;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class StringPropertyEditFactoryController extends GroupedItemEditFactoryController<StringProperty> {
	private ComboBox<PropertyField> comboBoxPropertyField;
	private TextField textFieldValue;
	
	{
		comboBoxPropertyField = new ComboBox<PropertyField>();
		textFieldValue = new TextField();
	}
	
	@Override
	public StringProperty createItem() {
		PropertyField propertyField = comboBoxPropertyField.getSelectionModel().getSelectedItem();
		String value = textFieldValue.getText();
		return new StringProperty(propertyField, value);
	}

	@Override
	protected void setTemplate(StringProperty item) {
		super.selectComboBoxItem(comboBoxPropertyField, item.getPropertyField());
		textFieldValue.setText(item.getValue());
	}
	
	@Override
	protected void copy(StringProperty from, StringProperty to) {
		to.setPropertyField(from.getPropertyField());
		to.setValue(from.getValue());
	}
	
	@Override
	protected List<TitledPane> createGroupedProperties(StringProperty item) {
		List<TitledPane> panes = new ArrayList<TitledPane>();
		
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		createComboBox(comboBoxPropertyField, HttpConnectorSpring.getOrCreate(PropertyField.class), d -> String.valueOf(d.getName()));
		generalProperties.add(new PropertyEntry("Property Field", comboBoxPropertyField));
		generalProperties.add(new PropertyEntry("Value", textFieldValue));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		
		return panes;
	}

}