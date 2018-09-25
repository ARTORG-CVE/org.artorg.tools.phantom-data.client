package org.artorg.tools.phantomData.client.connectors;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.server.controller.AnnulusDiameterController;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class AnnulusDiameterConnector extends HttpConnectorSpring<AnnulusDiameter> {
	private static final AnnulusDiameterConnector connector;
	
	static {
		connector = new AnnulusDiameterConnector();
	}
	
	public static AnnulusDiameterConnector get() {
		return connector;
	}
	
	private AnnulusDiameterConnector() {}
	
	@Override
	public Class<?> getControllerClass() {
		return AnnulusDiameterController.class;
	}

	@Override
	public Class<?> getArrayModelClass() {
		return AnnulusDiameter[].class;
	}

	@Override
	public Class<AnnulusDiameter> getModelClass() {
		return AnnulusDiameter.class;
	}
	
	private final String annoStringReadByShortcut;
	
	
	public final String getAnnoStringReadByShortcut() {
		return annoStringReadByShortcut;
	}
	
	{
		annoStringReadByShortcut = super.getAnnotationStringRead("SHORTCUT");
	}
	
	public AnnulusDiameter readByShortcut(Integer shortcut) {
		return readByAttribute(shortcut, getAnnoStringReadByShortcut());
	}

}
