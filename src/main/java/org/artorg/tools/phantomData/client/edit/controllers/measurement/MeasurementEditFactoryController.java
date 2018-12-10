package org.artorg.tools.phantomData.client.edit.controllers.measurement;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.edit.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.edit.PropertyEntry;
import org.artorg.tools.phantomData.client.edit.TitledPropertyPane;
import org.artorg.tools.phantomData.client.edit.controllers.base.DbFileEditFactoryController;
import org.artorg.tools.phantomData.client.select.AbstractTableViewSelector;
import org.artorg.tools.phantomData.server.model.base.DbFile;
import org.artorg.tools.phantomData.server.model.base.person.Person;
import org.artorg.tools.phantomData.server.model.measurement.ExperimentalSetup;
import org.artorg.tools.phantomData.server.model.measurement.Measurement;
import org.artorg.tools.phantomData.server.model.measurement.Project;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TitledPane;

public class MeasurementEditFactoryController
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
		
		
		TitledPropertyPane generalPane =
			new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		setTitledPanes(panes);

		setItemFactory(this::createItem);
		setTemplateSetter(this::setEditTemplate);
		setChangeApplier(this::applyChanges);
		
		fileController.create(DbFile.class);
	}
	
	@Override
	protected void addProperties(Measurement item) {
		super.getTitledPanes().stream().filter(p -> p instanceof TitledPropertyPane)
				.forEach(p -> super.getPropertyEntries().addAll(((TitledPropertyPane) p).getEntries()));

		TitledPane protocolTitledPane = new TitledPane();
		protocolTitledPane.setContent(fileController.getTitledPanes().get(0).getContent());
		protocolTitledPane.setText("Protocol File");


		super.getTitledPanes().add(protocolTitledPane);
		
		List<AbstractTableViewSelector<Measurement>> selectors = this.getSelectors();
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
		createComboBox(comboBoxPerson, Person.class,
			item -> item.getSimpleAcademicName());
		createComboBox(comboBoxProject, Project.class,
			item -> item.getName());
		createComboBox(comboBoxSetup, ExperimentalSetup.class,
			item -> item.getLongName());
	}

	@Override
	protected void setEditTemplate(Measurement item) {
		LocalDate localDate = item.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		datePicker.setValue(localDate);
		super.selectComboBoxItem(comboBoxPerson, item.getPerson());
		super.selectComboBoxItem(comboBoxProject, item.getProject());
		super.selectComboBoxItem(comboBoxSetup, item.getExperimentalSetup());
	}

	@Override
	public Measurement createItem() {
		Date startDate = Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
		Person person = comboBoxPerson.getSelectionModel().getSelectedItem();
		Project project = comboBoxProject.getSelectionModel().getSelectedItem();
		ExperimentalSetup setup = comboBoxSetup.getSelectionModel().getSelectedItem();
		DbFile protocolFile = fileController.createAndPersistItem();

		return new Measurement(startDate, person, project, setup, protocolFile);
	}

	@Override
	protected void applyChanges(Measurement item) {
		Date startDate = Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
		Person person = comboBoxPerson.getSelectionModel().getSelectedItem();
		Project project = comboBoxProject.getSelectionModel().getSelectedItem();
		ExperimentalSetup setup = comboBoxSetup.getSelectionModel().getSelectedItem();

		item.setStartDate(startDate);
		item.setPerson(person);
		item.setProject(project);
		item.setExperimentalSetup(setup);
	}

}