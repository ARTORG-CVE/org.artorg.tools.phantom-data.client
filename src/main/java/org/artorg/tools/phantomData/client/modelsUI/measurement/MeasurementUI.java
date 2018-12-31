package org.artorg.tools.phantomData.client.modelsUI.measurement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.artorg.tools.phantomData.server.models.base.person.Person;
import org.artorg.tools.phantomData.server.models.measurement.ExperimentalSetup;
import org.artorg.tools.phantomData.server.models.measurement.Measurement;
import org.artorg.tools.phantomData.server.models.measurement.Project;

public class MeasurementUI extends UIEntity<Measurement> {

	public Class<Measurement> getItemClass() {
		return Measurement.class;
	}

	private static final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

	@Override
	public String getTableName() {
		return "Measurements";
	}

	@Override
	public List<AbstractColumn<Measurement, ?>> createColumns(Table<Measurement> table,
			List<Measurement> items) {
		List<AbstractColumn<Measurement, ?>> columns = new ArrayList<>();
		ColumnCreator<Measurement, Measurement> creator = new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Date", path -> format.format(path.getStartDate()),
				(path, value) -> {
					try {
						path.setStartDate(format.parse(value));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}));
		columns.add(creator.createFilterColumn("Person", path -> path.getPerson().toName()));
		columns.add(creator.createFilterColumn("Project", path -> path.getProject().toName()));
		columns.add(creator.createFilterColumn("Experimental Setup",
				path -> path.getExperimentalSetup().getShortName()));
		createCountingColumn(table, "Files", columns, item -> item.getFiles());
		createCountingColumn(table, "Notes", columns, item -> item.getNotes());
		createPropertyColumns(table, columns, items);
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditor<Measurement> createEditFactory() {
		ItemEditor<Measurement> editor = new ItemEditor<Measurement>(getItemClass()) {

			@Override
			public void createPropertyGridPanes(Creator<Measurement> creator) {
				PropertyGridPane<Measurement> propertyPane =
						new PropertyGridPane<Measurement>(Measurement.class);
				creator.createDatePicker((item, value) -> item.setStartDate(value),
						item -> item.getStartDate()).addOn(propertyPane, "Start date");
				creator.createComboBox(Person.class, item -> item.getPerson(),
						(item, value) -> item.setPerson(value)).setMapper(p -> p.getName())
						.addOn(propertyPane, "Person");
				creator.createComboBox(Project.class, item -> item.getProject(),
						(item, value) -> item.setProject(value)).setMapper(p -> p.getName())
						.addOn(propertyPane, "Project");
				creator.createComboBox(ExperimentalSetup.class, item -> item.getExperimentalSetup(),
						(item, value) -> item.setExperimentalSetup(value))
						.setMapper(p -> p.getShortName()).addOn(propertyPane, "Experimental Setup");
				propertyPane.setTitled("General");
				propertyPane.addOn(this);
			}

			@Override
			public void createSelectors(Creator<Measurement> creator) {
				creator.createSelector(DbFile.class, item -> item.getFiles(),
						(item, files) -> item.setFiles((List<DbFile>) files))
						.setTitled("Protocol Files").addOn(this);
				creator.createSelector(DbFile.class, item -> item.getFiles(),
						(item, files) -> item.setFiles((List<DbFile>) files)).setTitled("Files")
						.addOn(this);
			}

		};

		editor.addApplyButton();
		return editor;
	}

}
