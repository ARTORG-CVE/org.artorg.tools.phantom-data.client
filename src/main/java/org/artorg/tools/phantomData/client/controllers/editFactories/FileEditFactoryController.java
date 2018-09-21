package org.artorg.tools.phantomData.client.controllers.editFactories;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connectors.FileTypeConnector;
import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.server.model.FileType;
import org.artorg.tools.phantomData.server.model.PhantomFile;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class FileEditFactoryController extends GroupedItemEditFactoryController<PhantomFile> {
	private TableViewSpring<PhantomFile> table;
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
	
	public FileEditFactoryController(TableViewSpring<PhantomFile> table) {
		this.table = table;
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

	@Override
	protected TableViewSpring<PhantomFile> getTable() {
		return table;
	}

	@Override
	protected List<TitledPane> createGroupedProperties() {
		List<TitledPane> panes = new ArrayList<TitledPane>();
		
		createComboBox(comboBoxFileType, FileTypeConnector.get(), d -> String.valueOf(d.getName()));
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("Path", textFieldPath));
		generalProperties.add(new PropertyEntry("Name", textFieldName));
		generalProperties.add(new PropertyEntry("Extension", textFieldExtension));
		generalProperties.add(new PropertyEntry("File Type", comboBoxFileType));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		
		return panes;
	}

}
