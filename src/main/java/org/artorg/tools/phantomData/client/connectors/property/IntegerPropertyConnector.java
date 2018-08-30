package org.artorg.tools.phantomData.client.connectors.property;

import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.server.controller.property.IntegerPropertyController;
import org.artorg.tools.phantomData.server.model.property.IntegerProperty;
import org.artorg.tools.phantomData.server.model.property.PropertyField;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class IntegerPropertyConnector extends HttpDatabaseCrud<IntegerProperty, Integer> {

	private static final IntegerPropertyConnector connector;
	
	static {
		connector = new IntegerPropertyConnector();
	}
	
	public static IntegerPropertyConnector get() {
		return connector;
	}
	
	private IntegerPropertyConnector() {}
	
	@Override
	public Class<?> getControllerClass() {
		return IntegerPropertyController.class;
	}

	@Override
	public Class<?> getArrayModelClass() {
		return IntegerProperty[].class;
	}

	@Override
	public Class<? extends DatabasePersistent<Integer>> getModelClass() {
		return IntegerProperty.class;
	}

	private final String annoStringReadByPropertyField;
	
	public final String getAnnoStringReadByPropertyField() {
		return annoStringReadByPropertyField;
	}
	
	{
		annoStringReadByPropertyField = super.getAnnotationStringRead("PROPERTY_FIELD");
	}
	
	public IntegerProperty readFirstByPropertyFieldName(PropertyField propertyField) {
		return readByAttribute(propertyField.getId(), getAnnoStringReadByPropertyField());
	}
	
}