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
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.models.base.DbFile;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
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
				Image icon = FxUtil.getFileIcon(path.getFile());
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
//		columns.add(
//				new FilterColumn<>("Phantoms", path -> String.valueOf(path.getPhantoms().size())));
		createCountingColumn(table, "Notes", columns, item -> item.getNotes());
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditor<DbFile> createEditFactory() {
		TextField textFieldFilePath = new TextField();
		ItemEditor<DbFile> creator = new ItemEditor<DbFile>(getItemClass()) {
			@Override
			public void onCreatePostSuccessful(DbFile item) {
				item.putFile(new File(textFieldFilePath.getText()));
			}
		};
		VBox vBox = new VBox();

		List<PropertyEntry> entries = new ArrayList<>();

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
				String[] splits = splitOffFileExtension(file.getName());
				textFieldName.setText(splits[0]);
				textFieldExtension.setText(splits[1]);
				textFieldFilePath.setDisable(false);
				textFieldName.setDisable(false);
				textFieldExtension.setDisable(false);
			}
		});

		entries.add(new PropertyEntry("Choose File", buttonFileChooser));
		creator.createTextField(textFieldFilePath, item -> item.getFile().getPath(),
				(item, value) -> {}).addLabeled("File path", entries);
		creator.createTextField(textFieldName, item -> item.getName(),
				(item, value) -> item.setName(value)).addLabeled("Name", entries);
		creator.createTextField(textFieldExtension, item -> item.getExtension(),
				(item, value) -> item.setExtension(value))
				.setValueToNodeSetter(s -> textFieldExtension.setText(s.toLowerCase()))
				.addLabeled("Extension", entries);
		TitledPane generalPane = creator.createTitledPane(entries, "General");
		vBox.getChildren().add(generalPane);

		vBox.getChildren().add(creator.createButtonPane(creator.getApplyButton()));

		FxUtil.addToPane(creator, vBox);

		return creator;
	}

	private String[] splitOffFileExtension(String name) {
		int index = name.lastIndexOf('.');
		String[] splits = new String[2];
		splits[0] = name.substring(0, index);
		splits[1] = name.substring(index + 1, name.length());
		return splits;
	}

}
