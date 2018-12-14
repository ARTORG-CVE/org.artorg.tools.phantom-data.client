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
import org.artorg.tools.phantomData.server.model.phantom.Special;

import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class SpecialUI implements UIEntity<Special> {

	@Override
	public Class<Special> getItemClass() {
		return Special.class;
	}

	@Override
	public String getTableName() {
		return "Specials";
	}

	@Override
	public List<AbstractColumn<Special, ?>> createColumns() {
		List<AbstractColumn<Special, ?>> columns =
			new ArrayList<AbstractColumn<Special, ?>>();
		columns.add(new FilterColumn<Special, String>("Shortcut", item -> item,
			path -> path.getShortcut(), (path, value) -> path.setShortcut(value)));
		columns.add(new FilterColumn<Special, String>("Description", item -> item,
			path -> path.getDescription(),
			(path, value) -> path.setDescription(value)));
		ColumnUtils.createCountingColumn("Files", columns, item -> item.getFiles());
		ColumnUtils.createCountingColumn("Notes", columns, item -> item.getNotes());
//		createPropertyColumns(columns, this.getItems());
		ColumnUtils.createPersonifiedColumns(columns);
		return columns;
	}

	@Override
	public ItemEditFactoryController<Special> createEditFactory() {
		return new SpecialEditFactoryController();
	}

	private class SpecialEditFactoryController extends GroupedItemEditFactoryController<Special> {
		private final TextField textFieldShortcut;
		private final TextField textFieldDescription;
		
		{
			textFieldShortcut = new TextField();
			textFieldDescription = new TextField();
			
			List<TitledPane> panes = new ArrayList<TitledPane>();
			List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
			generalProperties.add(new PropertyEntry("Shortcut", textFieldShortcut));
			generalProperties.add(new PropertyEntry("Description", textFieldDescription));
			TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
			panes.add(generalPane);
			setTitledPanes(panes);
			
			setItemFactory(this::createItem);
			setTemplateSetter(this::setEditTemplate);
			setChangeApplier(this::applyChanges);
		}

		@Override
		public Special createItem() {
			String shortcut = textFieldShortcut.getText();
			String description = textFieldDescription.getText();
			return new Special(shortcut, description);
		}

		@Override
		protected void setEditTemplate(Special item) {
			textFieldShortcut.setText(item.getShortcut());
			textFieldDescription.setText(item.getDescription());
		}

		@Override
		protected void applyChanges(Special item) {
			String shortcut = textFieldShortcut.getText();
			String description = textFieldDescription.getText();
	    	
			item.setShortcut(shortcut);
			item.setDescription(description);
		}
		
	}
	
}
