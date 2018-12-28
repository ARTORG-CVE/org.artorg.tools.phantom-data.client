package org.artorg.tools.phantomData.client.modelsUI.measurement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.PropertyNode;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.DbFile;
import org.artorg.tools.phantomData.server.models.base.person.Person;
import org.artorg.tools.phantomData.server.models.measurement.ExperimentalSetup;
import org.artorg.tools.phantomData.server.models.measurement.Measurement;
import org.artorg.tools.phantomData.server.models.measurement.Project;
import org.artorg.tools.phantomData.server.util.FxUtil;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

public class MeasurementUI extends UIEntity<Measurement> {

	public Class<Measurement> getItemClass() {
		return Measurement.class;
	}

	private static final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

	@Override
	public String getTableName() {
		return "Measurement";
	}

	@Override
	public List<AbstractColumn<Measurement, ?>> createColumns(Table<Measurement> table,
			List<Measurement> items) {
		List<AbstractColumn<Measurement, ?>> columns = new ArrayList<>();
		ColumnCreator<Measurement, Measurement> creator = new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Date", path -> format.format(path.getStartDate()),
				(path, value) -> {
					try {
						path.setStartDate(format.parse(value));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}));
		columns.add(creator.createFilterColumn("Person", path -> path.getPerson().toName()));
		columns.add(creator.createFilterColumn("Project", path -> path.getProject().toName()));
		columns.add(creator.createFilterColumn("Experimental Setup",
				path -> path.getExperimentalSetup().getShortName()));
		createCountingColumn(table, "Files", columns, item -> item.getFiles());
		createCountingColumn(table, "Notes", columns, item -> item.getNotes());
		createPropertyColumns(table, columns, items);
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditor<Measurement> createEditFactory() {
		ItemEditor<Measurement> creator = new ItemEditor<>(getItemClass());
		VBox vBox = new VBox();

		List<PropertyEntry> generalProperties = new ArrayList<>();
		creator.createDatePicker((item, value) -> item.setStartDate(value),
				item -> item.getStartDate()).addLabeled("Start date", generalProperties);
		creator.createComboBox(Person.class)
				.of(item -> item.getPerson(), (item, value) -> item.setPerson(value))
				.setMapper(p -> p.getName()).addLabeled("Person", generalProperties);
		creator.createComboBox(Project.class)
				.of(item -> item.getProject(), (item, value) -> item.setProject(value))
				.setMapper(p -> p.getName()).addLabeled("Project", generalProperties);
		creator.createComboBox(ExperimentalSetup.class)
				.of(item -> item.getExperimentalSetup(),
						(item, value) -> item.setExperimentalSetup(value))
				.setMapper(p -> p.getShortName())
				.addLabeled("Experimental Setup", generalProperties);
		TitledPane generalPane = creator.createTitledPane(generalProperties, "General");
		vBox.getChildren().add(generalPane);

		PropertyNode<Measurement, ?> selector;
		selector = creator.createSelector(DbFile.class).titled("Protocol Files",
				item -> item.getFiles(), (item, files) -> item.setFiles((List<DbFile>) files));
		vBox.getChildren().add(selector.getParentNode());
		selector = creator.createSelector(DbFile.class).titled("Files", item -> item.getFiles(),
				(item, files) -> item.setFiles((List<DbFile>) files));
		vBox.getChildren().add(selector.getParentNode());

		vBox.getChildren().add(creator.createButtonPane(creator.getApplyButton()));

		FxUtil.addToPane(creator, vBox);
		return creator;
	}

}
