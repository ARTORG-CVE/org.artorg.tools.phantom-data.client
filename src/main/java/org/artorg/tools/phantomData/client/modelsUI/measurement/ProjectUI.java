package org.artorg.tools.phantomData.client.modelsUI.measurement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.client.util.ColumnUtils;
import org.artorg.tools.phantomData.server.models.base.person.Person;
import org.artorg.tools.phantomData.server.models.measurement.Project;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class ProjectUI implements UIEntity<Project> {

	public Class<Project> getItemClass() {
		return Project.class;
	}

	@Override
	public String getTableName() {
		return "Projects";
	}

	@Override
	public List<AbstractColumn<Project, ?>> createColumns(Table<Project> table, List<Project> items) {
		List<AbstractColumn<Project, ?>> columns = new ArrayList<AbstractColumn<Project, ?>>();
		ColumnCreator<Project, Project> creator = new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Name", path -> path.getName(),
				(path, value) -> path.setName(value)));
		columns.add(creator.createFilterColumn("Description", path -> path.getDescription(),
				(path, value) -> path.setDescription(value)));
		columns.add(creator.createFilterColumn("Start Year", path -> Short.toString(path.getStartYear()),
				(path, value) -> path.setStartYear(Short.valueOf(value))));
		columns.add(creator.createFilterColumn("Leader", path -> path.getLeader().toName()));
		columns.add(creator.createFilterColumn("Members", path -> String.valueOf(path.getMembers()
				.stream().map(member -> member.getLastname()).collect(Collectors.joining(", ")))));
//		ColumnUtils.createCountingColumn("Measur.", columns, item -> item.getMeasurements());
		ColumnUtils.createCountingColumn(table, "Files", columns, item -> item.getFiles());
		ColumnUtils.createCountingColumn(table, "Notes", columns, item -> item.getNotes());
		ColumnUtils.createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditFactoryController<Project> createEditFactory() {
		return new ProjectEditFactoryController();
	}

	private class ProjectEditFactoryController extends GroupedItemEditFactoryController<Project> {
		private TextField textFieldName;
		private TextField textFieldDescription;
		private TextField textFieldStartYear;
		private ComboBox<Person> comboBoxPerson;

		{
			textFieldName = new TextField();
			textFieldDescription = new TextField();
			textFieldStartYear = new TextField();
			comboBoxPerson = new ComboBox<Person>();

			List<TitledPane> panes = new ArrayList<TitledPane>();
			createComboBoxes();
			List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
			generalProperties.add(new PropertyEntry("Name", textFieldName));
			generalProperties.add(new PropertyEntry("Description", textFieldDescription));
			generalProperties.add(new PropertyEntry("Start year", textFieldStartYear));
			generalProperties.add(new PropertyEntry("Person", comboBoxPerson));
			TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
			panes.add(generalPane);
			setTitledPanes(panes);
		}

		private void createComboBoxes() {
			createComboBox(comboBoxPerson, Person.class, item -> item.getSimpleAcademicName());
		}

		@Override
		protected void setEditTemplate(Project item) {
			textFieldName.setText(item.getName());
			textFieldDescription.setText(item.getDescription());
			textFieldStartYear.setText(Short.toString(item.getStartYear()));
			super.selectComboBoxItem(comboBoxPerson, item.getLeader());
		}

		@Override
		public Project createItem() {
			String name = textFieldName.getText();
			String description = textFieldDescription.getText();
			short startYear = Short.valueOf(textFieldStartYear.getText());
			Person leader = comboBoxPerson.getSelectionModel().getSelectedItem();

			return new Project(name, description, startYear, leader);
		}

		@Override
		protected void applyChanges(Project item) {
			String name = textFieldName.getText();
			String description = textFieldDescription.getText();
			short startYear = Short.valueOf(textFieldStartYear.getText());
			Person leader = comboBoxPerson.getSelectionModel().getSelectedItem();

			item.setName(name);
			item.setDescription(description);
			item.setStartYear(startYear);
			item.setLeader(leader);
		}

	}

}
