package org.artorg.tools.phantomData.client.modelsUI.measurement;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.Creator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyGridPane;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.DbFile;
import org.artorg.tools.phantomData.server.models.measurement.ExperimentalSetup;

public class ExperimentalSetupUI extends UIEntity<ExperimentalSetup> {

	public Class<ExperimentalSetup> getItemClass() {
		return ExperimentalSetup.class;
	}

	@Override
	public String getTableName() {
		return "Experimental Setups";
	}

	@Override
	public List<AbstractColumn<ExperimentalSetup, ?>> createColumns(Table<ExperimentalSetup> table,
			List<ExperimentalSetup> items) {
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
	public ItemEditor<ExperimentalSetup> createEditFactory() {
		ItemEditor<ExperimentalSetup> creator = new ItemEditor<ExperimentalSetup>(getItemClass()) {

			@Override
			public void createPropertyGridPanes(Creator<ExperimentalSetup> creator) {
				PropertyGridPane<ExperimentalSetup> propertyPane =
						new PropertyGridPane<ExperimentalSetup>(ExperimentalSetup.class);
				creator.createTextField(item -> item.getShortName(),
						(item, value) -> item.setShortName(value))
						.addOn(propertyPane, "Short name");
				creator.createTextField(item -> item.getLongName(),
						(item, value) -> item.setLongName(value)).addOn(propertyPane, "Long name");
				creator.createTextArea(item -> item.getDescription(),
						(item, value) -> item.setDescription(value))
						.addOn(propertyPane, "Description");
				propertyPane.setTitled("General");
				propertyPane.addOn(this);
			}

			@Override
			public void createSelectors(Creator<ExperimentalSetup> creator) {
				creator.createSelector(DbFile.class, item -> item.getFiles(),
						(item, files) -> item.setFiles((List<DbFile>) files)).setTitled("Files")
						.addOn(this);
			}

		};
		creator.addApplyButton();
		return creator;
	}

}
