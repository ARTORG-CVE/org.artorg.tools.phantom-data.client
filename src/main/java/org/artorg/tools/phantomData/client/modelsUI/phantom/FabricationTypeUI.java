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
import org.artorg.tools.phantomData.server.models.phantom.FabricationType;

import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class FabricationTypeUI extends UIEntity<FabricationType> {

	public Class<FabricationType> getItemClass() {
		return FabricationType.class;
	}

	@Override
	public String getTableName() {
		return "Fabrication Types";
	}

	@Override
	public List<AbstractColumn<FabricationType, ?>> createColumns(Table<FabricationType> table, List<FabricationType> items) {
		List<AbstractColumn<FabricationType, ?>> columns = new ArrayList<>();
		ColumnCreator<FabricationType, FabricationType> creator =
				new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Shortcut", path -> path.getShortcut(),
				(path, value) -> path.setShortcut(value)));
		columns.add(creator.createFilterColumn("Value", path -> path.getValue(),
				(path, value) -> path.setValue(value)));
		createCountingColumn(table, "Files", columns, item -> item.getFiles());
		createCountingColumn(table, "Notes", columns, item -> item.getNotes());
		createPropertyColumns(table, columns, items);
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditFactoryController<FabricationType> createEditFactory() {
		return new FabricationTypeEditFactoryController();
	}

	public class FabricationTypeEditFactoryController
			extends GroupedItemEditFactoryController<FabricationType> {
		private TextField textFieldShortcut;
		private TextField textFieldValue;

		{
			textFieldShortcut = new TextField();
			textFieldValue = new TextField();

			List<TitledPane> panes = new ArrayList<TitledPane>();
			List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
			generalProperties.add(new PropertyEntry("Shortcut", textFieldShortcut));
			generalProperties.add(new PropertyEntry("Name", textFieldValue));;
			TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
			panes.add(generalPane);
			setTitledPanes(panes);
		}

		@Override
		protected void setEditTemplate(FabricationType item) {
			textFieldShortcut.setText(item.getShortcut());
			textFieldValue.setText(item.getValue());
		}

		@Override
		public FabricationType createItem() {
			String shortcut = textFieldShortcut.getText();
			String value = textFieldValue.getText();
			return new FabricationType(shortcut, value);
		}

		@Override
		protected void applyChanges(FabricationType item) {
			String shortcut = textFieldShortcut.getText();
			String value = textFieldValue.getText();

			item.setShortcut(shortcut);
			item.setValue(value);
		}

	}

}
