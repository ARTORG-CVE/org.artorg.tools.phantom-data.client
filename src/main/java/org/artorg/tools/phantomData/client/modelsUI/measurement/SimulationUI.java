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
import org.artorg.tools.phantomData.server.models.measurement.Project;
import org.artorg.tools.phantomData.server.models.measurement.Simulation;

public class SimulationUI extends UIEntity<Simulation> {

	public Class<Simulation> getItemClass() {
		return Simulation.class;
	}

	private static final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

	@Override
	public String getTableName() {
		return "Simulations";
	}

	@Override
	public List<AbstractColumn<Simulation, ?>> createColumns(Table<Simulation> table,
			List<Simulation> items) {
		List<AbstractColumn<Simulation, ?>> columns = new ArrayList<>();
		ColumnCreator<Simulation, Simulation> creator = new ColumnCreator<>(table);
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
		createCountingColumn(table, "Files", columns, item -> item.getFiles());
		createCountingColumn(table, "Notes", columns, item -> item.getNotes());
		createPropertyColumns(table, columns, items);
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditor<Simulation> createEditFactory() {
		ItemEditor<Simulation> editor = new ItemEditor<Simulation>(getItemClass()) {

			@Override
			public void createPropertyGridPanes(Creator<Simulation> creator) {
				PropertyGridPane<Simulation> propertyPane =
						new PropertyGridPane<Simulation>(Simulation.class);
				creator.createDatePicker((item, value) -> item.setStartDate(value),
						item -> item.getStartDate()).addOn(propertyPane, "Start date");
				creator.createComboBox(Person.class, item -> item.getPerson(),
						(item, value) -> item.setPerson(value)).setMapper(p -> p.getName())
						.addOn(propertyPane, "Person");
				creator.createComboBox(Project.class, item -> item.getProject(),
						(item, value) -> item.setProject(value)).setMapper(p -> p.getName())
						.addOn(propertyPane, "Project");
				propertyPane.setTitled("General");
				propertyPane.addOn(this);
			}

			@Override
			public void createSelectors(Creator<Simulation> creator) {
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
