package org.artorg.tools.phantomData.client.controllers.editFactories.base.property;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connector.PersonalizedHttpConnectorSpring;
import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.base.property.DoubleProperty;
import org.artorg.tools.phantomData.server.model.base.property.PropertyField;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class DoublePropertyEditFactoryController extends GroupedItemEditFactoryController<DoubleProperty> {
	private ComboBox<PropertyField> comboBoxPropertyField;
	private TextField textFieldValue;
	
	{
		comboBoxPropertyField = new ComboBox<PropertyField>();
		textFieldValue = new TextField();
		
		List<TitledPane> panes = new ArrayList<TitledPane>();
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		FxUtil.createDbComboBox(comboBoxPropertyField, PersonalizedHttpConnectorSpring.getOrCreate(PropertyField.class), d -> String.valueOf(d.getName()));
		generalProperties.add(new PropertyEntry("Property Field", comboBoxPropertyField));
		generalProperties.add(new PropertyEntry("Value", textFieldValue));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		setTitledPanes(panes);
		
		setItemFactory(this::createItem);
		setTemplateSetter(this::setEditTemplate);
		setChangeApplier(this::applyChanges);
	}
	
	@Override
	public DoubleProperty createItem() {
		PropertyField propertyField = comboBoxPropertyField.getSelectionModel().getSelectedItem();
		Double value = Double.valueOf(textFieldValue.getText());
		return new DoubleProperty(propertyField, value);
	}
	
	@Override
	protected void setEditTemplate(DoubleProperty item) {
		super.selectComboBoxItem(comboBoxPropertyField, item.getPropertyField());
		textFieldValue.setText(Double.toString(item.getValue()));
	}

	@Override
	protected void applyChanges(DoubleProperty item) {
		PropertyField propertyField = comboBoxPropertyField.getSelectionModel().getSelectedItem();
		Double value = Double.valueOf(textFieldValue.getText());
    	
		item.setPropertyField(propertyField);
		item.setValue(value);
	}

}
