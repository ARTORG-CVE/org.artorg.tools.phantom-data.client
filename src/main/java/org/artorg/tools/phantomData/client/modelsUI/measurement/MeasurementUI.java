package org.artorg.tools.phantomData.client.modelsUI.measurement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.Creator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
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
		return "Measurement";
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
				creator.createDatePicker((item, value) -> item.setStartDate(value),
						item -> item.getStartDate()).addLabeled("Start date");
				creator.createComboBox(Person.class)
						.of(item -> item.getPerson(), (item, value) -> item.setPerson(value))
						.setMapper(p -> p.getName()).addLabeled("Person");
				creator.createComboBox(Project.class)
						.of(item -> item.getProject(), (item, value) -> item.setProject(value))
						.setMapper(p -> p.getName()).addLabeled("Project");
				creator.createComboBox(ExperimentalSetup.class)
						.of(item -> item.getExperimentalSetup(),
								(item, value) -> item.setExperimentalSetup(value))
						.setMapper(p -> p.getShortName())
						.addLabeled("Experimental Setup");
				creator.addTitledPropertyPane("General");
			}

			@Override
			public void createSelectors(Creator<Measurement> creator) {
				creator.addSelector("Protocol Files", DbFile.class,
						item -> item.getFiles(), (item, files) -> item.setFiles((List<DbFile>) files));
				creator.addSelector("Files", DbFile.class, item -> item.getFiles(),
						(item, files) -> item.setFiles((List<DbFile>) files));
			}
			
		};

		editor.addApplyButton();
		return editor;
	}

}
