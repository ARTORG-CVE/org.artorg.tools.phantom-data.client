package org.artorg.tools.phantomData.client.controllers.editTable.property;

import java.util.List;

import org.artorg.tools.phantomData.client.connectors.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.server.model.property.PropertyField;
import org.artorg.tools.phantomData.server.model.property.StringProperty;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class AddStringPropertyController extends AddEditController<StringProperty> {
	private TableViewSpring<StringProperty> table;
	private ComboBox<PropertyField> comboBoxPropertyField;
	private TextField textFieldValue;
	
	{
		comboBoxPropertyField = new ComboBox<PropertyField>();
		textFieldValue = new TextField();
	}
	
	public AddStringPropertyController(TableViewSpring<StringProperty> table) {
		this.table = table;
	}
	
	@Override
	public StringProperty createItem() {
		PropertyField propertyField = comboBoxPropertyField.getSelectionModel().getSelectedItem();
		String value = textFieldValue.getText();
		return new StringProperty(propertyField, value);
	}
	
	@Override
	protected void addPropertyEntries(List<PropertyEntry> entries) {
		createComboBox(comboBoxPropertyField, PropertyFieldConnector.get(), d -> String.valueOf(d.getName()));
		entries.add(new PropertyEntry("Property Field", comboBoxPropertyField));
		entries.add(new PropertyEntry("Value", textFieldValue));
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
	protected TableViewSpring<StringProperty> getTable() {
		return table;
	}

}