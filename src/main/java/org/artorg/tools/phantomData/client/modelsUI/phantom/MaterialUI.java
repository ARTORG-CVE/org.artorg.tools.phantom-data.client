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
import org.artorg.tools.phantomData.server.models.phantom.Material;

public class MaterialUI extends UIEntity<Material> {

	@Override
	public Class<Material> getItemClass() {
		return Material.class;
	}

	@Override
	public String getTableName() {
		return "Materials";
	}

	@Override
	public List<AbstractColumn<Material, ? extends Object>> createColumns(Table<Material> table,
			List<Material> items) {
		List<AbstractColumn<Material, ?>> columns = new ArrayList<>();
		ColumnCreator<Material, Material> editor = new ColumnCreator<>(table);
		columns.add(editor.createFilterColumn("Name", path -> path.getName(),
				(path, value) -> path.setName(value)));
		columns.add(editor.createFilterColumn("Description", path -> path.getDescription(),
				(path, value) -> path.setDescription(value)));
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditor<Material> createEditFactory() {
		ItemEditor<Material> editor = new ItemEditor<>(getItemClass());

		PropertyGridPane propertyPane = new PropertyGridPane();
		propertyPane.addEntry("Name", editor.createTextField(item -> item.getName(),
				(item, value) -> item.setName(value)));
		propertyPane.addEntry("Description", editor.createTextArea(item -> item.getDescription(),
				(item, value) -> item.setDescription(value)));
		propertyPane.autosizeColumnWidths();
		editor.add(new TitledPropertyPane("General", propertyPane));

		editor.add(new TitledPropertyPane("Files",
				editor.createSelector(DbFile.class, item -> item.getFiles(),
						(item, subItems) -> item.setFiles((List<DbFile>) subItems))));

		editor.addApplyButton();
		return editor;
	}

}
