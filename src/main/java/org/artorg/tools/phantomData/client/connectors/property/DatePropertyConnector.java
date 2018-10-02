package org.artorg.tools.phantomData.client.connectors.property;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.server.controller.property.DatePropertyController;
import org.artorg.tools.phantomData.server.model.property.DateProperty;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

public class DatePropertyConnector extends HttpConnectorSpring<DateProperty> {

	private static final DatePropertyConnector connector;
	
	static {
		connector = new DatePropertyConnector();
	}
	
	public static DatePropertyConnector get() {
		return connector;
	}
	
	private DatePropertyConnector() {}
	
	@Override
	public Class<?> getControllerClass() {
		return DatePropertyController.class;
	}
	
	private final String annoStringReadByPropertyField;
	
	public final String getAnnoStringReadByPropertyField() {
		return annoStringReadByPropertyField;
	}
	
	{
		annoStringReadByPropertyField = super.getAnnotationStringRead("PROPERTY_FIELD");
	}
	
	public DateProperty readFirstByPropertyFieldName(PropertyField propertyField) {
		return readByAttribute(propertyField.getId(), getAnnoStringReadByPropertyField());
	}

}
