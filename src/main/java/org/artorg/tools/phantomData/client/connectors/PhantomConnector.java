package org.artorg.tools.phantomData.client.connectors;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.server.controller.PhantomController;
import org.artorg.tools.phantomData.server.model.Phantom;

public class PhantomConnector extends HttpConnectorSpring<Phantom> {
	
	private static final PhantomConnector connector;
	
	static {
		connector = new PhantomConnector();
	}
	
	public static PhantomConnector get() {
		return connector;
	}
	
	private PhantomConnector() {}
	
	@Override
	public Class<?> getControllerClass() {
		return PhantomController.class;
	}

}
