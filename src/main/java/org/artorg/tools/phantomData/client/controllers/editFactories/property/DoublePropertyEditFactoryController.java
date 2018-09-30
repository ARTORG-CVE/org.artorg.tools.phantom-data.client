package org.artorg.tools.phantomData.client.controllers.editFactories.property;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connectors.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoEditFilterTableView;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;
import org.artorg.tools.phantomData.server.model.property.DoubleProperty;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class DoublePropertyEditFactoryController extends GroupedItemEditFactoryController<DoubleProperty> {
	private DbUndoRedoEditFilterTableView<DoubleProperty> table;
	private ComboBox<PropertyField> comboBoxPropertyField;
	private TextField textFieldValue;
	
	{
		comboBoxPropertyField = new ComboBox<PropertyField>();
		textFieldValue = new TextField();
	}
	
	public DoublePropertyEditFactoryController(DbUndoRedoEditFilterTableView<DoubleProperty> table) {
		this.table = table;
	}
	
	@Override
	public DoubleProperty createItem() {
		PropertyField propertyField = comboBoxPropertyField.getSelectionModel().getSelectedItem();
		Double value = Double.valueOf(textFieldValue.getText());
		return new DoubleProperty(propertyField, value);
	}
	
	@Override
	protected void setTemplate(DoubleProperty item) {
		super.selectComboBoxItem(comboBoxPropertyField, item.getPropertyField());
		textFieldValue.setText(Double.toString(item.getValue()));
	}

	@Override
	protected void copy(DoubleProperty from, DoubleProperty to) {
		to.setPropertyField(from.getPropertyField());
		to.setValue(from.getValue());
	}
	
	@Override
	protected DbUndoRedoEditFilterTableView<DoubleProperty> getTable() {
		return table;
	}

	@Override
	protected List<TitledPane> createGroupedProperties(DoubleProperty item) {
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
