package org.artorg.tools.phantomData.client.controllers.editTable;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.FileConnector;
import org.artorg.tools.phantomData.client.connectors.FileTypeConnector;
import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.server.model.FileType;
import org.artorg.tools.phantomData.server.model.PhantomFile;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class AddFileController extends AddEditController<PhantomFile, Integer> {
	TextField textFieldPath;
	TextField textFieldName;
	TextField textFieldExtension;
	ComboBox<FileType> comboBoxFileType;
	
	{
		textFieldPath = new TextField();
		textFieldName = new TextField();
		textFieldExtension = new TextField();
		comboBoxFileType = new ComboBox<FileType>();
		
		super.addProperty("Path", textFieldPath);
		super.addProperty("Name", textFieldName);
		super.addProperty("Extension", textFieldExtension);
		super.addProperty("File Type", comboBoxFileType);
		
		createComboBox(comboBoxFileType, FileTypeConnector.get(), d -> String.valueOf(d.getName()));
		
		super.create();
		
	}
	
	
	@Override
	public void initDefaultValues() {
		textFieldPath.setText("");
		textFieldName.setText("");
		textFieldExtension.setText("");
		comboBoxFileType.getSelectionModel().clearSelection();
	}

	@Override
	public PhantomFile createItem() {
		String path = textFieldPath.getText();
		String name = textFieldName.getText();
		String extension = textFieldExtension.getText();
		FileType fileType = comboBoxFileType.getSelectionModel().getSelectedItem();
		
		return new PhantomFile(path, name, extension, fileType);
	}

	@Override
	protected HttpConnectorSpring<PhantomFile, Integer> getConnector() {
		return FileConnector.get();
	}

}
