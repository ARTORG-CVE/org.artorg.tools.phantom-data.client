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
import org.artorg.tools.phantomData.server.models.base.Note;

public class NoteUI extends UIEntity<Note> {

	public Class<Note> getItemClass() {
		return Note.class;
	}

	@Override
	public String getTableName() {
		return "Notes";
	}

	@Override
	public List<AbstractColumn<Note, ?>> createColumns(Table<Note> table, List<Note> items) {
		List<AbstractColumn<Note, ?>> columns = new ArrayList<>();
		ColumnCreator<Note, Note> creator = new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Name", path -> path.getName(),
				(path, value) -> path.setName(value)));
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditor<Note> createEditFactory() {
		ItemEditor<Note> editor = new ItemEditor<Note>(getItemClass());
		PropertyGridPane propertyPane = new PropertyGridPane();
		propertyPane.addEntry("Message", editor.createTextArea(item -> item.getName(),
				(item, value) -> item.setName(value)));
		propertyPane.autosizeColumnWidths();
		editor.add(new TitledPropertyPane("General", propertyPane));
		editor.addApplyButton();
		return editor;
	}

}
