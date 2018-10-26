package org.artorg.tools.phantomData.client.controllers.editFactories;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.server.model.person.AcademicTitle;
import org.artorg.tools.phantomData.server.model.person.Gender;
import org.artorg.tools.phantomData.server.model.person.Person;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class PersonEditFactoryController extends GroupedItemEditFactoryController<Person> {
	private ComboBox<Gender> comboBoxGender;
	private ComboBox<AcademicTitle> comboBoxTitle;
	private TextField textFieldFirstname;
	private TextField textFieldLastname;
	
	{
		comboBoxGender = new ComboBox<Gender>();
		comboBoxTitle = new ComboBox<AcademicTitle>();
		textFieldFirstname = new TextField();
		textFieldLastname = new TextField();
		
		List<TitledPane> panes = new ArrayList<TitledPane>();
		createComboBoxes();
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("Male/Female", comboBoxGender));
		generalProperties.add(new PropertyEntry("Title", comboBoxTitle));
		generalProperties.add(new PropertyEntry("Firstname", textFieldFirstname));
		generalProperties.add(new PropertyEntry("Lastname", textFieldLastname));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		setTitledPanes(panes);
		
		setItemFactory(this::createItem);
		setTemplateSetter(this::setTemplate);
		setItemCopier(this::copy);
	}
	
	private void createComboBoxes() {
        createComboBox(comboBoxGender, Gender.class, g -> g.getName());
        createComboBox(comboBoxTitle, AcademicTitle.class, t -> t.getPrefix());
    }
	
	@Override
	public void initDefaultValues() {
		super.initDefaultValues();
		textFieldFirstname.setText("");
		textFieldLastname.setText("");
	}

	@Override
	public Person createItem() {
		Gender gender = comboBoxGender.getSelectionModel().getSelectedItem();
		AcademicTitle title = comboBoxTitle.getSelectionModel().getSelectedItem();
		String firstname = textFieldFirstname.getText();
		String lastname = textFieldLastname.getText();
		return new Person(title, firstname, lastname, gender);
	}

	@Override
	protected void setTemplate(Person item) {
		super.selectComboBoxItem(comboBoxGender, item.getGender());
		super.selectComboBoxItem(comboBoxTitle, item.getAcademicTitle());
		textFieldFirstname.setText(item.getFirstname());
		textFieldLastname.setText(item.getLastname());
	}

	@Override
	protected void copy(Person from, Person to) {
		to.setGender(from.getGender());
		to.setAcademicTitle(from.getAcademicTitle());
		to.setFirstname(from.getFirstname());
		to.setLastname(from.getLastname());
	}
	
}