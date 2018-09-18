package org.artorg.tools.phantomData.client.controllers.editTable;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.SpecialConnector;
import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.server.model.Special;
import org.artorg.tools.phantomData.server.model.property.PropertyContainer;

import javafx.scene.control.TextField;

public class AddSpecialController extends AddEditController<Special, Integer> {
	TextField textFieldShortcut; 
	
	{
		textFieldShortcut = new TextField();
		
		super.addProperty("Shortcut", textFieldShortcut);
		
		super.create();
	}
	

	@Override
	public void initDefaultValues() {
		textFieldShortcut.setText("");
		
	}

	@Override
	public Special createItem() {
		String shortcut = textFieldShortcut.getText();
		PropertyContainer propertyContainer = new PropertyContainer();
		
		return new Special(shortcut, propertyContainer);
	}

	@Override
	protected HttpConnectorSpring<Special, Integer> getConnector() {
		return SpecialConnector.get();
	}

}
