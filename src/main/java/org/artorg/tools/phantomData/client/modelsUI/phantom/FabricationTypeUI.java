package org.artorg.tools.phantomData.client.modelsUI.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyGridPane;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.DbFile;
import org.artorg.tools.phantomData.server.models.phantom.FabricationType;

public class FabricationTypeUI extends UIEntity<FabricationType> {

	public Class<FabricationType> getItemClass() {
		return FabricationType.class;
	}

	@Override
	public String getTableName() {
		return "Fabrication Types";
	}

	@Override
	public List<AbstractColumn<FabricationType, ?>> createColumns(Table<FabricationType> table,
			List<FabricationType> items) {
		List<AbstractColumn<FabricationType, ?>> columns = new ArrayList<>();
		ColumnCreator<FabricationType, FabricationType> editor = new ColumnCreator<>(table);
		columns.add(editor.createFilterColumn("Shortcut", path -> path.getShortcut(),
				(path, value) -> path.setShortcut(value)));
		columns.add(editor.createFilterColumn("Value", path -> path.getValue(),
				(path, value) -> path.setValue(value)));
		createCountingColumn(table, "Files", columns, item -> item.getFiles());
		createCountingColumn(table, "Notes", columns, item -> item.getNotes());
		createPropertyColumns(table, columns, items);
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditor<FabricationType> createEditFactory() {
		ItemEditor<FabricationType> editor = new ItemEditor<>(getItemClass());

		PropertyGridPane propertyPane = new PropertyGridPane();
		propertyPane.addEntry("Shortcut", editor.createTextField(item -> item.getShortcut(),
				(item, value) -> item.setShortcut(value)));
		propertyPane.addEntry("Name", editor.createTextField(item -> item.getValue(),
				(item, value) -> item.setValue(value)));
		propertyPane.autosizeColumnWidths();
		editor.add(new TitledPropertyPane("General", propertyPane));

		editor.add(new TitledPropertyPane("Files", editor.createSelector(DbFile.class,
				item -> item.getFiles(), (item, files) -> item.setFiles((List<DbFile>) files))));

		editor.addApplyButton();
		return editor;
	}

}
