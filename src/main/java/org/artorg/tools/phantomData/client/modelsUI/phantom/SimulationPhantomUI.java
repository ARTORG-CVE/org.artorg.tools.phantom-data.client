package org.artorg.tools.phantomData.client.modelsUI.phantom;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.column.FilterColumn;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyGridPane;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.exceptions.NoUserLoggedInException;
import org.artorg.tools.phantomData.client.exceptions.PostException;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.DbFile;
import org.artorg.tools.phantomData.server.models.measurement.Simulation;
import org.artorg.tools.phantomData.server.models.phantom.AnnulusDiameter;
import org.artorg.tools.phantomData.server.models.phantom.FabricationType;
import org.artorg.tools.phantomData.server.models.phantom.LiteratureBase;
import org.artorg.tools.phantomData.server.models.phantom.Phantom;
import org.artorg.tools.phantomData.server.models.phantom.Phantomina;
import org.artorg.tools.phantomData.server.models.phantom.SimulationPhantom;
import org.artorg.tools.phantomData.server.models.phantom.Special;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class SimulationPhantomUI extends UIEntity<SimulationPhantom> {

	public Class<SimulationPhantom> getItemClass() {
		return SimulationPhantom.class;
	}

	@Override
	public String getTableName() {
		return "Simulation Phantoms";
	}

	@Override
	public List<AbstractColumn<SimulationPhantom, ?>> createColumns(Table<SimulationPhantom> table,
			List<SimulationPhantom> items) {
		List<AbstractColumn<SimulationPhantom, ?>> columns = new ArrayList<>();
		FilterColumn<SimulationPhantom, ?, ?> column;
		ColumnCreator<SimulationPhantom, SimulationPhantom> creator = new ColumnCreator<>(table);
		ColumnCreator<SimulationPhantom, Phantomina> creatorP =
				new ColumnCreator<>(table, item -> item.getPhantomina());
		column = creator.createFilterColumn("PID", path -> path.getProductId(),
				(path, value) -> path.setProductId(value));
		column.setAscendingSortComparator(
				(p1, p2) -> Phantomina.comparePid(p1.getProductId(), p2.getProductId()));
		column.setItemsFilter(false);
		columns.add(column);
		columns.add(creatorP.createFilterColumn("Annulus [mm]",
				path -> String.valueOf(path.getAnnulusDiameter().getValue()),
				(path, value) -> path.getAnnulusDiameter().setValue(Double.valueOf(value))));
		columns.add(
				creatorP.createFilterColumn("Type", path -> path.getFabricationType().getValue(),
						(path, value) -> path.getFabricationType().setValue(value)));
		columns.add(creatorP.createFilterColumn("Literature",
				path -> path.getLiteratureBase().getValue(),
				(path, value) -> path.getLiteratureBase().setValue(value)));
		columns.add(creatorP.createFilterColumn("Special", path -> path.getSpecial().getShortcut(),
				(path, value) -> path.getSpecial().setShortcut(value)));
		column = creator.createFilterColumn("Number", path -> String.valueOf(path.getNumber()),
				(path, value) -> path.setNumber(Integer.valueOf(value)));
		column.setItemsFilter(false);
		columns.add(column);
		columns.add(
				creator.createFilterColumn("Thickness", path -> Float.toString(path.getThickness()),
						(path, value) -> path.setThickness(Float.valueOf(value))));
		createCountingColumn(table, "Simulations", columns, item -> item.getSimulations());
		createCountingColumn(table, "Files", columns, item -> item.getFiles());
		createCountingColumn(table, "Notes", columns, item -> item.getNotes());
		createPropertyColumns(table, columns, items);
		createPersonifiedColumns(table, columns);

		column.setAscendingSortComparator(
				(p1, p2) -> ((Integer) p1.getNumber()).compareTo((Integer) p2.getNumber()));
		return columns;
	}

	@Override
	public ItemEditor<SimulationPhantom> createEditFactory() {
		TextField textFieldNumber = new TextField();
		Label labelIdValue = new Label();

		ComboBox<AnnulusDiameter> comboBoxAnnulusDiameter = new ComboBox<>();
		ComboBox<FabricationType> comboBoxFabricationType = new ComboBox<>();
		ComboBox<LiteratureBase> comboBoxLiteratureBase = new ComboBox<>();
		ComboBox<Special> comboBoxSpecial = new ComboBox<>();

		ItemEditor<SimulationPhantom> editor = new ItemEditor<SimulationPhantom>(SimulationPhantom.class) {

			@Override
			public void onCreatingServer(SimulationPhantom item) throws NoUserLoggedInException, PostException  {
				setPhantomina(item);
			}

			@Override
			public void onUpdatingServer(SimulationPhantom item)
					throws NoUserLoggedInException, PostException {
				setPhantomina(item);
			}

			private void setPhantomina(SimulationPhantom item) throws PostException, NoUserLoggedInException {
				ICrudConnector<Phantomina> connector = Connectors.get(Phantomina.class);
				List<Phantomina> phantominas = connector.readAllAsList();

				String pid = Phantomina.createProductId(
						comboBoxAnnulusDiameter.getSelectionModel().getSelectedItem(),
						comboBoxFabricationType.getSelectionModel().getSelectedItem(),
						comboBoxLiteratureBase.getSelectionModel().getSelectedItem(),
						comboBoxSpecial.getSelectionModel().getSelectedItem());
				Optional<Phantomina> optional =
						phantominas.stream().filter(p -> p.getProductId().equals(pid)).findFirst();
				if (optional.isPresent()) {
					item.setPhantomina(optional.get());
				} else {
					Phantomina phantomina = new Phantomina(
							comboBoxAnnulusDiameter.getSelectionModel().getSelectedItem(),
							comboBoxFabricationType.getSelectionModel().getSelectedItem(),
							comboBoxLiteratureBase.getSelectionModel().getSelectedItem(),
							comboBoxSpecial.getSelectionModel().getSelectedItem());
					connector.create(phantomina);
					item.setPhantomina(phantomina);
				}
			}

		};

		PropertyGridPane propertyPane = new PropertyGridPane();
		propertyPane.addEntry(new Label("PID"), labelIdValue);
		propertyPane.addEntry("Annulus Diameter",
				editor.createComboBox(comboBoxAnnulusDiameter, AnnulusDiameter.class,
						item -> item.getPhantomina().getAnnulusDiameter(), (item, value) -> {})
						.setMapper(a -> String.valueOf(a.getValue())));
		propertyPane.addEntry("Fabrication Type",
				editor.createComboBox(comboBoxFabricationType, FabricationType.class,
						item -> item.getPhantomina().getFabricationType(), (item, value) -> {})
						.setMapper(f -> String.format("(%s) %s", f.getShortcut(), f.getValue())));
		propertyPane.addEntry("Literautre Base",
				editor.createComboBox(comboBoxLiteratureBase, LiteratureBase.class,
						item -> item.getPhantomina().getLiteratureBase(), (item, value) -> {})
						.setMapper(f -> String.format("(%s) %s", f.getShortcut(), f.getValue())));
		propertyPane.addEntry("Special", editor
				.createComboBox(comboBoxSpecial, Special.class,
						item -> item.getPhantomina().getSpecial(), (item, value) -> {})
				.setMapper(s -> String.format("(%s) %s", s.getShortcut(), s.getDescription())));
		propertyPane.addEntry("Specific Number",
				editor.create(textFieldNumber, item -> Integer.toString(item.getNumber()),
						(item, value) -> item.setNumber(Integer.valueOf(value))));
		propertyPane.addEntry("Nominal thickness",
				editor.createTextField(item -> Float.toString(item.getThickness()),
						(item, value) -> item.setThickness(Float.valueOf(value))));
		editor.add(new TitledPropertyPane("General", propertyPane));

		editor.add(new TitledPropertyPane("Files",
				editor.createSelector(DbFile.class, item -> item.getFiles(),
						(item, subItems) -> item.setFiles((List<DbFile>) subItems))));
		editor.add(new TitledPropertyPane("Simulations",
				editor.createSelector(Simulation.class, item -> item.getSimulations(),
						(item, subItems) -> item.setSimulations((List<Simulation>) subItems))));
		editor.add(new TitledPropertyPane("Properties", editor.createPropertySelector()));

		Runnable updateId = () -> {
			String phantominaPid = Phantomina.createProductId(
					comboBoxAnnulusDiameter.getSelectionModel().getSelectedItem(),
					comboBoxFabricationType.getSelectionModel().getSelectedItem(),
					comboBoxLiteratureBase.getSelectionModel().getSelectedItem(),
					comboBoxSpecial.getSelectionModel().getSelectedItem());
			String text = textFieldNumber.getText();
			if (text.isEmpty()) return;
			try {
				int number = Integer.valueOf(text);
				labelIdValue.setText(Phantom.createProductId(phantominaPid, number));
			} catch (NumberFormatException e) {}
		};

		comboBoxAnnulusDiameter.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> updateId.run());
		comboBoxFabricationType.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> updateId.run());
		comboBoxLiteratureBase.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> updateId.run());
		comboBoxSpecial.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> updateId.run());

		editor.closeTitledNonGeneralPanes();
		editor.addAutoCloseOnNonGeneral();
		editor.addApplyButton();
		return editor;

	}

}