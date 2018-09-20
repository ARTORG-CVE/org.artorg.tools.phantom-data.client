package org.artorg.tools.phantomData.client.controllers.editTable;

import java.util.List;

import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.server.model.FabricationType;

import javafx.scene.control.TextField;

public class AddFabricationTypeController extends AddEditController<FabricationType> {
	private TableViewSpring<FabricationType> table;
	private TextField textFieldShortcut;
	private TextField textFieldValue;

	{
		textFieldShortcut = new TextField();
		textFieldValue = new TextField();
	}
	
	public AddFabricationTypeController(TableViewSpring<FabricationType> table) {
		this.table = table;
	}
	
	@Override
	protected void addPropertyEntries(List<PropertyEntry> entries) {
		entries.add(new PropertyEntry("Shortcut", textFieldShortcut));
		entries.add(new PropertyEntry("Name", textFieldValue));
	}

	@Override
	protected void setTemplate(FabricationType item) {
		textFieldShortcut.setText(item.getShortcut());
		textFieldValue.setText(item.getValue());
	}

	@Override
	public FabricationType createItem() {
		String shortcut = textFieldShortcut.getText();
		String value = textFieldValue.getText();
		return new FabricationType(shortcut, value);
	}

	@Override
	protected void copy(FabricationType from, FabricationType to) {
		to.setShortcut(from.getShortcut());
		to.setValue(from.getValue());
	}

	@Override
	protected TableViewSpring<FabricationType> getTable() {
		return table;
	}

}
