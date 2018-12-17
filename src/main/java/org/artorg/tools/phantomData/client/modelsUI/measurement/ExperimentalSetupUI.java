package org.artorg.tools.phantomData.client.modelsUI.measurement;

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
import org.artorg.tools.phantomData.server.models.measurement.ExperimentalSetup;

import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class ExperimentalSetupUI implements UIEntity<ExperimentalSetup> {

	@Override
	public Class<ExperimentalSetup> getItemClass() {
		return ExperimentalSetup.class;
	}

	@Override
	public String getTableName() {
		return "Experimental Setups";
	}

	@Override
	public List<AbstractColumn<ExperimentalSetup, ?>> createColumns() {
		List<AbstractColumn<ExperimentalSetup, ?>> columns = new ArrayList<>();
		columns.add(new FilterColumn<>("Short name", path -> path.getShortName(),
				(path, value) -> path.setShortName(value)));
		columns.add(new FilterColumn<>("Long name", path -> path.getLongName(),
				(path, value) -> path.setLongName(value)));
		columns.add(new FilterColumn<>("Description", path -> path.getDescription(),
				(path, value) -> path.setDescription(value)));
		ColumnUtils.createCountingColumn("Files", columns, item -> item.getFiles());
		ColumnUtils.createCountingColumn("Notes", columns, item -> item.getNotes());
		ColumnUtils.createPersonifiedColumns(columns);
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

			setItemFactory(this::createItem);
			setTemplateSetter(this::setEditTemplate);
			setChangeApplier(this::applyChanges);
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
