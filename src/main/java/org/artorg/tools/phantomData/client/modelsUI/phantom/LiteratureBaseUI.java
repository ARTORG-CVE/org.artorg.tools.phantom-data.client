package org.artorg.tools.phantomData.client.modelsUI.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.phantom.LiteratureBase;

import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class LiteratureBaseUI extends UIEntity<LiteratureBase> {


	public Class<LiteratureBase> getItemClass() {
		return LiteratureBase.class;
	}
	
	@Override
	public String getTableName() {
		return "Literature Bases";
	}

	@Override
	public List<AbstractColumn<LiteratureBase, ?>> createColumns(Table<LiteratureBase> table, List<LiteratureBase> items) {
		List<AbstractColumn<LiteratureBase, ?>> columns = new ArrayList<>();
		ColumnCreator<LiteratureBase, LiteratureBase> creator =
				new ColumnCreator<>(table);
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
	public ItemEditFactoryController<LiteratureBase> createEditFactory() {
		return new LiteratureBaseEditFactoryController();
	}

	private class LiteratureBaseEditFactoryController
			extends GroupedItemEditFactoryController<LiteratureBase> {
		private TextField textFieldShortcut;
		private TextField textFieldValue;

		{
			textFieldShortcut = new TextField();
			textFieldValue = new TextField();

			List<TitledPane> panes = new ArrayList<TitledPane>();
			List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
			generalProperties.add(new PropertyEntry("Shortcut", textFieldShortcut));
			generalProperties.add(new PropertyEntry("Name", textFieldValue));
			TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
			panes.add(generalPane);
			setTitledPanes(panes);
		}

		@Override
		protected void setEditTemplate(LiteratureBase item) {
			textFieldShortcut.setText(item.getShortcut());
			textFieldValue.setText(item.getValue());
		}

		@Override
		public LiteratureBase createItem() {
			String shortcut = textFieldShortcut.getText();
			String value = textFieldValue.getText();
			return new LiteratureBase(shortcut, value);
		}

		@Override
		protected void applyChanges(LiteratureBase item) {
			String shortcut = textFieldShortcut.getText();
			String value = textFieldValue.getText();

			item.setShortcut(shortcut);
			item.setValue(value);
		}

		@Override
		public void setDefaultTemplate() {
			textFieldShortcut.setText("");
			textFieldValue.setText("");
		}

	}

}
