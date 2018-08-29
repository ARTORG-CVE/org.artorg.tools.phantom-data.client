package org.artorg.tools.phantomData.client.connectors;

import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.server.controller.FileController;
import org.artorg.tools.phantomData.server.model.PhantomFile;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class FileConnector extends HttpDatabaseCrud<PhantomFile, Integer> {

	private static final FileConnector connector;
	
	static {
		connector = new FileConnector();
	}
	
	public static FileConnector get() {
		return connector;
	}
	
	private FileConnector() {}

	@Override
	public Class<?> getControllerClass() {
		return FileController.class;
	}

	@Override
	public Class<?> getArrayModelClass() {
		return PhantomFile[].class;
	}

	@Override
	public Class<? extends DatabasePersistent<Integer>> getModelClass() {
		return PhantomFile.class;
	}
	
	@Override
	public boolean create(PhantomFile phantomFile) {
		boolean check = super.create(phantomFile);
		phantomFile.updateNativeFileName();
		return check;
	}
	
	
	private final String annoStringReadByName;
	
	
	public final String getAnnoStringReadByName() {
		return annoStringReadByName;
	}
	
	{
		annoStringReadByName = super.getAnnotationStringRead("NAME");
	}
	
	public PhantomFile readByName(String name) {
		return readByAttribute(name, getAnnoStringReadByName());
	}

}
