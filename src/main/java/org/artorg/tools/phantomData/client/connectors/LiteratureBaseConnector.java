package org.artorg.tools.phantomData.client.connectors;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.server.controller.LiteratureBaseController;
import org.artorg.tools.phantomData.server.model.LiteratureBase;

public class LiteratureBaseConnector extends HttpConnectorSpring<LiteratureBase> {
	
	private static final LiteratureBaseConnector connector;
	
	static {
		connector = new LiteratureBaseConnector();
	}
	
	public static LiteratureBaseConnector get() {
		return connector;
	}
	
	private LiteratureBaseConnector() {}
	
	@Override
	public Class<?> getControllerClass() {
		return LiteratureBaseController.class;
	}
	
	private final String annoStringReadByShortcut;
	
	
	public final String getAnnoStringReadByShortcut() {
		return annoStringReadByShortcut;
	}
	
	{ 
		annoStringReadByShortcut = super.getAnnotationStringRead("SHORTCUT");
	}
	
	public LiteratureBase readByShortcut(String shortcut) {
		return readByAttribute(shortcut, getAnnoStringReadByShortcut());
	}

}
