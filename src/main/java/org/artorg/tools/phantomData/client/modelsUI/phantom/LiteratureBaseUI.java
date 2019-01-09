package org.artorg.tools.phantomData.client.modelsUI.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyGridPane;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.exceptions.InvalidUIInputException;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.DbFile;
import org.artorg.tools.phantomData.server.models.phantom.LiteratureBase;
import org.artorg.tools.phantomData.server.models.phantom.Special;

import javafx.scene.control.TextField;

public class LiteratureBaseUI extends UIEntity<LiteratureBase> {

	public Class<LiteratureBase> getItemClass() {
		return LiteratureBase.class;
	}

	@Override
	public String getTableName() {
		return "Literature Bases";
	}

	@Override
	public List<AbstractColumn<LiteratureBase, ?>> createColumns(Table<LiteratureBase> table,
			List<LiteratureBase> items) {
		List<AbstractColumn<LiteratureBase, ?>> columns = new ArrayList<>();
		ColumnCreator<LiteratureBase, LiteratureBase> editor = new ColumnCreator<>(table);
		columns.add(editor.createFilterColumn("Shortcut", path -> path.getShortcut(),
				(path, value) -> path.setShortcut((String) value)));
		columns.add(editor.createFilterColumn("Value", path -> path.getValue(),
				(path, value) -> path.setValue((String) value)));
		createCountingColumn(table, "Files", columns, item -> item.getFiles());
		createPropertyColumns(table, columns, items);
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditor<LiteratureBase> createEditFactory() {
		TextField textFieldShortcut = new TextField();
		ItemEditor<LiteratureBase> editor = new ItemEditor<LiteratureBase>(getItemClass()) {

			@Override
			public void onCreatingClient(LiteratureBase item) throws InvalidUIInputException {
				checkShortcut();
			}

			@Override
			public void onUpdatingClient(LiteratureBase item) throws InvalidUIInputException {
				checkShortcut();
			}

			private void checkShortcut() throws InvalidUIInputException {
				String shortcut = textFieldShortcut.getText();
				if (shortcut.isEmpty() || shortcut.length() > 1 ||  !Character.isLetter(shortcut.charAt(0)))
					throw new InvalidUIInputException(Special.class, "Shortcut has to be a letter");
			}

		};

		PropertyGridPane propertyPane = new PropertyGridPane();
		propertyPane.addEntry("Shortcut", editor.create(textFieldShortcut,
				item -> item.getShortcut(), (item, value) -> item.setShortcut(value.toUpperCase())));
		propertyPane.addEntry("Name", editor.createTextField(item -> item.getValue(),
				(item, value) -> item.setValue(value)));
		editor.add(new TitledPropertyPane("General", propertyPane));

		editor.add(new TitledPropertyPane("Files", editor.createSelector(DbFile.class,
				item -> item.getFiles(), (item, files) -> item.setFiles((List<DbFile>) files))));
		editor.add(new TitledPropertyPane("Properties", editor.createPropertySelector()));

		editor.closeTitledNonGeneralPanes();
		editor.addAutoCloseOnNonGeneral();
		editor.addApplyButton();
		return editor;
	}

}
