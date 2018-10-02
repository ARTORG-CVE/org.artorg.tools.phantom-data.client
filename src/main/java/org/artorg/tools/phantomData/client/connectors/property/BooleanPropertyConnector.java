package org.artorg.tools.phantomData.client.connectors.property;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.server.controller.property.BooleanPropertyController;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

public class BooleanPropertyConnector extends HttpConnectorSpring<BooleanProperty> {

	private static final BooleanPropertyConnector connector;
	
	static {
		connector = new BooleanPropertyConnector();
	}
	
	public static BooleanPropertyConnector get() {
		return connector;
	}
	
	private BooleanPropertyConnector() {}
	
	@Override
	public Class<?> getControllerClass() {
		return BooleanPropertyController.class;
	}

	private final String annoStringReadByPropertyField;
	
	public final String getAnnoStringReadByPropertyField() {
		return annoStringReadByPropertyField;
	}
	
	{
		annoStringReadByPropertyField = super.getAnnotationStringRead("PROPERTY_FIELD");
	}
	
	public BooleanProperty readFirstByPropertyFieldName(PropertyField propertyField) {
		return readByAttribute(propertyField.getId(), getAnnoStringReadByPropertyField());
	}
	
}
