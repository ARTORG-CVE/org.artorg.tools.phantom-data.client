package org.artorg.tools.phantomData.client.controllers.editFactories.property;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connector.PersonalizedHttpConnectorSpring;
import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TitledPane;

public class BooleanPropertyEditFactoryController extends GroupedItemEditFactoryController<BooleanProperty> {
	private ComboBox<PropertyField> comboBoxPropertyField;
	private CheckBox checkBoxValue;

	{
		comboBoxPropertyField = new ComboBox<PropertyField>();
		checkBoxValue = new CheckBox();
		
		List<TitledPane> panes = new ArrayList<TitledPane>();
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		createComboBox(comboBoxPropertyField, PersonalizedHttpConnectorSpring.getOrCreate(PropertyField.class), d -> String.valueOf(d.getName()));
		generalProperties.add(new PropertyEntry("Property Field", comboBoxPropertyField));
		generalProperties.add(new PropertyEntry("Value", checkBoxValue));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		setTitledPanes(panes);
		
		setItemFactory(this::createItem);
		setTemplateSetter(this::setTemplate);
		setItemCopier(this::copy);
	}
	
	@Override
	public BooleanProperty createItem() {
		PropertyField propertyField = comboBoxPropertyField.getSelectionModel().getSelectedItem();
		Boolean value = checkBoxValue.isSelected();
		return new BooleanProperty(propertyField, value);
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