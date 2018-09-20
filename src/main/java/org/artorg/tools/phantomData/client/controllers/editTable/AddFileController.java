package org.artorg.tools.phantomData.client.controllers.editTable;

import java.util.List;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.FileConnector;
import org.artorg.tools.phantomData.client.connectors.FileTypeConnector;
import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.server.model.FileType;
import org.artorg.tools.phantomData.server.model.PhantomFile;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class AddFileController extends AddEditController<PhantomFile> {
	private TextField textFieldPath;
	private TextField textFieldName;
	private TextField textFieldExtension;
	private ComboBox<FileType> comboBoxFileType;
	
	{
		textFieldPath = new TextField();
		textFieldName = new TextField();
		textFieldExtension = new TextField();
		comboBoxFileType = new ComboBox<FileType>();
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
	protected HttpConnectorSpring<PhantomFile> getConnector() {
		return FileConnector.get();
	}

	@Override
	protected void addPropertyEntries(List<PropertyEntry> entries) {
		createComboBox(comboBoxFileType, FileTypeConnector.get(), d -> String.valueOf(d.getName()));
		entries.add(new PropertyEntry("Path", textFieldPath));
		entries.add(new PropertyEntry("Name", textFieldName));
		entries.add(new PropertyEntry("Extension", textFieldExtension));
		entries.add(new PropertyEntry("File Type", comboBoxFileType));
	}

	@Override
	protected void setTemplate(PhantomFile item) {
		textFieldPath.setText(item.getPath());
		textFieldName.setText(item.getName());
		textFieldExtension.setText(item.getExtension());
		super.selectComboBoxItem(comboBoxFileType, item.getFileType());
	}

	@Override
	protected void copy(PhantomFile from, PhantomFile to) {
		to.setExtension(from.getExtension());
		to.setFileType(from.getFileType());
		to.setName(from.getName());
		to.setPath(from.getPath());
	}

}
