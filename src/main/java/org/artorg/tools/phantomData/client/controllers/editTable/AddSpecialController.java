package org.artorg.tools.phantomData.client.controllers.editTable;

import java.util.List;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.SpecialConnector;
import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.server.model.Special;
import org.artorg.tools.phantomData.server.model.property.PropertyContainer;

import javafx.scene.control.TextField;

public class AddSpecialController extends AddEditController<Special> {
	private TextField textFieldShortcut; 
	
	{
		textFieldShortcut = new TextField();
	}

	@Override
	public Special createItem() {
		String shortcut = textFieldShortcut.getText();
		PropertyContainer propertyContainer = new PropertyContainer();
		return new Special(shortcut, propertyContainer);
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

}
