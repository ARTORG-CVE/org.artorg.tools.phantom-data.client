package org.artorg.tools.phantomData.client.modelsUI.measurement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.editor.select.AbstractTableViewSelector;
import org.artorg.tools.phantomData.client.editors.DbFileEditFactoryController;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.DbFile;
import org.artorg.tools.phantomData.server.models.base.person.Person;
import org.artorg.tools.phantomData.server.models.measurement.ExperimentalSetup;
import org.artorg.tools.phantomData.server.models.measurement.Measurement;
import org.artorg.tools.phantomData.server.models.measurement.Project;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TitledPane;

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
	public List<AbstractColumn<Measurement, ?>> createColumns(Table<Measurement> table, List<Measurement> items) {
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
	public ItemEditFactoryController<Measurement> createEditFactory() {
		return new MeasurementEditFactoryController();
	}

	private class MeasurementEditFactoryController
			extends GroupedItemEditFactoryController<Measurement> {
		private ComboBox<Person> comboBoxPerson;
		private ComboBox<Project> comboBoxProject;
		private ComboBox<ExperimentalSetup> comboBoxSetup;

		private DatePicker datePicker;
		private DbFileEditFactoryController fileController;

		{
			comboBoxPerson = new ComboBox<Person>();
			comboBoxProject = new ComboBox<Project>();
			comboBoxSetup = new ComboBox<ExperimentalSetup>();
			datePicker = new DatePicker();
			datePicker.setValue(LocalDate.now());

			fileController = new DbFileEditFactoryController();

			List<TitledPane> panes = new ArrayList<TitledPane>();
			createComboBoxes();
			List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
			generalProperties.add(new PropertyEntry("Date", datePicker));
			generalProperties.add(new PropertyEntry("Person", comboBoxPerson));
			generalProperties.add(new PropertyEntry("Project", comboBoxProject));
			generalProperties.add(new PropertyEntry("Experimental Setup", comboBoxSetup));

			TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
			panes.add(generalPane);
			setTitledPanes(panes);

			fileController.create();
		}

		@Override
		protected void addProperties(Measurement item) {
			super.getTitledPanes().stream().filter(p -> p instanceof TitledPropertyPane).forEach(
					p -> super.getPropertyEntries().addAll(((TitledPropertyPane) p).getEntries()));

			TitledPane protocolTitledPane = new TitledPane();
			protocolTitledPane.setContent(fileController.getTitledPanes().get(0).getContent());
			protocolTitledPane.setText("Protocol File");

			super.getTitledPanes().add(protocolTitledPane);

			List<AbstractTableViewSelector<?>> selectors = this.getSelectors();
			super.getTitledPanes().addAll(selectors.stream().map(selector -> {
				if (selector.getGraphic() instanceof TitledPane)
					return (TitledPane) selector.getGraphic();
				TitledPane titledPane = new TitledPane();
				titledPane.setContent(selector.getGraphic());
				titledPane.setText(selector.getSubItemClass().getSimpleName());
				return titledPane;
			}).collect(Collectors.toList()));

		}

		private void createComboBoxes() {
			createComboBox(comboBoxPerson, Person.class, item -> item.getSimpleAcademicName());
			createComboBox(comboBoxProject, Project.class, item -> item.getName());
			createComboBox(comboBoxSetup, ExperimentalSetup.class, item -> item.getLongName());
		}

		@Override
		protected void setEditTemplate(Measurement item) {
			LocalDate localDate =
					item.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			datePicker.setValue(localDate);
			super.selectComboBoxItem(comboBoxPerson, item.getPerson());
			super.selectComboBoxItem(comboBoxProject, item.getProject());
			super.selectComboBoxItem(comboBoxSetup, item.getExperimentalSetup());
		}

		@Override
		public Measurement createItem() {
			Date startDate = Date
					.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
			Person person = comboBoxPerson.getSelectionModel().getSelectedItem();
			Project project = comboBoxProject.getSelectionModel().getSelectedItem();
			ExperimentalSetup setup = comboBoxSetup.getSelectionModel().getSelectedItem();
			DbFile protocolFile = fileController.createAndPersistItem();

			return new Measurement(startDate, person, project, setup, protocolFile);
		}

		@Override
		protected void applyChanges(Measurement item) {
			Date startDate = Date
					.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
			Person person = comboBoxPerson.getSelectionModel().getSelectedItem();
			Project project = comboBoxProject.getSelectionModel().getSelectedItem();
			ExperimentalSetup setup = comboBoxSetup.getSelectionModel().getSelectedItem();

			item.setStartDate(startDate);
			item.setPerson(person);
			item.setProject(project);
			item.setExperimentalSetup(setup);
		}
	}
}
