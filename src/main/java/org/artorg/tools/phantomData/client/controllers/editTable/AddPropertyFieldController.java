package org.artorg.tools.phantomData.client.controllers.editTable;

import java.util.List;

import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

import javafx.scene.control.TextField;

public class AddPropertyFieldController extends AddEditController<PropertyField>{
	private TableViewSpring<PropertyField> table;
	private TextField textFielName;
	private TextField textFieldDescription;

	{
		textFielName = new TextField();
		textFieldDescription = new TextField();
	}
	
	public AddPropertyFieldController(TableViewSpring<PropertyField> table) {
		this.table = table;
	}
	
	@Override
	protected void addPropertyEntries(List<PropertyEntry> entries) {
		entries.add(new PropertyEntry("Name", textFielName));
		entries.add(new PropertyEntry("Description", textFieldDescription));
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

}