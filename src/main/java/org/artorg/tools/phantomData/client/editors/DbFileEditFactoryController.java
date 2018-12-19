package org.artorg.tools.phantomData.client.editors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.editor.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.server.models.base.DbFile;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.stage.FileChooser;

public class DbFileEditFactoryController
	extends GroupedItemEditFactoryController<DbFile> {
	private TextField textFieldPath;
	private TextField textFieldName;
	private TextField textFieldExtension;
	private Button buttonFileChooser;

	{
		textFieldPath = new TextField();
		textFieldName = new TextField();
		textFieldExtension = new TextField();
		buttonFileChooser = new Button("Browse");

		textFieldPath.setDisable(true);
		textFieldName.setDisable(true);
		textFieldExtension.setDisable(true);

		buttonFileChooser.setOnAction(event -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open source file");
			File file = fileChooser.showOpenDialog(Main.getStage());
			textFieldPath.setText(file.getAbsolutePath());
			String[] splits = splitOffFileExtension(file.getName());
			textFieldName.setText(splits[0]);
			textFieldExtension.setText(splits[1]);
			textFieldPath.setDisable(false);
			textFieldName.setDisable(false);
			textFieldExtension.setDisable(false);
		});

		List<TitledPane> panes = new ArrayList<TitledPane>();
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("Choose File", buttonFileChooser));
		generalProperties.add(new PropertyEntry("Path", textFieldPath));
		generalProperties.add(new PropertyEntry("Name", textFieldName));
		generalProperties.add(new PropertyEntry("Extension", textFieldExtension));

		TitledPropertyPane generalPane =
			new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		setTitledPanes(panes);

		setItemFactory(this::createItem);
		setTemplateSetter(this::setEditTemplate);
		setChangeApplier(this::applyChanges);
	}

	private String[] splitOffFileExtension(String name) {
		int index = name.lastIndexOf('.');
		String[] splits = new String[2];
		splits[0] = name.substring(0, index);
		splits[1] = name.substring(index + 1, name.length());
		return splits;
	}

	@Override
	public DbFile createItem() {
		String path = textFieldPath.getText();
		String name = textFieldName.getText();
		String extension = textFieldExtension.getText();

		return new DbFile(new File(path), name, extension);
	}

	@Override
	protected void setEditTemplate(DbFile item) {
		textFieldPath.setText(item.getFile().getPath());
		textFieldPath.setDisable(true);
		textFieldName.setText(item.getName());
		textFieldName.setDisable(false);
		textFieldExtension.setText(item.getExtension());
		textFieldExtension.setDisable(false);
	}

	@Override
	protected void applyChanges(DbFile item) {
		String name = textFieldName.getText();
		String extension = textFieldExtension.getText();

		item.setName(name);
		item.setExtension(extension);
	}

}
