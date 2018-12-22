package org.artorg.tools.phantomData.client.modelsUI.base;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.util.ColumnUtils;
import org.artorg.tools.phantomData.server.models.base.Note;

import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class NoteUI implements UIEntity<Note> {



	public Class<Note> getItemClass() {
		return Note.class;
	}

	@Override
	public String getTableName() {
		return "Notes";
	}

	@Override
	public List<AbstractColumn<Note, ?>> createColumns(List<Note> items) {
		List<AbstractColumn<Note, ?>> columns = new ArrayList<>();
		ColumnCreator<Note, Note> creator = new ColumnCreator<>(getItemClass());
		columns.add(creator.createFilterColumn("Name", path -> path.getName(),
				(path, value) -> path.setName(value)));
		ColumnUtils.createPersonifiedColumns(getItemClass(), columns);
		return columns;
	}

	@Override
	public ItemEditFactoryController<Note> createEditFactory() {
		return new NoteEditFactoryController();
	}

	private class NoteEditFactoryController extends GroupedItemEditFactoryController<Note> {
		private TextField textFieldMessage;

		{
			textFieldMessage = new TextField();

			List<TitledPane> panes = new ArrayList<TitledPane>();
			List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
			generalProperties.add(new PropertyEntry("Message", textFieldMessage));
			TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
			panes.add(generalPane);
			setTitledPanes(panes);
		}

		@Override
		public Note createItem() {
			String note = textFieldMessage.getText();
			return new Note(note);
		}

		@Override
		protected void setEditTemplate(Note item) {
			textFieldMessage.setText(item.getName());
		}

		@Override
		protected void applyChanges(Note item) {
			String message = textFieldMessage.getText();

			item.setName(message);
		}

	}

}
