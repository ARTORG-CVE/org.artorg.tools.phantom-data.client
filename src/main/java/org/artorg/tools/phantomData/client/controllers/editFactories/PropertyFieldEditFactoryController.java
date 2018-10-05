package org.artorg.tools.phantomData.client.controllers.editFactories;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoAddEditControlFilterTableView;
import org.artorg.tools.phantomData.server.model.property.DoubleProperty;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class PropertyFieldEditFactoryController extends GroupedItemEditFactoryController<PropertyField>{
	private TextField textFielName;
	private TextField textFieldDescription;

	{
		textFielName = new TextField();
		textFieldDescription = new TextField();
	}

	@Override
	protected void setTemplate(PropertyField item) {
		textFielName.setText(item.getName());
		textFieldDescription.setText(item.getDescription());
	}

	@Override
	public PropertyField createItem() {
		String name = textFielName.getText();
		String description = textFieldDescription.getText();
		return new PropertyField(name, description);
	}

	@Override
	protected void copy(PropertyField from, PropertyField to) {
		to.setDescription(from.getDescription());
		to.setName(from.getName());
	}

	@Override
	protected List<TitledPane> createGroupedProperties(PropertyField item) {
		List<TitledPane> panes = new ArrayList<TitledPane>();
		
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("Name", textFielName));
		generalProperties.add(new PropertyEntry("Description", textFieldDescription));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		
		return panes;
	}

}