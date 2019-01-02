package org.artorg.tools.phantomData.client.modelsUI.base;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyGridPane;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.FileTag;

public class FileTagUI extends UIEntity<FileTag> {

	public Class<FileTag> getItemClass() {
		return FileTag.class;
	}

	@Override
	public String getTableName() {
		return "File Tags";
	}

	@Override
	public List<AbstractColumn<FileTag, ?>> createColumns(Table<FileTag> table,
			List<FileTag> items) {
		List<AbstractColumn<FileTag, ?>> columns = new ArrayList<AbstractColumn<FileTag, ?>>();
		ColumnCreator<FileTag, FileTag> creator = new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Name", path -> path.getName(),
				(path, value) -> path.setName(value)));
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditor<FileTag> createEditFactory() {
		ItemEditor<FileTag> editor = new ItemEditor<>(getItemClass());
		PropertyGridPane propertyPane = new PropertyGridPane();
		propertyPane.addEntry("Name", editor.createTextField(item -> item.getName(),
				(item, value) -> item.setName(value)));
		editor.add(new TitledPropertyPane("General", propertyPane));
		editor.addApplyButton();
		return editor;

	}

}
