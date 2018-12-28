package org.artorg.tools.phantomData.client.modelsUI.base;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.Note;
import org.artorg.tools.phantomData.server.util.FxUtil;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

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
		ItemEditor<Note> creator = new ItemEditor<>(getItemClass());
		VBox vBox = new VBox();

		List<PropertyEntry> entries = new ArrayList<>();
		creator.createTextField((item, value) -> item.setName(value), item -> item.getName())
				.addLabeled("Message", entries);
		TitledPane generalPane = creator.createTitledPane(entries, "General");
		vBox.getChildren().add(generalPane);

		vBox.getChildren().add(creator.createButtonPane(creator.getApplyButton()));

		FxUtil.addToPane(creator, vBox);
		return creator;
	}

}
