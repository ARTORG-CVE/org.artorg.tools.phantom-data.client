package org.artorg.tools.phantomData.client.connectors.property;

import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.server.controller.property.PropertyFieldController;
import org.artorg.tools.phantomData.server.model.property.PropertyField;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class PropertyFieldConnector extends HttpDatabaseCrud<PropertyField, Integer> {

	private static final PropertyFieldConnector connector;
	
	static {
		connector = new PropertyFieldConnector();
	}
	
	public static PropertyFieldConnector get() {
		return connector;
	}
	
	private PropertyFieldConnector() {}
	
	@Override
	public Class<?> getControllerClass() {
		return PropertyFieldController.class;
	}

	@Override
	public Class<?> getArrayModelClass() {
		return PropertyField[].class;
	}

	@Override
	public Class<? extends DatabasePersistent<Integer>> getModelClass() {
		return PropertyField.class;
	}

	private final String annoStringReadByName;
	
	
	public final String getAnnoStringReadByName() {
		return annoStringReadByName;
	}
	
	{ 
		annoStringReadByName = super.getAnnotationStringRead("NAME");
	}
	
	public PropertyField readByName(String name) {
		return readByAttribute(name, getAnnoStringReadByName());
	}

}
