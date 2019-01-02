package org.artorg.tools.phantomData.client.modelsUI.base;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.AbstractFilterColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyGridPane;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.exceptions.InvalidUIInputException;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.scene.SelectableLabel;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.models.base.DbFile;
import org.artorg.tools.phantomData.server.models.base.FileTag;
import org.artorg.tools.phantomData.server.models.phantom.Phantomina;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class DbFileUI extends UIEntity<DbFile> {
	private static final Map<String, Image> iconMap = new HashMap<>();

	public Class<DbFile> getItemClass() {
		return DbFile.class;
	}

	@Override
	public String getTableName() {
		return "Files";
	}

	@Override
	public List<AbstractColumn<DbFile, ?>> createColumns(Table<DbFile> table, List<DbFile> items) {
		List<AbstractColumn<DbFile, ?>> columns = new ArrayList<>();
		ColumnCreator<DbFile, DbFile> creator = new ColumnCreator<>(table);
		AbstractFilterColumn<DbFile, ?> column;
		columns.add(creator.createColumn("", path -> {
			if (path.getExtension().isEmpty()) return null;
			if (iconMap.containsKey(path.getExtension()))
				return new ImageView(iconMap.get(path.getExtension()));
			else {
				Image icon = FxUtil.getFileIcon(path.createFile());
				iconMap.put(path.getExtension(), icon);
				return new ImageView(icon);
			}
		}));
		column = creator.createFilterColumn("Name", path -> path.getName(),
				(path, value) -> path.setName(value));
		column.setItemsFilter(false);
		columns.add(column);
		columns.add(creator.createFilterColumn("Extension", path -> path.getExtension(),
				(path, value) -> path.setExtension(value)));
		columns.add(creator.createFilterColumn("File Tags", path -> path.getFileTags().stream()
				.map(fileTag -> fileTag.getName()).collect(Collectors.joining(", "))));
		createCountingColumn(table, "Notes", columns, item -> item.getNotes());
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditor<DbFile> createEditFactory() {
		TextField textFieldFilePath = new TextField();
		SelectableLabel textFieldSwitch = new SelectableLabel();

		Label labelSwitch = new Label();
		TextField textFieldName = new TextField();
		TextField textFieldExtension = new TextField();
		Button buttonFileChooser = new Button("Browse");
		buttonFileChooser.setOnAction(event -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open source file");
			File file = null;
			try {
				file = fileChooser.showOpenDialog(Main.getStage());
			} catch (NullPointerException e) {}
			if (file != null) {
				textFieldFilePath.setText(file.getAbsolutePath());
				textFieldSwitch.setText(file.getAbsolutePath());
				String[] splits = splitOffFileExtension(file.getName());
				textFieldName.setText(splits[0]);
				textFieldExtension.setText(splits[1].toLowerCase());
			}
		});

		ItemEditor<DbFile> editor = new ItemEditor<DbFile>(getItemClass()) {

			@Override
			public void onShowingCreateMode(DbFile item) {
				labelSwitch.setText("File path");
				String path = textFieldFilePath.getText();
				String[] splits = splitOffFileExtension(path);
				textFieldSwitch.setText(splits[0]);
			}

			@Override
			public void onShowingEditMode(DbFile item) {
				labelSwitch.setText("Id");
				textFieldSwitch.setText(item.getId().toString());
			}

			@Override
			public void onCreatingClient(DbFile item) throws InvalidUIInputException {
				File file = new File(textFieldFilePath.getText());
				if (!file.exists()) throw new InvalidUIInputException(Phantomina.class,
						"File does not exist '" + file.getPath() + "'");
			}

		};

		PropertyGridPane propertyPane = new PropertyGridPane();
		propertyPane.addEntry(new Label(""), buttonFileChooser);
		editor.addPropertyNode(editor.create(textFieldFilePath, item -> item.createFile().getPath(),
				(item, value) -> item.putFile(new File(value))));
		propertyPane.addEntry(labelSwitch, textFieldSwitch);
		propertyPane.addEntry("Name", editor.create(textFieldName, item -> item.getName(),
				(item, value) -> item.setName(value)));
		propertyPane.addEntry("Extensison", editor.create(textFieldExtension,
				item -> item.getExtension(), (item, value) -> item.setExtension(value)));
		editor.add(new TitledPropertyPane("General", propertyPane));
		editor.add(new TitledPropertyPane("Files",
				editor.createSelector(FileTag.class, item -> item.getFileTags(),
						(item, files) -> item.setFileTags((List<FileTag>) files))));
		
		editor.closeTitledSelectors();
		editor.addAutoCloseOnSelectors();
		editor.addApplyButton();
		return editor;
	}

	private String[] splitOffFileExtension(String name) {
		if (name.isEmpty()) return new String[] { "", "" };
		int index = name.lastIndexOf('.');
		String[] splits = new String[2];
		splits[0] = name.substring(0, index);
		splits[1] = name.substring(index + 1, name.length());
		return splits;
	}

}
