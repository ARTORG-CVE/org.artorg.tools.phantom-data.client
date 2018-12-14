package org.artorg.tools.phantomData.client.modelsUI.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.FilterColumn;
import org.artorg.tools.phantomData.client.editor.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.util.ColumnUtils;
import org.artorg.tools.phantomData.server.model.phantom.LiteratureBase;

import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class LiteratureBaseUI implements UIEntity<LiteratureBase> {

	@Override
	public Class<LiteratureBase> getItemClass() {
		return LiteratureBase.class;
	}

	@Override
	public String getTableName() {
		return "Literature Bases";
	}

	@Override
	public List<AbstractColumn<LiteratureBase, ?>> createColumns() {
		List<AbstractColumn<LiteratureBase, ?>> columns =
			new ArrayList<AbstractColumn<LiteratureBase, ?>>();
		columns.add(new FilterColumn<LiteratureBase, String>("Shortcut", item -> item,
			path -> path.getShortcut(),
			(path, value) -> path.setShortcut((String) value)));
		columns.add(new FilterColumn<LiteratureBase, String>("Value", item -> item,
			path -> path.getValue(), (path, value) -> path.setValue((String) value)));
		ColumnUtils.createCountingColumn("Files", columns, item -> item.getFiles());
		ColumnUtils.createCountingColumn("Notes", columns, item -> item.getNotes());
		ColumnUtils.createPersonifiedColumns(columns);
		return columns;
	}

	@Override
	public ItemEditFactoryController<LiteratureBase> createEditFactory() {
		return new LiteratureBaseEditFactoryController();
	}
	
	private class LiteratureBaseEditFactoryController extends GroupedItemEditFactoryController<LiteratureBase> {
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
			
			setItemFactory(this::createItem);
			setTemplateSetter(this::setEditTemplate);
			setChangeApplier(this::applyChanges);
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
		
	}

}
