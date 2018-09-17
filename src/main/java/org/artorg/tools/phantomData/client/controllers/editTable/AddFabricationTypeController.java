package org.artorg.tools.phantomData.client.controllers.editTable;

import java.util.function.BiFunction;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.FabricationTypeConnector;
import org.artorg.tools.phantomData.client.controller.AddEditStringStringController;
import org.artorg.tools.phantomData.server.model.FabricationType;

public class AddFabricationTypeController extends AddEditStringStringController<FabricationType, Integer> {

	public AddFabricationTypeController() {
		super("Shortcut", "Value");
	}

	@Override
	protected HttpConnectorSpring<FabricationType, Integer> getConnector() {
		return FabricationTypeConnector.get();
	}

	@Override
	public BiFunction<String, String, FabricationType> getItemConstructor() {
		return FabricationType::new;
	}

}
