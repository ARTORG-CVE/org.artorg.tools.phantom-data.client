package org.artorg.tools.phantomData.client.modelsUI.base.person;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyGridPane;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.logging.Logger;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.DbTable;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.person.AcademicTitle;
import org.artorg.tools.phantomData.server.models.base.person.Gender;
import org.artorg.tools.phantomData.server.models.base.person.Person;

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
			getItems().remove(getAdmin());
		}

		@Override
		public void readAllData() {
			super.readAllData();
			getItems().remove(getAdmin());
		}
		
		private Person getAdmin() {
			return getItems().stream()
					.filter(person -> person.getId()
							.equals(getUuid("2ccc4440340a4afc9a0307d4167fcefe")))
					.findFirst().get();
		}
		
		private UUID getUuid(String s) {
			String s2 = s.replace("-", "");
			return new UUID(
			        new BigInteger(s2.substring(0, 16), 16).longValue(),
			        new BigInteger(s2.substring(16), 16).longValue()); 
		}

		@Override
		public List<AbstractColumn<Person, ? extends Object>> createColumns(List<Person> items) {
			return PersonUI.this.createColumns(this,items);
		}

	}

	@Override
	public DbTable<Person> createDbTableBase() {
		long startTime = System.currentTimeMillis();
		DbTable<Person> table = new PersonDbTable();
		table.setTableName(getTableName());
		Logger.debug.println(String.format("%s - DbTable created in %d ms", getItemClass().getSimpleName(),
				System.currentTimeMillis() - startTime));
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
		return columns;
	}

	@Override
	public ItemEditor<Person> createEditFactory() {
		ItemEditor<Person> editor = new ItemEditor<Person>(getItemClass());
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
		editor.add(new TitledPropertyPane("General", propertyPane));
		editor.addApplyButton();
		return editor;

	}

}
