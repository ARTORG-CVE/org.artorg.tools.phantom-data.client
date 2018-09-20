package org.artorg.tools.phantomData.client.controllers.editTable;

import java.util.List;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.SpecialConnector;
import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.server.model.Special;

import javafx.scene.control.TextField;

public class AddSpecialController extends AddEditController<Special> {
	private TextField textFieldShortcut; 
	
	{
		textFieldShortcut = new TextField();
	}

	@Override
	public Special createItem() {
		String shortcut = textFieldShortcut.getText();
		return new Special(shortcut);
	}

	@Override
	protected HttpConnectorSpring<Special> getConnector() {
		return SpecialConnector.get();
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

}
