package org.artorg.tools.phantomData.client.connector;

import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.server.controller.FileTypeController;
import org.artorg.tools.phantomData.server.model.FileType;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class FileTypeConnector extends HttpDatabaseCrud<FileType, Integer> {

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
	public Class<? extends DatabasePersistent<FileType, Integer>> getModelClass() {
		return FileType.class;
	}

}
