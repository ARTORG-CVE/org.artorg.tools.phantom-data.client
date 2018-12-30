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
import org.artorg.tools.phantomData.server.models.phantom.Manufacturing;
import org.artorg.tools.phantomData.server.models.phantom.Phantom;
import org.artorg.tools.phantomData.server.models.phantom.Phantomina;

import javafx.scene.control.TextField;

public class PhantomUI extends UIEntity<Phantom> {

	public Class<Phantom> getItemClass() {
		return Phantom.class;
	}

	@Override
	public String getTableName() {
		return "Phantoms";
	}

	@Override
	public List<AbstractColumn<Phantom, ?>> createColumns(Table<Phantom> table,
			List<Phantom> items) {
		List<AbstractColumn<Phantom, ?>> columns = new ArrayList<>();
		FilterColumn<Phantom, ?, ?> column;
		ColumnCreator<Phantom, Phantom> creator = new ColumnCreator<>(table);
		ColumnCreator<Phantom, Phantomina> creatorP =
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
		columns.add(creator.createFilterColumn("Manufacturing",
				path -> path.getManufacturing().getName(),
				(path, value) -> path.getManufacturing().setName(value)));
		columns.add(
				creator.createFilterColumn("Thickness", path -> Float.toString(path.getThickness()),
						(path, value) -> path.setThickness(Float.valueOf(value))));
		createCountingColumn(table, "Files", columns, item -> item.getFiles());
		createCountingColumn(table, "Measurements", columns, item -> item.getMeasurements());
		createCountingColumn(table, "Notes", columns, item -> item.getNotes());
		createPropertyColumns(table, columns, items);
		createPersonifiedColumns(table, columns);

		column.setAscendingSortComparator(
				(p1, p2) -> ((Integer) p1.getNumber()).compareTo((Integer) p2.getNumber()));
		return columns;
	}

