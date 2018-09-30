package org.artorg.tools.phantomData.client.connectors;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.server.controller.FileTypeController;
import org.artorg.tools.phantomData.server.model.FileType;

public class FileTypeConnector extends HttpConnectorSpring<FileType> {

	private static final FileTypeConnector connector;
	
	static {
		connector = new FileTypeConnector();
	}
	
	public static FileTypeConnector get() {
		return connector;
	}
	
	private FileTypeConnector() {}

	@Override
	public Class<?> getControllerClass() {
		return FileTypeController.class;
	}

	@Override
	public Class<?> getArrayModelClass() {
		return FileType[].class;
	}

	@Override
	public Class<FileType> getModelClass() {
		return FileType.class;
	}

}
