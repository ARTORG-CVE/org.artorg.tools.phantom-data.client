package org.artorg.tools.phantomData.client.editors.measurement;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.editor.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.server.model.base.person.Person;
import org.artorg.tools.phantomData.server.model.measurement.Project;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class ProjectEditFactoryController extends GroupedItemEditFactoryController<Project> {
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
		TitledPropertyPane generalPane =
			new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		setTitledPanes(panes);

		setItemFactory(this::createItem);
		setTemplateSetter(this::setEditTemplate);
		setChangeApplier(this::applyChanges);
	}
	
	
	private void createComboBoxes() {
		createComboBox(comboBoxPerson, Person.class,
			item -> item.getSimpleAcademicName());
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
