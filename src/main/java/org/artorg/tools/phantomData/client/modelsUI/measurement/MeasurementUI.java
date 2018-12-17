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
import org.artorg.tools.phantomData.client.column.FilterColumn;
import org.artorg.tools.phantomData.client.editor.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.editors.DbFileEditFactoryController;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.select.AbstractTableViewSelector;
import org.artorg.tools.phantomData.client.util.ColumnUtils;
import org.artorg.tools.phantomData.server.models.base.DbFile;
import org.artorg.tools.phantomData.server.models.base.person.Person;
import org.artorg.tools.phantomData.server.models.measurement.ExperimentalSetup;
import org.artorg.tools.phantomData.server.models.measurement.Measurement;
import org.artorg.tools.phantomData.server.models.measurement.Project;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TitledPane;

public class MeasurementUI implements UIEntity<Measurement> {
	private static final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

	@Override
	public Class<Measurement> getItemClass() {
		return Measurement.class;
	}

	@Override
	public String getTableName() {
		return "Measurement";
	}

	@Override
	public List<AbstractColumn<Measurement, ?>> createColumns() {
		List<AbstractColumn<Measurement, ?>> columns = new ArrayList<>();
		columns.add(new FilterColumn<>("Date", path -> format.format(path.getStartDate()),
				(path, value) -> {
					try {
						path.setStartDate(format.parse(value));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}));
		columns.add(new FilterColumn<>("Person", item -> item.getPerson(), path -> path.toName()));
		columns.add(
				new FilterColumn<>("Project", item -> item.getProject(), path -> path.toName()));
		columns.add(new FilterColumn<>("Experimental Setup", item -> item.getExperimentalSetup(),
				path -> path.getShortName()));
		ColumnUtils.createCountingColumn("Files", columns, item -> item.getFiles());
		ColumnUtils.createCountingColumn("Notes", columns, item -> item.getNotes());
		ColumnUtils.createPersonifiedColumns(columns);
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

			setItemFactory(this::createItem);
			setTemplateSetter(this::setEditTemplate);
			setChangeApplier(this::applyChanges);

			fileController.create(DbFile.class);
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
