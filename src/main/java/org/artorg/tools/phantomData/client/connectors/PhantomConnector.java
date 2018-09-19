package org.artorg.tools.phantomData.client.connectors;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.server.controller.PhantomController;
import org.artorg.tools.phantomData.server.model.Phantom;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class PhantomConnector extends HttpConnectorSpring<Phantom, Integer> {
	
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

	@Override
	public Class<?> getArrayModelClass() {
		return Phantom[].class;
	}

	@Override
	public Class<? extends DatabasePersistent<Integer>> getModelClass() {
		return Phantom.class;
	}
	

	

}
