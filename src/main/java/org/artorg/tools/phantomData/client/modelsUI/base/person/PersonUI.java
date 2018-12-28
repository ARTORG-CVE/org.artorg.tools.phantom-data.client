package org.artorg.tools.phantomData.client.modelsUI.base.person;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.person.AcademicTitle;
import org.artorg.tools.phantomData.server.models.base.person.Gender;
import org.artorg.tools.phantomData.server.models.base.person.Person;
import org.artorg.tools.phantomData.server.util.FxUtil;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

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
		ItemEditor<Person> creator = new ItemEditor<>(getItemClass());
		VBox vBox = new VBox();

		List<PropertyEntry> generalProperties = new ArrayList<>();
		creator.createComboBox(Gender.class)
				.of(item -> item.getGender(), (item, value) -> item.setGender(value))
				.setMapper(g -> g.getName()).addLabeled("Gender", generalProperties);
		creator.createComboBox(AcademicTitle.class)
				.of(item -> item.getAcademicTitle(), (item, value) -> item.setAcademicTitle(value))
				.setMapper(a -> a.getPrefix()).addLabeled("Academic Title", generalProperties);
		creator.createTextField(item -> item.getFirstname(),
				(item, value) -> item.setFirstname(value)).addLabeled("Firstname", generalProperties);
		creator.createTextField(item -> item.getLastname(),
				(item, value) -> item.setLastname(value)).addLabeled("Lastname", generalProperties);
		TitledPane generalPane = creator.createTitledPane(generalProperties, "General");
		vBox.getChildren().add(generalPane);

		vBox.getChildren().add(creator.createButtonPane(creator.getApplyButton()));

		FxUtil.addToPane(creator, vBox);
		return creator;

	}

}
