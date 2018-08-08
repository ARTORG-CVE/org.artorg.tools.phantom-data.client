package org.artorg.tools.phantomData.client.connector;

import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.server.controller.PhantomController;
import org.artorg.tools.phantomData.server.model.Phantom;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class PhantomConnector extends HttpDatabaseCrud<Phantom, Integer> {
	
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
	public Class<? extends DatabasePersistent<Phantom, Integer>> getModelClass() {
		return Phantom.class;
	}
	

	

}
