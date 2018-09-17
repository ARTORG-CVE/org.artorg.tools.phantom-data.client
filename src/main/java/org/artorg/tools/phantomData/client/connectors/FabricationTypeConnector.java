package org.artorg.tools.phantomData.client.connectors;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.server.controller.FabricationTypeController;
import org.artorg.tools.phantomData.server.model.FabricationType;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class FabricationTypeConnector extends HttpConnectorSpring<FabricationType, Integer> {

	private static final FabricationTypeConnector connector;
	
	static {
		connector = new FabricationTypeConnector();
	}
	
	public static FabricationTypeConnector get() {
		return connector;
	}
	
	private FabricationTypeConnector() {}
	
	@Override
	public Class<?> getControllerClass() {
		return FabricationTypeController.class;
	}

	@Override
	public Class<?> getArrayModelClass() {
		return FabricationType[].class;
	}

	@Override
	public Class<? extends DatabasePersistent<Integer>> getModelClass() {
		return FabricationType.class;
	}
	
	private final String annoStringReadByShortcut;
	
	
	public final String getAnnoStringReadByShortcut() {
		return annoStringReadByShortcut;
	}
	
	{ 
		annoStringReadByShortcut = super.getAnnotationStringRead("SHORTCUT");
	}
	
	public FabricationType readByShortcut(String shortcut) {
		return readByAttribute(shortcut, getAnnoStringReadByShortcut());
	}

}
