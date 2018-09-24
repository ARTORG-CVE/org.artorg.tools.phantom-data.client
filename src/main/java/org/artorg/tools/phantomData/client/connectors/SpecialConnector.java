package org.artorg.tools.phantomData.client.connectors;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.server.controller.SpecialController;
import org.artorg.tools.phantomData.server.model.Special;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class SpecialConnector extends HttpConnectorSpring<Special> {
	
	private static final SpecialConnector connector;
	
	static {
		connector = new SpecialConnector();
	}
	
	public static SpecialConnector get() {
		return connector;
	}
	
	private SpecialConnector() {}
	
	@Override
	public Class<?> getControllerClass() {
		return SpecialController.class;
	}

	@Override
	public Class<?> getArrayModelClass() {
		return Special[].class;
	}

	@Override
	public Class<Special> getModelClass() {
		return Special.class;
	}
	
	private final String annoStringReadByShortcut;
	
	
	public final String getAnnoStringReadByShortcut() {
		return annoStringReadByShortcut;
	}
	
	{ 
		annoStringReadByShortcut = super.getAnnotationStringRead("SHORTCUT");
	}
	
	public Special readByShortcut(String shortcut) {
		return readByAttribute(shortcut, getAnnoStringReadByShortcut());
	}

}
