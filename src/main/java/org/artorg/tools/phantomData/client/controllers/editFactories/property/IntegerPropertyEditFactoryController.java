package org.artorg.tools.phantomData.client.controllers.editFactories.property;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.server.model.property.IntegerProperty;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class IntegerPropertyEditFactoryController extends GroupedItemEditFactoryController<IntegerProperty> {
	private ComboBox<PropertyField> comboBoxPropertyField;
	private TextField textFieldValue;

	{
		comboBoxPropertyField = new ComboBox<PropertyField>();
		textFieldValue = new TextField();
		
		List<TitledPane> panes = new ArrayList<TitledPane>();
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		createComboBox(comboBoxPropertyField, HttpConnectorSpring.getOrCreate(PropertyField.class), d -> String.valueOf(d.getName()));
		generalProperties.add(new PropertyEntry("Property Field", comboBoxPropertyField));
		generalProperties.add(new PropertyEntry("Value", textFieldValue));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		setTitledPanes(panes);
		
		setItemFactory(this::createItem);
		setTemplateSetter(this::setTemplate);
		setItemCopier(this::copy);
	}
	
	@Override
	public IntegerProperty createItem() {
		PropertyField propertyField = comboBoxPropertyField.getSelectionModel().getSelectedItem();
		Integer value = Integer.valueOf(textFieldValue.getText());
		return new IntegerProperty(propertyField, value);
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