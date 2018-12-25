package org.artorg.tools.phantomData.client.modelsUI.base.person;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.person.AcademicTitle;
import org.artorg.tools.phantomData.server.models.base.person.Gender;
import org.artorg.tools.phantomData.server.models.base.person.Person;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;


public class PersonUI extends UIEntity<Person> {

	public Class<Person> getItemClass() {
		return Person.class;
	}

	@Override
	public String getTableName() {
		return "Persons";
	}

	@Override
	public List<AbstractColumn<Person, ?>> createColumns(Table<Person> table, List<Person> items) {
		List<AbstractColumn<Person, ?>> columns = new ArrayList<>();
		ColumnCreator<Person, Person> creator = new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Title", item -> item.getAcademicTitle().getPrefix()));
		columns.add(creator.createFilterColumn("Firstname", path -> path.getFirstname(),
				(path, value) -> path.setFirstname((String) value)));
		columns.add(creator.createFilterColumn("Lastname", path -> path.getLastname(),
				(path, value) -> path.setLastname((String) value)));
		columns.add(creator.createFilterColumn("Gender", path -> path.getGender().getName(),
				(path, value) -> path.getGender().setName((String) value)));
		return columns;
	}

	@Override
	public ItemEditFactoryController<Person> createEditFactory() {
		return new PersonEditFactoryController();
	}

	private class PersonEditFactoryController extends GroupedItemEditFactoryController<Person> {
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
		protected void setEditTemplate(Person item) {
			super.selectComboBoxItem(comboBoxGender, item.getGender());
			super.selectComboBoxItem(comboBoxTitle, item.getAcademicTitle());
			textFieldFirstname.setText(item.getFirstname());
			textFieldLastname.setText(item.getLastname());
		}

		@Override
		protected void applyChanges(Person item) {
			Gender gender = comboBoxGender.getSelectionModel().getSelectedItem();
			AcademicTitle title = comboBoxTitle.getSelectionModel().getSelectedItem();
			String firstname = textFieldFirstname.getText();
			String lastname = textFieldLastname.getText();

			item.setGender(gender);
			item.setAcademicTitle(title);
			item.setFirstname(firstname);
			item.setLastname(lastname);
		}

	}

}
