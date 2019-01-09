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
import org.artorg.tools.phantomData.server.models.phantom.Special;

import javafx.scene.control.TextField;

public class SpecialUI extends UIEntity<Special> {

	public Class<Special> getItemClass() {
		return Special.class;
	}

	@Override
	public String getTableName() {
		return "Specials";
	}

	@Override
	public List<AbstractColumn<Special, ?>> createColumns(Table<Special> table,
			List<Special> items) {
		List<AbstractColumn<Special, ?>> columns = new ArrayList<>();
		ColumnCreator<Special, Special> editor = new ColumnCreator<>(table);
		columns.add(editor.createFilterColumn("Shortcut", path -> path.getShortcut(),
				(path, value) -> path.setShortcut(value)));
		columns.add(editor.createFilterColumn("Description", path -> path.getDescription(),
				(path, value) -> path.setDescription(value)));
		createCountingColumn(table, "Files", columns, item -> item.getFiles());
		createPropertyColumns(table, columns, items);
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditor<Special> createEditFactory() {
		TextField textFieldShortcut = new TextField();
		ItemEditor<Special> editor = new ItemEditor<Special>(getItemClass()) {

			@Override
			public void onCreatingClient(Special item) throws InvalidUIInputException {
				checkShortcut();
			}

			@Override
			public void onUpdatingClient(Special item) throws InvalidUIInputException {
				checkShortcut();
			}

			private void checkShortcut() throws InvalidUIInputException {
				String shortcut = textFieldShortcut.getText();
				if (shortcut.isEmpty() || shortcut.length() > 1 ||  !Character.isLetter(shortcut.charAt(0)))
					throw new InvalidUIInputException(Special.class, "Shortcut has to be a letter");
			}
		};

		PropertyGridPane propertyPane = new PropertyGridPane();
		propertyPane.addEntry("Shortcut", editor.create(textFieldShortcut, item -> item.getShortcut(),
				(item, value) -> item.setShortcut(value.toUpperCase())));
		propertyPane.addEntry("Description", editor.createTextArea(item -> item.getDescription(),
				(item, value) -> item.setDescription(value)));
		editor.add(new TitledPropertyPane("General", propertyPane));

		editor.add(new TitledPropertyPane("Files",
				editor.createSelector(DbFile.class, item -> item.getFiles(),
						(item, subItems) -> item.setFiles((List<DbFile>) subItems))));
		editor.add(new TitledPropertyPane("Properties", editor.createPropertySelector()));

		editor.closeTitledNonGeneralPanes();
		editor.addAutoCloseOnNonGeneral();
		editor.addApplyButton();
		return editor;
	}

}
