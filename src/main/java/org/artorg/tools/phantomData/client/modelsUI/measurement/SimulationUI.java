package org.artorg.tools.phantomData.client.modelsUI.measurement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.admin.UserAdmin;
import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyGridPane;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.models.base.DbFile;
import org.artorg.tools.phantomData.server.models.base.person.Person;
import org.artorg.tools.phantomData.server.models.measurement.Project;
import org.artorg.tools.phantomData.server.models.measurement.Simulation;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

public class SimulationUI extends UIEntity<Simulation> {

	public Class<Simulation> getItemClass() {
		return Simulation.class;
	}

	private static final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

	@Override
	public String getTableName() {
		return "Simulations";
	}

	@Override
	public List<AbstractColumn<Simulation, ?>> createColumns(Table<Simulation> table,
			List<Simulation> items) {
		List<AbstractColumn<Simulation, ?>> columns = new ArrayList<>();
		ColumnCreator<Simulation, Simulation> editor = new ColumnCreator<>(table);
		columns.add(editor.createFilterColumn("Date", path -> format.format(path.getStartDate()),
				(path, value) -> {
					try {
						path.setStartDate(format.parse(value));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}));
		columns.add(editor.createFilterColumn("Person", path -> path.getPerson().toName()));
		columns.add(editor.createFilterColumn("Project", path -> path.getProject().toName()));
		createCountingColumn(table, "Files", columns, item -> item.getFiles());
		createPropertyColumns(table, columns, items);
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditor<Simulation> createEditFactory() {
		ItemEditor<Simulation> editor = new ItemEditor<>(getItemClass());
		ComboBox<Person> comboBoxPersons = new ComboBox<Person>();
		List<Person> persons = Connectors.get(Person.class).readAllAsList().stream()
				.filter(person -> !person.equalsId(UserAdmin.getAdmin()))
				.collect(Collectors.toList());
		comboBoxPersons.setItems(FXCollections.observableArrayList(persons));
		FxUtil.setComboBoxCellFactory(comboBoxPersons, person -> person.getName());

		PropertyGridPane propertyPane = new PropertyGridPane();
		propertyPane.addEntry("Start Date", editor.createDatePicker(
				(item, value) -> item.setStartDate(value), item -> item.getStartDate()));
		propertyPane.addEntry("Person", editor.create(comboBoxPersons, item -> item.getPerson(),
				(item, value) -> item.setPerson(value)));
		propertyPane
				.addEntry("Project",
						editor.createComboBox(Project.class, item -> item.getProject(),
								(item, value) -> item.setProject(value))
								.setMapper(p -> p.getName()));
		editor.add(new TitledPropertyPane("General", propertyPane));
		editor.add(new TitledPropertyPane("Files", editor.createSelector(DbFile.class,
				item -> item.getFiles(), (item, files) -> item.setFiles((List<DbFile>) files))));
		editor.add(new TitledPropertyPane("Properties", editor.createPropertySelector()));

		editor.closeTitledNonGeneralPanes();
		editor.addAutoCloseOnNonGeneral();
		editor.addApplyButton();
		return editor;
	}

}
