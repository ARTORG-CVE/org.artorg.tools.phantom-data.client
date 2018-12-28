package org.artorg.tools.phantomData.client.modelsUI.base.person;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.FxFactory;
import org.artorg.tools.phantomData.client.editor.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.editor2.ItemEditor;
import org.artorg.tools.phantomData.client.editor2.PropertyNode;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.person.AcademicTitle;
import org.artorg.tools.phantomData.server.models.base.person.Gender;
import org.artorg.tools.phantomData.server.models.base.person.Person;
import org.artorg.tools.phantomData.server.util.FxUtil;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
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
	public FxFactory<Person> createEditFactory() {
		ItemEditor<Person> creator = new ItemEditor<>(getItemClass());
		VBox vBox = new VBox();

		List<PropertyEntry> generalProperties = new ArrayList<>();
		creator.createComboBox(Gender.class).of(
				(item, value) -> item.setGender(value), item -> item.getGender(), g -> g.getName()).addLabeled("Gender", generalProperties);
		creator.createComboBox(AcademicTitle.class).of(
				(item, value) -> item.setAcademicTitle(value), item -> item.getAcademicTitle(),
				g -> g.getPrefix()).addLabeled("Academic Title", generalProperties);
		creator.createTextField((item, value) -> item.setFirstname(value),
				item -> item.getFirstname()).addLabeled("Firstname", generalProperties);
		creator.createTextField((item, value) -> item.setLastname(value),
				item -> item.getLastname()).addLabeled("Lastname", generalProperties);
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		vBox.getChildren().add(generalPane);

		vBox.getChildren().add(creator.createButtonPane(creator.getApplyButton()));

		FxUtil.addToPane(creator, vBox);
		return creator;

	}

}
