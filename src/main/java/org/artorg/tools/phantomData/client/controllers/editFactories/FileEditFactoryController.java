package org.artorg.tools.phantomData.client.controllers.editFactories;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.boot.MainFx;
import org.artorg.tools.phantomData.client.connector.PersonalizedHttpConnectorSpring;
import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.server.model.FileType;
import org.artorg.tools.phantomData.server.model.PhantomFile;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.stage.FileChooser;

public class FileEditFactoryController
	extends GroupedItemEditFactoryController<PhantomFile> {
	private TextField textFieldPath;
	private TextField textFieldName;
	private TextField textFieldExtension;
	private ComboBox<FileType> comboBoxFileType;
	private Button buttonFileChooser;

	{
		textFieldPath = new TextField();
		textFieldName = new TextField();
		textFieldExtension = new TextField();
		comboBoxFileType = new ComboBox<FileType>();
		buttonFileChooser = new Button("Browse");

		textFieldPath.setDisable(true);
		textFieldName.setDisable(true);
		textFieldExtension.setDisable(true);

		buttonFileChooser.setOnAction(event -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open source file");
			File desktopDir = new File(
				System.getProperty("user.home") + "\\Desktop\\");
			fileChooser.setInitialDirectory(desktopDir);
			File file = fileChooser.showOpenDialog(MainFx.getStage());
			textFieldPath.setText(file.getAbsolutePath());
			String[] splits = splitOffFileExtension(file.getName());
			textFieldName.setText(splits[0]);
			textFieldExtension.setText(splits[1]);
			textFieldPath.setDisable(false);
			textFieldName.setDisable(false);
			textFieldExtension.setDisable(false);
		});

		List<TitledPane> panes = new ArrayList<TitledPane>();
		createComboBox(comboBoxFileType,
			PersonalizedHttpConnectorSpring.getOrCreate(FileType.class),
			d -> String.valueOf(d.getName()));
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("Choose File", buttonFileChooser));
		generalProperties.add(new PropertyEntry("Path", textFieldPath));
		generalProperties.add(new PropertyEntry("Name", textFieldName));
		generalProperties.add(new PropertyEntry("Extension", textFieldExtension));
		generalProperties.add(new PropertyEntry("File Type", comboBoxFileType));

		TitledPropertyPane generalPane =
			new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		setTitledPanes(panes);

		setItemFactory(this::createItem);
		setTemplateSetter(this::setTemplate);
		setItemCopier(this::copy);
	}

	private String[] splitOffFileExtension(String name) {
		int index = name.lastIndexOf('.');
		String[] splits = new String[2];
		splits[0] = name.substring(0, index);
		splits[1] = name.substring(index + 1, name.length());
		return splits;
	}

	@Override
	public PhantomFile createItem() {
		String path = textFieldPath.getText();
		String name = textFieldName.getText();
		String extension = textFieldExtension.getText();
		FileType fileType = comboBoxFileType.getSelectionModel()
			.getSelectedItem();

		return new PhantomFile(new File(path), name, extension, fileType);
	}

	@Override
	protected void setTemplate(PhantomFile item) {
		textFieldPath.setText("");
		textFieldName.setText(item.getName());
		textFieldExtension.setText(item.getExtension());
		super.selectComboBoxItem(comboBoxFileType, item.getFileType());
	}

	@Override
	protected void copy(PhantomFile from, PhantomFile to) {
		to.setExtension(from.getExtension());
		to.setFileType(from.getFileType());
		to.setName(from.getName());
	}

}
