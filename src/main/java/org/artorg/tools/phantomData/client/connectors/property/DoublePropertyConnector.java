package org.artorg.tools.phantomData.client.connectors.property;

import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.server.controller.property.DoublePropertyController;
import org.artorg.tools.phantomData.server.model.property.DoubleProperty;
import org.artorg.tools.phantomData.server.model.property.PropertyField;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class DoublePropertyConnector extends HttpDatabaseCrud<DoubleProperty, Integer> {

	private static final DoublePropertyConnector connector;
	
	static {
		connector = new DoublePropertyConnector();
	}
	
	public static DoublePropertyConnector get() {
		return connector;
	}
	
	private DoublePropertyConnector() {}
	
	@Override
	public Class<?> getControllerClass() {
		return DoublePropertyController.class;
	}

	@Override
	public Class<?> getArrayModelClass() {
		return DoubleProperty[].class;
	}

	@Override
	public Class<? extends DatabasePersistent<Integer>> getModelClass() {
		return DoubleProperty.class;
	}

	private final String annoStringReadByPropertyField;
	
	public final String getAnnoStringReadByPropertyField() {
		return annoStringReadByPropertyField;
	}
	
	{
		annoStringReadByPropertyField = super.getAnnotationStringRead("PROPERTY_FIELD");
	}
	
	public DoubleProperty readFirstByPropertyFieldName(PropertyField propertyField) {
		return readByAttribute(propertyField.getId(), getAnnoStringReadByPropertyField());
	}
	
}