	@Override
	public ItemEditor<Phantom> createEditFactory() {
		TextField textFieldNumber = new TextField();
		PhantominaEditor phantominaEditor =
				(PhantominaEditor) Main.getUIEntity(Phantomina.class).createEditFactory();
		
		ItemEditor<Phantom> editor = new ItemEditor<Phantom>(Phantom.class) {

			@Override
			public void onCreateInit(Phantom item) {
				phantominaEditor.showCreateMode();
			}

			@Override
			public void onEditInit(Phantom item) {
				phantominaEditor.showEditMode(item.getPhantomina());
			}

			@Override
			public void createPropertyGridPanes(Creator<Phantom> creator) {
				Collection<PropertyGridPane> propertyPanes =
						phantominaEditor.getCreator().getPropertyGridPanes();
				propertyPanes.forEach(propertyPane -> creator
						.addPropertyEntries(propertyPane.getPropertyEntries()));

				creator.createTextField(textFieldNumber, item -> Integer.toString(item.getNumber()),
						(item, value) -> item.setNumber(Integer.valueOf(value)))
						.addLabeled("Specific Number");
				creator.createComboBox(Manufacturing.class)
						.of(item -> item.getManufacturing(),
								(item, value) -> item.setManufacturing(value))
						.setMapper(m -> m.getName()).addLabeled("Manufacturing");
				creator.createTextField(item -> Float.toString(item.getThickness()),
						(item, value) -> item.setThickness(Float.valueOf(value)))
						.addLabeled("Nominal thickness");
				creator.addTitledPropertyPane("General");
			}

			@Override
			public void createSelectors(Creator<Phantom> creator) {
				creator.addSelector("Files", DbFile.class, item -> item.getFiles(),
						(item, subItems) -> item.setFiles((List<DbFile>) subItems));
				creator.addSelector("Measurement", Measurement.class,
						item -> item.getMeasurements(),
						(item, subItems) -> item.setMeasurements((List<Measurement>) subItems));
			}

			@Override
			public void onCreateBeforePost(Phantom item)
					throws NoUserLoggedInException, PostException, InvalidUIInputException {
				ICrudConnector<Phantomina> connector = Connectors.get(Phantomina.class);
				if (item.getPhantomina() == null) throw new RuntimeException();
				if (!connector.exist(item.getPhantomina())) connector.create(item.getPhantomina());
			}

			@Override
			public void onCreateBeforeApplyChanges(Phantom item)
					throws PostException, InvalidUIInputException, NoUserLoggedInException {
				setPhantomina(item);
			}

			@Override
			public void onEditBeforeApplyChanges(Phantom item)
					throws PostException, InvalidUIInputException, NoUserLoggedInException {
				setPhantomina(item);
			}
			
			private void setPhantomina(Phantom item) throws PostException, InvalidUIInputException, NoUserLoggedInException {
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

//		Label labelIdValue = new Label("id");
//		ComboBox<AnnulusDiameter> comboBoxAnnulusDiameter = new ComboBox<>();
//		ComboBox<FabricationType> comboBoxFabricationType = new ComboBox<>();
//		ComboBox<LiteratureBase> comboBoxLiteratureBase = new ComboBox<>();
//		ComboBox<Special> comboBoxSpecial = new ComboBox<>();
//		TextField textFieldNumber = new TextField();
//
//		Supplier<String> phantominaPidSupplier = () -> {
//			return Phantomina.createProductId(
//					comboBoxAnnulusDiameter.getSelectionModel().getSelectedItem(),
//					comboBoxFabricationType.getSelectionModel().getSelectedItem(),
//					comboBoxLiteratureBase.getSelectionModel().getSelectedItem(),
//					comboBoxSpecial.getSelectionModel().getSelectedItem());
//		};
//		Supplier<String> idSupplier = () -> {
//			String sNumber = textFieldNumber.getText();
//			int number;
//			if (sNumber.isEmpty()) number = 0;
//			else
//				number = Integer.valueOf(sNumber);
//			String phantominaProductId = phantominaPidSupplier.get();
//			return Phantom.createProductId(phantominaProductId, number);
//		};
//		Runnable updateId = () -> labelIdValue.setText(idSupplier.get());
//
//		ItemEditor<Phantom> creator = new ItemEditor<Phantom>(getItemClass()) {
//
//			@Override
//			public void createPropertyGridPanes(Creator<Phantom> creator) {
//				creator.addPropertyEntry(new PropertyEntry("PID", labelIdValue));
//				creator.createComboBox(AnnulusDiameter.class, comboBoxAnnulusDiameter)
//						.of(item -> item.getPhantomina().getAnnulusDiameter(),
//								(item, value) -> item.getPhantomina().setAnnulusDiameter(value))
//						.setMapper(a -> String.valueOf(a.getValue()))
//						.addLabeled("Annulus diameter");
//				creator.createComboBox(FabricationType.class, comboBoxFabricationType)
//						.of(item -> item.getPhantomina().getFabricationType(),
//								(item, value) -> item.getPhantomina().setFabricationType(value))
//						.setMapper(f -> f.getValue()).addLabeled("Fabrication type");
//				creator.createComboBox(LiteratureBase.class, comboBoxLiteratureBase)
//						.of(item -> item.getPhantomina().getLiteratureBase(),
//								(item, value) -> item.getPhantomina().setLiteratureBase(value))
//						.setMapper(l -> l.getValue()).addLabeled("Literature type");
//				creator.createComboBox(Special.class, comboBoxSpecial)
//						.of(item -> item.getPhantomina().getSpecial(),
//								(item, value) -> item.getPhantomina().setSpecial(value))
//						.setMapper(s -> s.getShortcut()).addLabeled("Special");
//				creator.createTextField(textFieldNumber, item -> Integer.toString(item.getNumber()),
//						(item, value) -> item.setNumber(Integer.valueOf(value)))
//						.addLabeled("Phantom specific Number");
//				creator.createComboBox(Manufacturing.class)
//						.of(item -> item.getManufacturing(),
//								(item, value) -> item.setManufacturing(value))
//						.setMapper(m -> m.getName()).addLabeled("Manufacturing");
//				creator.createTextField(item -> Float.toString(item.getThickness()),
//						(item, value) -> item.setThickness(Float.valueOf(value)))
//						.addLabeled("Nominal thickness");
//				creator.addTitledPropertyPane("General");
//			}
//
//			@Override
//			public void createSelectors(Creator<Phantom> creator) {
//				creator.addSelector("Files", DbFile.class, item -> item.getFiles(),
//						(item, subItems) -> item.setFiles((List<DbFile>) subItems));
//				creator.addSelector("Measurement", Measurement.class,
//						item -> item.getMeasurements(),
//						(item, subItems) -> item.setMeasurements((List<Measurement>) subItems));
//			}
//
//		};
//		comboBoxAnnulusDiameter.getSelectionModel().selectedItemProperty()
//				.addListener((observable, oldValue, newValue) -> updateId.run());
//		comboBoxFabricationType.getSelectionModel().selectedItemProperty()
//				.addListener((observable, oldValue, newValue) -> updateId.run());
//		comboBoxLiteratureBase.getSelectionModel().selectedItemProperty()
//				.addListener((observable, oldValue, newValue) -> updateId.run());
//		comboBoxSpecial.getSelectionModel().selectedItemProperty()
//				.addListener((observable, oldValue, newValue) -> updateId.run());
//
//		creator.addApplyButton();

		editor.addApplyButton();
		return editor;

	}

}
