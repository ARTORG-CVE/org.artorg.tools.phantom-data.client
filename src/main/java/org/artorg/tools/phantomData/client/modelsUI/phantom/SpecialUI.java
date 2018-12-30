package org.artorg.tools.phantomData.client.modelsUI.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.Creator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.DbFile;
import org.artorg.tools.phantomData.server.models.phantom.Special;

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
		ColumnCreator<Special, Special> creator = new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Shortcut", path -> path.getShortcut(),
				(path, value) -> path.setShortcut(value)));
		columns.add(creator.createFilterColumn("Description", path -> path.getDescription(),
				(path, value) -> path.setDescription(value)));
		createCountingColumn(table, "Files", columns, item -> item.getFiles());
		createCountingColumn(table, "Notes", columns, item -> item.getNotes());
		createPropertyColumns(table, columns, items);
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditor<Special> createEditFactory() {
		ItemEditor<Special> creator = new ItemEditor<Special>(getItemClass()) {

			@Override
			public void createPropertyGridPanes(Creator<Special> creator) {
				creator.createTextField(item -> item.getShortcut(),
						(item, value) -> item.setShortcut(value)).addLabeled("Shortcut");
				creator.createTextField(item -> item.getDescription(),
						(item, value) -> item.setDescription(value)).addLabeled("Description");
				creator.addTitledPropertyPane("General");
			}

			@Override
			public void createSelectors(Creator<Special> creator) {
				creator.addSelector("Files", DbFile.class, item -> item.getFiles(),
						(item, subItems) -> item.setFiles((List<DbFile>) subItems));
			}

		};
		creator.addApplyButton();
		return creator;
	}

}
