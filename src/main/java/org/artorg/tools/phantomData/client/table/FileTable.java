package org.artorg.tools.phantomData.client.table;

import java.util.Arrays;
import java.util.List;

import org.artorg.tools.phantomData.client.connector.FileConnector;
import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.specification.StageTable;
import org.artorg.tools.phantomData.server.model.FileType;
import org.artorg.tools.phantomData.server.model.PhantomFile;

public class FileTable extends StageTable<FileTable, PhantomFile, Integer> {

	@Override
	public HttpDatabaseCrud<PhantomFile, Integer> getConnector() {
		return FileConnector.get();
	}

	@Override
	public Object getValue(PhantomFile item, int col) {
		switch (col) {
			case 0: return item.getId();
			case 1: return item.getPath();
			case 2: return item.getName();
			case 3: return item.getExtension();
			case 4: return item.getFileType();
		}
		throw new IllegalArgumentException();
	}

	@Override
	public void setValue(PhantomFile item, int col, Object value) {
		switch (col) {
			case 0: item.setId((Integer) value); break;
			case 1: item.setPath((String) value); break;
			case 2: item.setName((String) value); break;
			case 3: item.setExtension((String) value); break;
			case 4: item.setFileType((FileType) value); break;
		}
		throw new IllegalArgumentException();
	}

	@Override
	public List<String> getColumnNames() {
		return Arrays.asList("id", "path", "name", "extension", "file type");
	}

}
