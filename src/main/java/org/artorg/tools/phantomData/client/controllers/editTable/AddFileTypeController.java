package org.artorg.tools.phantomData.client.controllers.editTable;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.FileTypeConnector;
import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.server.model.FileType;

import javafx.scene.control.TextField;

public class AddFileTypeController extends AddEditController<FileType, Integer> {
	TextField textFieldName;
	
	{
		textFieldName = new TextField();
		
		super.addProperty("Name", textFieldName);
		
		super.init();
	}
	
	
	@Override
	public void initDefaultValues() {
		textFieldName.setText("");
		
	}

	@Override
	public FileType createItem() {
		String name = textFieldName.getText();
		return new FileType(name);
	}

	@Override
	protected HttpConnectorSpring<FileType, Integer> getConnector() {
		return FileTypeConnector.get();
	}
	

}
