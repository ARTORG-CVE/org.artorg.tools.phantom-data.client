package org.artorg.tools.phantomData.client.modelsUI.base.person;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.admin.UserAdmin;
import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyGridPane;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.exceptions.InvalidUIInputException;
import org.artorg.tools.phantomData.client.logging.Logger;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.DbTable;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.person.AcademicTitle;
import org.artorg.tools.phantomData.server.models.base.person.Gender;
import org.artorg.tools.phantomData.server.models.base.person.Person;

import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class PersonUI extends UIEntity<Person> {

	public Class<Person> getItemClass() {
		return Person.class;
	}

	@Override
	public String getTableName() {
		return "Persons";
	}

	public class PersonDbTable extends DbTable<Person> {

		public PersonDbTable() {
			super(Person.class);
		}

		@Override
		public void reload() {
			super.reload();
			getItems().remove(UserAdmin.getAdmin());
		}

		@Override
		public void readAllData() {
			super.readAllData();
			getItems().remove(UserAdmin.getAdmin());
		}

		@Override
		public List<AbstractColumn<Person, ? extends Object>> createColumns(List<Person> items) {
			return PersonUI.this.createColumns(this, items);
		}

	}

	@Override
	public DbTable<Person> createDbTableBase() {
		long startTime = System.currentTimeMillis();
		DbTable<Person> table = new PersonDbTable();
		table.setTableName(getTableName());
		Logger.debug.println(String.format("%s - DbTable created in %d ms",
				getItemClass().getSimpleName(), System.currentTimeMillis() - startTime));
		return table;
	}

	@Override
	public List<AbstractColumn<Person, ?>> createColumns(Table<Person> table, List<Person> items) {
		List<AbstractColumn<Person, ?>> columns = new ArrayList<>();
		ColumnCreator<Person, Person> creator = new ColumnCreator<>(table);
		columns.add(
				creator.createFilterColumn("Title", item -> item.getAcademicTitle().getPrefix()));
		columns.add(creator.createFilterColumn("Firstname", path -> path.getFirstname(),
				(path, value) -> path.setFirstname((String) value)));
		columns.add(creator.createFilterColumn("Lastname", path -> path.getLastname(),
				(path, value) -> path.setLastname((String) value)));
		columns.add(creator.createFilterColumn("Gender", path -> path.getGender().getName(),
				(path, value) -> path.getGender().setName((String) value)));
		columns.add(creator.createFilterColumn("Active?", path -> Boolean.toString(path.isActive()),
				(path, value) -> path.setActive(Boolean.valueOf(value))));
		return columns;
	}

	@Override
	public ItemEditor<Person> createEditFactory() {
		AnchorPane panePassword = new AnchorPane();
		PasswordField passwordField = new PasswordField();
		TextField passwordTextField = new TextField();
		passwordField.setText("12345678");
		passwordField.setEditable(false);

		ItemEditor<Person> editor = new ItemEditor<Person>(getItemClass()) {

			@Override
			public void onShowingCreateMode(Class<? extends Person> beanClass) {
				panePassword.getChildren().clear();
				panePassword.getChildren().add(passwordTextField);
			}

			@Override
			public void onShowingEditMode(Person item) {
				if (UserAdmin.isUserLoggedIn()) {
					if (UserAdmin.getUser().equalsId(UserAdmin.getHutzli())) {
						showPassword();
						return;
					} else if (UserAdmin.getUser().equalsId(item)) {
						showPassword();
						return;
					} else if (UserAdmin.getUser().equalsId(UserAdmin.getAdmin())
							&& !item.equalsId(UserAdmin.getHutzli())) {
						showPassword();
						return;
					}
				}
				hidePassword();
			}

			private void showPassword() {
				panePassword.getChildren().clear();
				panePassword.getChildren().add(passwordTextField);
			}

			private void hidePassword() {
				panePassword.getChildren().clear();
				panePassword.getChildren().add(passwordField);
			}

			@Override
			public void onCreatingClient(Person item) throws InvalidUIInputException {
				if (passwordTextField.getText().length() < 4)
					throw new InvalidUIInputException(Person.class,
							"Password needs at least 4 characters");
			}

		};
		PropertyGridPane propertyPane = new PropertyGridPane();

		propertyPane
				.addEntry("Gender",
						editor.createComboBox(Gender.class, item -> item.getGender(),
								(item, value) -> item.setGender(value))
								.setMapper(g -> g.getName()));
		propertyPane.addEntry("Academic Title",
				editor.createComboBox(AcademicTitle.class, item -> item.getAcademicTitle(),
						(item, value) -> item.setAcademicTitle(value))
						.setMapper(a -> a.getPrefix()));
		propertyPane.addEntry("Firstname", editor.createTextField(item -> item.getFirstname(),
				(item, value) -> item.setFirstname(value)));
		propertyPane.addEntry("Lastname", editor.createTextField(item -> item.getLastname(),
				(item, value) -> item.setLastname(value)));
		propertyPane.addPropertyNode(editor.create(passwordTextField, item -> item.getPassword(),
				(item, value) -> item.setPassword(value)));
		propertyPane.addEntry(new Label("Password"), panePassword);
		propertyPane.addEntry("Active?", editor.createCheckBox(item -> item.isActive(),
				(item, value) -> item.setActive(value), true));
		editor.add(new TitledPropertyPane("General", propertyPane));
		editor.addApplyButton();
		return editor;

	}

}
