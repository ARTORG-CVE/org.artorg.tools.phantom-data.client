package org.artorg.tools.phantomData.client.controllers.editTable;

import java.util.function.BiFunction;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.controller.AddEditStringStringController;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

public class AddPropertyFieldController extends AddEditStringStringController<PropertyField, Integer>{

	public AddPropertyFieldController() {
		super("Name", "Description");
	}

	@Override
	public BiFunction<String, String, PropertyField> getItemConstructor() {
		return PropertyField::new;
	}

	@Override
	protected HttpConnectorSpring<PropertyField, Integer> getConnector() {
		return PropertyFieldConnector.get();
	}

}
