package org.artorg.tools.phantomData.client.modelsUI.measurement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyGridPane;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.exceptions.InvalidUIInputException;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.DbFile;
import org.artorg.tools.phantomData.server.models.base.person.Person;
import org.artorg.tools.phantomData.server.models.measurement.Project;

import javafx.scene.control.TextField;

public class ProjectUI extends UIEntity<Project> {

	public Class<Project> getItemClass() {
		return Project.class;
	}

	@Override
	public String getTableName() {
		return "Projects";
	}

	@Override
	public List<AbstractColumn<Project, ?>> createColumns(Table<Project> table,
			List<Project> items) {
		List<AbstractColumn<Project, ?>> columns = new ArrayList<AbstractColumn<Project, ?>>();
		ColumnCreator<Project, Project> creator = new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Name", path -> path.getName(),
				(path, value) -> path.setName(value)));
		columns.add(creator.createFilterColumn("Description", path -> path.getDescription(),
				(path, value) -> path.setDescription(value)));
		columns.add(creator.createFilterColumn("Start Year",
				path -> Short.toString(path.getStartYear()),
				(path, value) -> path.setStartYear(Short.valueOf(value))));
		columns.add(creator.createFilterColumn("Leader", path -> path.getLeader().toName()));
		columns.add(creator.createFilterColumn("Members", path -> String.valueOf(path.getMembers()
				.stream().map(member -> member.getLastname()).collect(Collectors.joining(", ")))));
		createCountingColumn(table, "Measur.", columns, item -> item.getMeasurements());
		createCountingColumn(table, "Simul.", columns, item -> item.getSimulations());
		createCountingColumn(table, "Files", columns, item -> item.getFiles());
		createCountingColumn(table, "Notes", columns, item -> item.getNotes());
		createPropertyColumns(table, columns, items);
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditor<Project> createEditFactory() {
		TextField textFieldStartYear = new TextField();

		ItemEditor<Project> editor = new ItemEditor<Project>(getItemClass()) {	
			@Override
			public void onCreatingClient(Project item) throws InvalidUIInputException {
				String startYear = textFieldStartYear.getText();
				if (!textFieldStartYear.getText().matches("\\d{4}"))
					throw new InvalidUIInputException(Project.class,
							"Start year has not format YYYY: '" + startYear + "'");
			}

			@Override
			public void onUpdatingClient(Project item)
					throws InvalidUIInputException {
				String startYear = textFieldStartYear.getText();
				if (!textFieldStartYear.getText().matches("\\d{4}"))
					throw new InvalidUIInputException(Project.class,
							"Start year has not format YYYY: '" + startYear + "'");
			}

		};
		
		PropertyGridPane propertyPane =
				new PropertyGridPane();
		propertyPane.addEntry("Prefix", editor.createTextField(item -> item.getName(),
				(item, value) -> item.setName(value)));
		propertyPane.addEntry("Description",editor.createTextArea(item -> item.getDescription(),
				(item, value) -> item.setDescription(value)));
		propertyPane.addEntry("Start year",editor.create(textFieldStartYear, item -> Short.toString(item.getStartYear()),
				(item, value) -> item.setStartYear(Short.valueOf((value)))));
		propertyPane.addEntry("Leader",editor.createComboBox(Person.class, item -> item.getLeader(),
				(item, value) -> item.setLeader(value))
				.setMapper(l -> l.getSimpleAcademicName()));
		propertyPane.autosizeColumnWidths();
		editor.add(new TitledPropertyPane("General", propertyPane));
		
		
		editor.add(new TitledPropertyPane("Members", editor.createSelector(Person.class, item -> item.getMembers(),
				(item, files) -> item.setMembers((List<Person>) files))));
		editor.add(new TitledPropertyPane("Files", editor.createSelector(DbFile.class, item -> item.getFiles(),
				(item, files) -> item.setFiles((List<DbFile>) files))));
		
		editor.addApplyButton();
		return editor;
	}

}
