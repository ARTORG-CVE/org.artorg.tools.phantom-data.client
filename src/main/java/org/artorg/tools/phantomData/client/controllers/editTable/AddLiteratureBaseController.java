package org.artorg.tools.phantomData.client.controllers.editTable;

import java.util.function.BiFunction;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.LiteratureBaseConnector;
import org.artorg.tools.phantomData.client.controller.AddEditStringStringController;
import org.artorg.tools.phantomData.server.model.LiteratureBase;

public class AddLiteratureBaseController extends AddEditStringStringController<LiteratureBase, Integer> {
	
	public AddLiteratureBaseController() {
		super("Shortcut", "Value");
	}

	@Override
	protected HttpConnectorSpring<LiteratureBase, Integer> getConnector() {
		return LiteratureBaseConnector.get();
	}

	@Override
	public BiFunction<String, String, LiteratureBase> getItemConstructor() {
		return LiteratureBase::new;
	}	

}
