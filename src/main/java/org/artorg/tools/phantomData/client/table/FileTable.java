package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.artorg.tools.phantomData.client.commandPattern.PropertyUndoable;
import org.artorg.tools.phantomData.client.connector.FileConnector;
import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.specification.StageTable;
import org.artorg.tools.phantomData.server.model.FabricationType;
import org.artorg.tools.phantomData.server.model.FileType;
import org.artorg.tools.phantomData.server.model.PhantomFile;

public class FileTable extends StageTable<FileTable, PhantomFile, Integer> {

	@Override
	public HttpDatabaseCrud<PhantomFile, Integer> getConnector() {
		return FileConnector.get();
	}

	@Override
	public List<String> createColumnNames() {
		return Arrays.asList("id", "path", "name", "extension", "file type");
	}

	@Override
	public List<PropertyUndoable<PhantomFile, Object>> createProperties() {
		List<PropertyUndoable<PhantomFile, Object>> properties = 
				new ArrayList<PropertyUndoable<PhantomFile, Object>>();
		properties.add(createProperty(
				(i,o) -> i.setId((Integer) o), 
				i -> i.getId()));
		properties.add(createProperty(
				(i,o) -> i.setPath((String) o), 
				i -> i.getPath()));
		properties.add(createProperty(
				(i,o) -> i.setName((String) o), 
				i -> i.getName()));
		properties.add(createProperty(
				(i,o) -> i.setExtension((String) o), 
				i -> i.getExtension()));
		properties.add(createProperty(
				(i,o) -> i.getFileType().setName((String) o), 
				i -> i.getFileType().getName()));
		return properties;
	}

}
