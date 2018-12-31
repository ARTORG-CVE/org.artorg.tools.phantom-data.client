package org.artorg.tools.phantomData.client.modelsUI.phantom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.column.FilterColumn;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.editor.Creator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyGridPane;
import org.artorg.tools.phantomData.client.exceptions.InvalidUIInputException;
import org.artorg.tools.phantomData.client.exceptions.NoUserLoggedInException;
import org.artorg.tools.phantomData.client.exceptions.PostException;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.modelsUI.phantom.PhantominaUI.PhantominaEditor;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.DbFile;
import org.artorg.tools.phantomData.server.models.measurement.Measurement;
import org.artorg.tools.phantomData.server.models.measurement.Simulation;
import org.artorg.tools.phantomData.server.models.phantom.Manufacturing;
import org.artorg.tools.phantomData.server.models.phantom.Phantom;
import org.artorg.tools.phantomData.server.models.phantom.Phantomina;
import org.artorg.tools.phantomData.server.models.phantom.SimulationPhantom;

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
		PhantominaEditor phantominaEditor =
				(PhantominaEditor) Main.getUIEntity(Phantomina.class).createEditFactory();

		ItemEditor<SimulationPhantom> editor = new ItemEditor<SimulationPhantom>(SimulationPhantom.class) {

			@Override
			public void onCreateInit(SimulationPhantom item) {
				phantominaEditor.showCreateMode();
			}

			@Override
			public void onEditInit(SimulationPhantom item) {
				phantominaEditor.showEditMode(item.getPhantomina());
			}

			@Override
			public void createPropertyGridPanes(Creator<SimulationPhantom> creator) {
				PropertyGridPane<SimulationPhantom> propertyPane =
						new PropertyGridPane<>(SimulationPhantom.class);

				PropertyGridPane<Phantomina> phantominaPropertyPane =
						phantominaEditor.getAllPropertyGridPanes().get(0);
				phantominaPropertyPane.setUntitled();
				propertyPane.addOn(this);

//				Collection<PropertyGridPane> propertyPanes =
//						phantominaEditor.getPropertyGridPanes();
//				propertyPanes.forEach(
//						propertyPane -> creator.addPropertyEntries(propertyPane.getEntries()));

				creator.create(textFieldNumber, item -> Integer.toString(item.getNumber()),
						(item, value) -> item.setNumber(Integer.valueOf(value)))
						.addOn(propertyPane, "Specific Number");
				creator.createTextField(item -> Float.toString(item.getThickness()),
						(item, value) -> item.setThickness(Float.valueOf(value)))
						.addOn(propertyPane, "Nominal thickness");
				propertyPane.setTitled("General");
				propertyPane.addOn(this);
			}

			@Override
			public void createSelectors(Creator<SimulationPhantom> creator) {
				creator.createSelector(DbFile.class, item -> item.getFiles(),
						(item, subItems) -> item.setFiles((List<DbFile>) subItems))
						.setTitled("Files").addOn(this);
				creator.createSelector(Simulation.class, item -> item.getSimulations(),
						(item, subItems) -> item.setSimulations((List<Simulation>) subItems))
						.setTitled("Measurement").addOn(this);
			}

			@Override
			public void onCreateBeforePost(SimulationPhantom item)
					throws NoUserLoggedInException, PostException, InvalidUIInputException {
				ICrudConnector<Phantomina> connector = Connectors.get(Phantomina.class);
				if (item.getPhantomina() == null) throw new RuntimeException();
				if (!connector.exist(item.getPhantomina())) connector.create(item.getPhantomina());
			}

			@Override
			public void onCreateBeforeApplyChanges(SimulationPhantom item)
					throws PostException, InvalidUIInputException, NoUserLoggedInException {
				setPhantomina(item);
			}

			@Override
			public void onEditBeforeApplyChanges(SimulationPhantom item)
					throws PostException, InvalidUIInputException, NoUserLoggedInException {
				setPhantomina(item);
			}

			private void setPhantomina(SimulationPhantom item)
					throws PostException, InvalidUIInputException, NoUserLoggedInException {
				ICrudConnector<Phantomina> connector = Connectors.get(Phantomina.class);
				List<Phantomina> phantominas = connector.readAllAsList();
				String pid = phantominaEditor.getPid();
				Optional<Phantomina> optional =
						phantominas.stream().filter(p -> p.getProductId().equals(pid)).findFirst();
				if (optional.isPresent()) {
					item.setPhantomina(optional.get());
				} else {
					Phantomina phantomina = phantominaEditor.createItem();
					connector.create(phantomina);
					item.setPhantomina(phantomina);
				}
			}

		};
		editor.addApplyButton();
		return editor;

	}

}
