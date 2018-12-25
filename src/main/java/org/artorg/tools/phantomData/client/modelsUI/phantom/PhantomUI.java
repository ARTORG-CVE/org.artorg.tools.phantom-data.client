package org.artorg.tools.phantomData.client.modelsUI.phantom;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.column.FilterColumn;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.editor.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.phantom.AnnulusDiameter;
import org.artorg.tools.phantomData.server.models.phantom.FabricationType;
import org.artorg.tools.phantomData.server.models.phantom.LiteratureBase;
import org.artorg.tools.phantomData.server.models.phantom.Manufacturing;
import org.artorg.tools.phantomData.server.models.phantom.Phantom;
import org.artorg.tools.phantomData.server.models.phantom.Phantomina;
import org.artorg.tools.phantomData.server.models.phantom.Special;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class PhantomUI extends UIEntity<Phantom> {

	public Class<Phantom> getItemClass() {
		return Phantom.class;
	}

	@Override
	public String getTableName() {
		return "Phantoms";
	}

	@Override
	public List<AbstractColumn<Phantom, ?>> createColumns(Table<Phantom> table, List<Phantom> items) {
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
	public ItemEditFactoryController<Phantom> createEditFactory() {
		return new PhantomEditFactoryController();
	}

	private class PhantomEditFactoryController extends GroupedItemEditFactoryController<Phantom> {
		private Label labelIdValue;
		private ComboBox<AnnulusDiameter> comboBoxAnnulus;
		private ComboBox<FabricationType> comboBoxFabricationType;
		private ComboBox<LiteratureBase> comboBoxLiterature;
		private ComboBox<Special> comboBoxSpecials;
		private TextField textFieldModelNumber;
		private ComboBox<Manufacturing> comboBoxManufacturing;
		private TextField textFieldThickness;

		{
			labelIdValue = new Label("id");
			comboBoxAnnulus = new ComboBox<AnnulusDiameter>();
			comboBoxFabricationType = new ComboBox<FabricationType>();
			comboBoxLiterature = new ComboBox<LiteratureBase>();
			comboBoxSpecials = new ComboBox<Special>();
			textFieldModelNumber = new TextField();
			comboBoxManufacturing = new ComboBox<>();
			textFieldThickness = new TextField();

			labelIdValue.setDisable(true);

			List<TitledPane> panes = new ArrayList<TitledPane>();
			createComboBoxes();
			List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
			generalProperties.add(new PropertyEntry("PID", labelIdValue));
			generalProperties.add(new PropertyEntry("Annulus diameter [mm]", comboBoxAnnulus));
			generalProperties.add(new PropertyEntry("Fabrication Type", comboBoxFabricationType));
			generalProperties.add(new PropertyEntry("Literarure Base", comboBoxLiterature));
			generalProperties.add(new PropertyEntry("Special", comboBoxSpecials));
			generalProperties.add(new PropertyEntry("Phantom specific Number", textFieldModelNumber,
					() -> updateId()));
			generalProperties.add(new PropertyEntry("Manufacturing", comboBoxManufacturing));
			generalProperties.add(new PropertyEntry("Nominal thickness", textFieldThickness));
			TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
			panes.add(generalPane);
			setTitledPanes(panes);
		}

		private void updateId() {
			AnnulusDiameter annulusDiameter = comboBoxAnnulus.getSelectionModel().getSelectedItem();
			FabricationType fabricationType =
					comboBoxFabricationType.getSelectionModel().getSelectedItem();
			LiteratureBase literatureBase =
					comboBoxLiterature.getSelectionModel().getSelectedItem();
			Special special = comboBoxSpecials.getSelectionModel().getSelectedItem();
			String sNumber = textFieldModelNumber.getText();
			int number;
			if (sNumber.isEmpty()) number = 0;
			else
				number = Integer.valueOf(sNumber);

			String pid = Phantom.createProductId(
					new Phantomina(annulusDiameter, fabricationType, literatureBase, special),
					number);

			try {
				labelIdValue.setText(pid);
			} catch (Exception e) {}
		}

		private void createComboBoxes() {
			createComboBox(comboBoxAnnulus, AnnulusDiameter.class,
					d -> String.valueOf(d.getValue()), item -> updateId());
			createComboBox(comboBoxFabricationType, FabricationType.class, f -> f.getValue(),
					item -> updateId());
			createComboBox(comboBoxLiterature, LiteratureBase.class, l -> l.getValue(),
					item -> updateId());
			createComboBox(comboBoxSpecials, Special.class, s -> s.getShortcut(),
					item -> updateId());
			createComboBox(comboBoxManufacturing, Manufacturing.class, s -> s.getName());
		}
		
		@Override
		public Phantom createItem() {
			AnnulusDiameter annulusDiameter = comboBoxAnnulus.getSelectionModel().getSelectedItem();
			FabricationType fabricationType =
					comboBoxFabricationType.getSelectionModel().getSelectedItem();
			LiteratureBase literatureBase =
					comboBoxLiterature.getSelectionModel().getSelectedItem();
			Special special = comboBoxSpecials.getSelectionModel().getSelectedItem();
			String sNumber = textFieldModelNumber.getText();
			int number = Integer.valueOf(sNumber);
			Manufacturing manufacturing =
					comboBoxManufacturing.getSelectionModel().getSelectedItem();
			float thickness = Float.valueOf(textFieldThickness.getText());

			ICrudConnector<Phantomina> phantominaConn = Connectors.getConnector(Phantomina.class);
			Phantomina phantomina =
					new Phantomina(annulusDiameter, fabricationType, literatureBase, special);
			final Phantomina finalPhantomina = phantomina;
			List<Phantomina> phantominas = phantominaConn.readAllAsStream()
					.filter(p -> p.getProductId().equals(finalPhantomina.getProductId()))
					.collect(Collectors.toList());
			if (phantominas.size() == 0) phantominaConn.create(phantomina);
			else if (phantominas.size() == 1) phantomina = phantominas.get(0);
			else {
				phantomina = phantominas.get(0);
				throw new UnsupportedOperationException();
			}

			phantomina.setAnnulusDiameter(annulusDiameter);
			phantomina.setFabricationType(fabricationType);
			phantomina.setLiteratureBase(literatureBase);
			phantomina.setSpecial(special);

			return new Phantom(phantomina, number, manufacturing, thickness);
		}

		@Override
		protected void setEditTemplate(Phantom item) {
			super.selectComboBoxItem(comboBoxAnnulus, item.getPhantomina().getAnnulusDiameter());
			super.selectComboBoxItem(comboBoxFabricationType,
					item.getPhantomina().getFabricationType());
			super.selectComboBoxItem(comboBoxLiterature, item.getPhantomina().getLiteratureBase());
			super.selectComboBoxItem(comboBoxSpecials, item.getPhantomina().getSpecial());

			super.selectComboBoxItem(comboBoxManufacturing, item.getManufacturing());
			textFieldModelNumber.setText(Integer.toString(item.getNumber()));
			textFieldThickness.setText(Float.toString(item.getThickness()));
		}

		@Override
		protected void applyChanges(Phantom item) {
			AnnulusDiameter annulusDiameter = comboBoxAnnulus.getSelectionModel().getSelectedItem();
			FabricationType fabricationType =
					comboBoxFabricationType.getSelectionModel().getSelectedItem();
			LiteratureBase literatureBase =
					comboBoxLiterature.getSelectionModel().getSelectedItem();
			Special special = comboBoxSpecials.getSelectionModel().getSelectedItem();
			String sNumber = textFieldModelNumber.getText();
			int number = Integer.valueOf(sNumber);
			Manufacturing manufacturing =
					comboBoxManufacturing.getSelectionModel().getSelectedItem();
			float thickness = Float.valueOf(textFieldThickness.getText());

			item.getPhantomina().setAnnulusDiameter(annulusDiameter);
			item.getPhantomina().setFabricationType(fabricationType);
			item.getPhantomina().setLiteratureBase(literatureBase);
			item.getPhantomina().setSpecial(special);
			item.setNumber(number);
			item.setManufacturing(manufacturing);
			item.setThickness(thickness);
		}

		@Override
		public void setDefaultTemplate() {
			comboBoxAnnulus.getSelectionModel().clearSelection();
			comboBoxFabricationType.getSelectionModel().clearSelection();
			comboBoxLiterature.getSelectionModel().clearSelection();
			comboBoxSpecials.getSelectionModel().clearSelection();

			comboBoxManufacturing.getSelectionModel().clearSelection();
			textFieldModelNumber.setText("1");
			textFieldThickness.setText("");
		}

	}

}
