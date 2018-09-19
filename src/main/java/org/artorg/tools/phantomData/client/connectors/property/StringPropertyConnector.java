package org.artorg.tools.phantomData.client.connectors.property;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.server.controller.property.StringPropertyController;
import org.artorg.tools.phantomData.server.model.property.PropertyField;
import org.artorg.tools.phantomData.server.model.property.StringProperty;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class StringPropertyConnector extends HttpConnectorSpring<StringProperty> {

	private static final StringPropertyConnector connector;
	
	static {
		connector = new StringPropertyConnector();
	}
	
	public static StringPropertyConnector get() {
		return connector;
	}
	
	private StringPropertyConnector() {}
	
	@Override
	public Class<?> getControllerClass() {
		return StringPropertyController.class;
	}

	@Override
	public Class<?> getArrayModelClass() {
		return StringProperty[].class;
	}

	@Override
	public Class<? extends DatabasePersistent> getModelClass() {
		return StringProperty.class;
	}

	private final String annoStringReadByPropertyField;
	
	public final String getAnnoStringReadByPropertyField() {
		return annoStringReadByPropertyField;
	}
	
	{
		annoStringReadByPropertyField = super.getAnnotationStringRead("PROPERTY_FIELD");
	}
	
	public StringProperty readFirstByPropertyFieldName(PropertyField propertyField) {
		return readByAttribute(propertyField.getId(), getAnnoStringReadByPropertyField());
	}
	
}
