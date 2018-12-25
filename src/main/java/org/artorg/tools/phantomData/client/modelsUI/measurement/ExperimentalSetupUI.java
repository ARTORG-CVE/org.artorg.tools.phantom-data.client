package org.artorg.tools.phantomData.client.modelsUI.measurement;

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
import org.artorg.tools.phantomData.server.models.measurement.ExperimentalSetup;

import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class ExperimentalSetupUI extends UIEntity<ExperimentalSetup> {

	public Class<ExperimentalSetup> getItemClass() {
		return ExperimentalSetup.class;
	}

	@Override
	public String getTableName() {
		return "Experimental Setups";
	}

	@Override
	public List<AbstractColumn<ExperimentalSetup, ?>> createColumns(Table<ExperimentalSetup> table, List<ExperimentalSetup> items) {
		List<AbstractColumn<ExperimentalSetup, ?>> columns = new ArrayList<>();
		ColumnCreator<ExperimentalSetup, ExperimentalSetup> creator = new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Short name", path -> path.getShortName(),
				(path, value) -> path.setShortName(value)));
		columns.add(creator.createFilterColumn("Long name", path -> path.getLongName(),
				(path, value) -> path.setLongName(value)));
		columns.add(creator.createFilterColumn("Description", path -> path.getDescription(),
				(path, value) -> path.setDescription(value)));
		createCountingColumn(table, "Files", columns, item -> item.getFiles());
		createCountingColumn(table, "Notes", columns, item -> item.getNotes());
		createPropertyColumns(table, columns, items);
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditFactoryController<ExperimentalSetup> createEditFactory() {
		return new ExperimentalSetupEditFactoryController();
	}

	private class ExperimentalSetupEditFactoryController
			extends GroupedItemEditFactoryController<ExperimentalSetup> {

		private final TextField textFieldShortName;
		private final TextField textFieldLongName;
		private final TextField textFieldDescription;

		{
			textFieldShortName = new TextField();
			textFieldLongName = new TextField();
			textFieldDescription = new TextField();

			List<TitledPane> panes = new ArrayList<TitledPane>();
			List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
			generalProperties.add(new PropertyEntry("Short name", textFieldShortName));
			generalProperties.add(new PropertyEntry("Long name", textFieldLongName));
			generalProperties.add(new PropertyEntry("Description", textFieldDescription));
			TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
			panes.add(generalPane);
			setTitledPanes(panes);
		}

		@Override
		public ExperimentalSetup createItem() {
			String shortName = textFieldShortName.getText();
			String longName = textFieldLongName.getText();
			String description = textFieldDescription.getText();
			return new ExperimentalSetup(shortName, longName, description);
		}

		@Override
		protected void setEditTemplate(ExperimentalSetup item) {
			textFieldShortName.setText(item.getShortName());
			textFieldLongName.setText(item.getLongName());
			textFieldDescription.setText(item.getDescription());
		}

		@Override
		protected void applyChanges(ExperimentalSetup item) {
			String shortName = textFieldShortName.getText();
			String longName = textFieldLongName.getText();
			String description = textFieldDescription.getText();

			item.setShortName(shortName);
			item.setLongName(longName);
			item.setDescription(description);
		}
	}

}
