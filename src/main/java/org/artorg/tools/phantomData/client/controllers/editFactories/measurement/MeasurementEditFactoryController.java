package org.artorg.tools.phantomData.client.controllers.editFactories.measurement;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.server.model.base.person.Person;
import org.artorg.tools.phantomData.server.model.measurement.Measurement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class MeasurementEditFactoryController
	extends GroupedItemEditFactoryController<Measurement> {
	private TextField textFieldName;
	private TextField textFieldDescription;
	private ComboBox<String> comboBoxDateFormat;
	private ComboBox<Person> comboBoxPerson;

	private DatePicker datePicker;

	{
		textFieldName = new TextField();
		textFieldDescription = new TextField();
		comboBoxDateFormat = new ComboBox<String>();
		comboBoxPerson = new ComboBox<Person>();
		datePicker = new DatePicker();
		datePicker.setValue(LocalDate.now());

		List<TitledPane> panes = new ArrayList<TitledPane>();
		createComboBoxes();
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("Date", datePicker));
		generalProperties.add(new PropertyEntry("Date Format", comboBoxDateFormat));
		generalProperties.add(new PropertyEntry("Person", comboBoxPerson));
		generalProperties.add(new PropertyEntry("Name", textFieldName));
		generalProperties.add(new PropertyEntry("Description", textFieldDescription));
		TitledPropertyPane generalPane =
			new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		setTitledPanes(panes);

		setItemFactory(this::createItem);
		setTemplateSetter(this::setEditTemplate);
		setChangeApplier(this::applyChanges);
	}

	private void createComboBoxes() {
		ObservableList<String> formats = FXCollections.observableArrayList();
		formats.add("YYYY");
		comboBoxDateFormat.setItems(formats);

		createComboBox(comboBoxPerson, Person.class,
			item -> item.getSimpleAcademicName());
	}

	@Override
	protected void setEditTemplate(Measurement item) {
		textFieldName.setText(item.getName());
		textFieldDescription.setText(item.getDescription());
		LocalDate localDate = item.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		datePicker.setValue(localDate);
		for (int i = 0; i < comboBoxDateFormat.getItems().size(); i++)
			if (comboBoxDateFormat.getItems().get(i).equals(item.getDateFormat())) {
				comboBoxDateFormat.getSelectionModel().select(i);
				break;
			}
		super.selectComboBoxItem(comboBoxPerson, item.getPerson());
	}

	@Override
	public Measurement createItem() {
		String name = textFieldName.getText();
		String description = textFieldDescription.getText();
		String format = comboBoxDateFormat.getSelectionModel().getSelectedItem();
		
		Date date = Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
		Person person = comboBoxPerson.getSelectionModel().getSelectedItem();

		return new Measurement(name, description, date, format, person);
	}

	@Override
	protected void applyChanges(Measurement item) {
		String name = textFieldName.getText();
		String description = textFieldDescription.getText();
		String format = comboBoxDateFormat.getSelectionModel().getSelectedItem();
		Date date = Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
		Person person = comboBoxPerson.getSelectionModel().getSelectedItem();

		item.setName(name);
		item.setDescription(description);
		item.setStartDate(date);
		item.setDateFormat(format);
		item.setPerson(person);
	}

}