package org.artorg.tools.phantomData.client.controllers.editTable;

import java.util.List;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.FileTypeConnector;
import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.server.model.FileType;

import javafx.scene.control.TextField;

public class AddFileTypeController extends AddEditController<FileType> {
	private TextField textFieldName;
	
	{
		textFieldName = new TextField();
	}

	@Override
	public FileType createItem() {
		String name = textFieldName.getText();
		return new FileType(name);
	}

	@Override
	protected HttpConnectorSpring<FileType> getConnector() {
		return FileTypeConnector.get();
	}

	@Override
	protected void addPropertyEntries(List<PropertyEntry> entries) {
		entries.add(new PropertyEntry("Name", textFieldName));
	}

	@Override
	protected void setTemplate(FileType item) {
		textFieldName.setText(item.getName());
	}

	@Override
	protected void copy(FileType from, FileType to) {
		to.setName(from.getName());
	}

}
