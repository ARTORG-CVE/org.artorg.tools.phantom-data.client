package org.artorg.tools.phantomData.client.controllers.editFactories.base.property;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.itemEdit.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.itemEdit.PropertyEntry;
import org.artorg.tools.phantomData.client.itemEdit.TitledPropertyPane;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.base.property.BooleanProperty;
import org.artorg.tools.phantomData.server.model.base.property.PropertyField;

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
		FxUtil.createDbComboBox(comboBoxPropertyField, Connectors.getConnector(PropertyField.class), d -> String.valueOf(d.getName()));
		generalProperties.add(new PropertyEntry("Property Field", comboBoxPropertyField));
		generalProperties.add(new PropertyEntry("Value", checkBoxValue));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		setTitledPanes(panes);
		
		setItemFactory(this::createItem);
		setTemplateSetter(this::setEditTemplate);
		setChangeApplier(this::applyChanges);
	}
	
	@Override
	public BooleanProperty createItem() {
		PropertyField propertyField = comboBoxPropertyField.getSelectionModel().getSelectedItem();
		Boolean value = checkBoxValue.isSelected();
		return new BooleanProperty(propertyField, value);
	}
	
	@Override
	protected void setEditTemplate(BooleanProperty item) {
		super.selectComboBoxItem(comboBoxPropertyField, item.getPropertyField());
		checkBoxValue.setSelected(item.getValue());
	}
	
	@Override
	protected void applyChanges(BooleanProperty item) {
		PropertyField propertyField = comboBoxPropertyField.getSelectionModel().getSelectedItem();
		Boolean value = checkBoxValue.isSelected();
    	
		item.setPropertyField(propertyField);
		item.setValue(value);
	}

}