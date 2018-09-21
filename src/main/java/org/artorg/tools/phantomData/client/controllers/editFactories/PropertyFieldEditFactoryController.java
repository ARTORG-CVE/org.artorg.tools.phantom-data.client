package org.artorg.tools.phantomData.client.controllers.editFactories;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

import javafx.scene.control.TextField;

public class PropertyFieldEditFactoryController extends GroupedItemEditFactoryController<PropertyField>{
	private TableViewSpring<PropertyField> table;
	private TextField textFielName;
	private TextField textFieldDescription;

	{
		textFielName = new TextField();
		textFieldDescription = new TextField();
	}
	
	public PropertyFieldEditFactoryController(TableViewSpring<PropertyField> table) {
		this.table = table;
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
	protected TableViewSpring<PropertyField> getTable() {
		return table;
	}

	@Override
	protected List<TitledPropertyPane> createProperties() {
		List<TitledPropertyPane> panes = new ArrayList<TitledPropertyPane>();
		
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("Name", textFielName));
		generalProperties.add(new PropertyEntry("Description", textFieldDescription));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		
		return panes;
	}

}