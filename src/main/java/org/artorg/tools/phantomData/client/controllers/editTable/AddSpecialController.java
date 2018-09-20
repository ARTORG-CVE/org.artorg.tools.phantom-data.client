package org.artorg.tools.phantomData.client.controllers.editTable;

import java.util.List;

import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.server.model.Special;

import javafx.scene.control.TextField;

public class AddSpecialController extends AddEditController<Special> {
	private TableViewSpring<Special> table;
	private TextField textFieldShortcut; 
	
	{
		textFieldShortcut = new TextField();
	}
	
	public AddSpecialController(TableViewSpring<Special> table) {
		this.table = table;
	}

	@Override
	public Special createItem() {
		String shortcut = textFieldShortcut.getText();
		return new Special(shortcut);
	}

	@Override
	protected void addPropertyEntries(List<PropertyEntry> entries) {
		entries.add(new PropertyEntry("Shortcut", textFieldShortcut));
	}

	@Override
	protected void setTemplate(Special item) {
		textFieldShortcut.setText(item.getShortcut());
	}

	@Override
	protected void copy(Special from, Special to) {
		to.setShortcut(from.getShortcut());
		
		to.setBooleanProperties(from.getBooleanProperties());
		to.setDateProperties(from.getDateProperties());
		to.setDoubleProperties(from.getDoubleProperties());
		to.setIntegerProperties(from.getIntegerProperties());
		to.setStringProperties(from.getStringProperties());
	}

	@Override
	protected TableViewSpring<Special> getTable() {
		return table;
	}

}
