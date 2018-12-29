package org.artorg.tools.phantomData.client.modelsUI.measurement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.person.Person;
import org.artorg.tools.phantomData.server.models.measurement.Project;
import org.artorg.tools.phantomData.server.util.FxUtil;

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
		List<AbstractColumn<Project, ?>> columns = new ArrayList<AbstractColumn<Project, ?>>();
		ColumnCreator<Project, Project> creator = new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Name", path -> path.getName(),
				(path, value) -> path.setName(value)));
		columns.add(creator.createFilterColumn("Description", path -> path.getDescription(),
				(path, value) -> path.setDescription(value)));
		columns.add(creator.createFilterColumn("Start Year",
				path -> Short.toString(path.getStartYear()),
				(path, value) -> path.setStartYear(Short.valueOf(value))));
		columns.add(creator.createFilterColumn("Leader", path -> path.getLeader().toName()));
		columns.add(creator.createFilterColumn("Members", path -> String.valueOf(path.getMembers()
				.stream().map(member -> member.getLastname()).collect(Collectors.joining(", ")))));
		createCountingColumn(table, "Files", columns, item -> item.getFiles());
		createCountingColumn(table, "Notes", columns, item -> item.getNotes());
		createPropertyColumns(table, columns, items);
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditor<Project> createEditFactory() {
		ItemEditor<Project> creator = new ItemEditor<>(getItemClass());
		VBox vBox = new VBox();

		List<PropertyEntry> entries = new ArrayList<>();
		creator.createTextField(item -> item.getName(), (item, value) -> item.setName(value))
				.addLabeled("Prefix", entries);
		creator.createTextField(item -> item.getDescription(),
				(item, value) -> item.setDescription(value)).addLabeled("Description", entries);
		creator.createTextField(item -> Short.toString(item.getStartYear()),
				(item, value) -> item.setStartYear(Short.valueOf((value)))).addLabeled("Start year", entries);
		creator.createComboBox(Person.class)
				.of(item -> item.getLeader(), (item, value) -> item.setLeader(value))
				.setMapper(l -> l.getSimpleAcademicName()).addLabeled("Leader", entries);
		TitledPane generalPane = creator.createTitledPane(entries, "General");
		vBox.getChildren().add(generalPane);

		vBox.getChildren().add(creator.createButtonPane(creator.getApplyButton()));

		FxUtil.addToPane(creator, vBox);
		return creator;
	}

}
