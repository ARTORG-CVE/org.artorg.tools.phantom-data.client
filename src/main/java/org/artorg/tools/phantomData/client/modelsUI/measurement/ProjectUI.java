package org.artorg.tools.phantomData.client.modelsUI.measurement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import org.artorg.tools.phantomData.server.models.base.person.Person;
import org.artorg.tools.phantomData.server.models.measurement.Project;
import org.artorg.tools.phantomData.server.util.FxUtil;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

public class ProjectUI extends UIEntity<Project> {

	public Class<Project> getItemClass() {
		return Project.class;
	}

	@Override
	public String getTableName() {
		return "Projects";
	}

	@Override
	public List<AbstractColumn<Project, ?>> createColumns(Table<Project> table,
		List<Project> items) {
		List<AbstractColumn<Project, ?>> columns =
			new ArrayList<AbstractColumn<Project, ?>>();
		ColumnCreator<Project, Project> creator = new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Name", path -> path.getName(),
			(path, value) -> path.setName(value)));
		columns.add(creator.createFilterColumn("Description",
			path -> path.getDescription(), (path, value) -> path.setDescription(value)));
		columns.add(creator.createFilterColumn("Start Year",
			path -> Short.toString(path.getStartYear()),
			(path, value) -> path.setStartYear(Short.valueOf(value))));
		columns
			.add(creator.createFilterColumn("Leader", path -> path.getLeader().toName()));
		columns.add(creator.createFilterColumn("Members",
			path -> String.valueOf(path.getMembers().stream()
				.map(member -> member.getLastname()).collect(Collectors.joining(", ")))));
		createCountingColumn(table, "Files", columns, item -> item.getFiles());
		createCountingColumn(table, "Notes", columns, item -> item.getNotes());
		createPropertyColumns(table, columns, items);
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public FxFactory<Project> createEditFactory() {
		ItemEditor<Project> creator = new ItemEditor<>(getItemClass());
		VBox vBox = new VBox();

		List<PropertyEntry> entries = new ArrayList<>();
		creator.createTextField((item, value) -> item.setName(value),
			item -> item.getName()).addLabeled("Prefix", entries);
		creator.createTextField(
			(item, value) -> item.setDescription(value), item -> item.getDescription()).addLabeled("Description", entries);
		creator.createTextField(
			(item, value) -> item.setStartYear(Short.valueOf((value))),
			item -> Short.toString(item.getStartYear())).addLabeled("Description", entries);
		creator.createComboBox(Person.class).of(
			(item, value) -> item.setLeader(value), item -> item.getLeader(),
			l -> l.getSimpleAcademicName()).addLabeled("Leader", entries);
		TitledPropertyPane generalPane =
			new TitledPropertyPane(entries, "General");
		vBox.getChildren().add(generalPane);

		vBox.getChildren().add(creator.createButtonPane(creator.getApplyButton()));

		FxUtil.addToPane(creator, vBox);
		return creator;
	}

}
