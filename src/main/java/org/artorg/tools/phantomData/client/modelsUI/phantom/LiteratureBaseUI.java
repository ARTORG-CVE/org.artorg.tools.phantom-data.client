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
import org.artorg.tools.phantomData.server.models.phantom.LiteratureBase;

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
		ColumnCreator<LiteratureBase, LiteratureBase> creator = new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Shortcut", path -> path.getShortcut(),
				(path, value) -> path.setShortcut((String) value)));
		columns.add(creator.createFilterColumn("Value", path -> path.getValue(),
				(path, value) -> path.setValue((String) value)));
		createCountingColumn(table, "Files", columns, item -> item.getFiles());
		createCountingColumn(table, "Notes", columns, item -> item.getNotes());
		createPropertyColumns(table, columns, items);
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditor<LiteratureBase> createEditFactory() {
		ItemEditor<LiteratureBase> creator = new ItemEditor<LiteratureBase>(getItemClass()) {

			@Override
			public void createPropertyGridPanes(Creator<LiteratureBase> creator) {
				creator.createTextField(item -> item.getShortcut(),
						(item, value) -> item.setShortcut(value)).addLabeled("Shortcut");
				creator.createTextField(item -> item.getValue(), (item, value) -> item.setValue(value))
						.addLabeled("Name");
				creator.addTitledPropertyPane("General");
			}

			@Override
			public void createSelectors(Creator<LiteratureBase> creator) {
				creator.addSelector("Files", DbFile.class, item -> item.getFiles(),
						(item, files) -> item.setFiles((List<DbFile>) files));
			}

		};
		creator.addApplyButton();
		return creator;
	}

}
