package org.artorg.tools.phantomData.client.modelsUI.base.person;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.Creator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyGridPane;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
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
		ItemEditor<Person> editor = new ItemEditor<Person>(getItemClass()) {

			@Override
			public void createPropertyGridPanes(Creator<Person> creator) {
				PropertyGridPane<Person> propertyPane = new PropertyGridPane<Person>(Person.class);
				creator.createComboBox(Gender.class, item -> item.getGender(),
						(item, value) -> item.setGender(value)).setMapper(g -> g.getName())
						.addOn(propertyPane, "Gender");
				creator.createComboBox(AcademicTitle.class, item -> item.getAcademicTitle(),
						(item, value) -> item.setAcademicTitle(value)).setMapper(a -> a.getPrefix())
						.addOn(propertyPane, "Academic Title");
				creator.createTextField(item -> item.getFirstname(),
						(item, value) -> item.setFirstname(value)).addOn(propertyPane, "Firstname");
				creator.createTextField(item -> item.getLastname(),
						(item, value) -> item.setLastname(value)).addOn(propertyPane, "Lastname");
				propertyPane.setTitled("General");
				propertyPane.addOn(this);
			}

			@Override
			public void createSelectors(Creator<Person> creator) {}

		};
		editor.addApplyButton();

		return editor;

	}

}
