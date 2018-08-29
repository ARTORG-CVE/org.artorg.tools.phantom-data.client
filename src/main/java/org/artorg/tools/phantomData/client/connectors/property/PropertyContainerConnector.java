package org.artorg.tools.phantomData.client.connectors.property;

import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.server.controller.property.PropertyContainerController;
import org.artorg.tools.phantomData.server.model.property.PropertyContainer;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class PropertyContainerConnector extends HttpDatabaseCrud<PropertyContainer, Integer> {

	private static final PropertyContainerConnector connector;
	
	static {
		connector = new PropertyContainerConnector();
	}
	
	public static PropertyContainerConnector get() {
		return connector;
	}
	
	private PropertyContainerConnector() {}
	
	@Override
	public Class<?> getControllerClass() {
		return PropertyContainerController.class;
	}

	@Override
	public Class<?> getArrayModelClass() {
		return PropertyContainer[].class;
	}

	@Override
	public Class<? extends DatabasePersistent<Integer>> getModelClass() {
		return PropertyContainer.class;
	}
	
	

}
