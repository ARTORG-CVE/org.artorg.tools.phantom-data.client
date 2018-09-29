package org.artorg.tools.phantomData.client.controllers.editFactories.property;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connectors.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.client.scene.control.DbEditFilterTableView;
import org.artorg.tools.phantomData.server.model.property.DoubleProperty;
import org.artorg.tools.phantomData.server.model.property.IntegerProperty;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class IntegerPropertyEditFactoryController extends GroupedItemEditFactoryController<IntegerProperty> {
	private DbEditFilterTableView<IntegerProperty> table;
	private ComboBox<PropertyField> comboBoxPropertyField;
	private TextField textFieldValue;

	{
		comboBoxPropertyField = new ComboBox<PropertyField>();
		textFieldValue = new TextField();
	}
	
	public IntegerPropertyEditFactoryController(DbEditFilterTableView<IntegerProperty> table) {
		this.table = table;
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

	@Override
	protected DbEditFilterTableView<IntegerProperty> getTable() {
		return table;
	}
	
	@Override
	protected List<TitledPane> createGroupedProperties(IntegerProperty item) {
		List<TitledPane> panes = new ArrayList<TitledPane>();
		
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		createComboBox(comboBoxPropertyField, PropertyFieldConnector.get(), d -> String.valueOf(d.getName()));
		generalProperties.add(new PropertyEntry("Property Field", comboBoxPropertyField));
		generalProperties.add(new PropertyEntry("Value", textFieldValue));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		
		return panes;
	}

}