package org.artorg.tools.phantomData.client.connectors;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.server.controller.PhantomController;
import org.artorg.tools.phantomData.server.model.Phantom;

public class PhantomConnector2 extends HttpConnectorSpring<Phantom> {
	
	private static final PhantomConnector2 connector;
	
	static {
		connector = new PhantomConnector2();
	}
	
	public static PhantomConnector2 get() {
		return connector;
	}
	
	private PhantomConnector2() {}
	
	@Override
	public Class<?> getControllerClass() {
		return PhantomController.class;
	}

}
