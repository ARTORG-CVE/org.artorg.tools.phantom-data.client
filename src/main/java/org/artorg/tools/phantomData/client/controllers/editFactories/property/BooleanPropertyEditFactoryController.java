package org.artorg.tools.phantomData.client.controllers.editFactories.property;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connectors.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

public class BooleanPropertyEditFactoryController extends GroupedItemEditFactoryController<BooleanProperty> {
	private TableViewSpring<BooleanProperty> table;
	private ComboBox<PropertyField> comboBoxPropertyField;
	private CheckBox checkBoxValue;

	{
		comboBoxPropertyField = new ComboBox<PropertyField>();
		checkBoxValue = new CheckBox();
	}
	
	public BooleanPropertyEditFactoryController(TableViewSpring<BooleanProperty> table) {
		this.table = table;
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

	@Override
	protected TableViewSpring<BooleanProperty> getTable() {
		return table;
	}
	
	@Override
	protected List<TitledPropertyPane> createProperties() {
		List<TitledPropertyPane> panes = new ArrayList<TitledPropertyPane>();
		
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		createComboBox(comboBoxPropertyField, PropertyFieldConnector.get(), d -> String.valueOf(d.getName()));
		generalProperties.add(new PropertyEntry("Property Field", comboBoxPropertyField));
		generalProperties.add(new PropertyEntry("Value", checkBoxValue));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		
		return panes;
	}

}