package org.artorg.tools.phantomData.client.modelsUI.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.FilterColumn;
import org.artorg.tools.phantomData.client.editor.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.util.ColumnUtils;
import org.artorg.tools.phantomData.server.models.phantom.AnnulusDiameter;
import org.artorg.tools.phantomData.server.models.phantom.FabricationType;
import org.artorg.tools.phantomData.server.models.phantom.LiteratureBase;
import org.artorg.tools.phantomData.server.models.phantom.Phantomina;
import org.artorg.tools.phantomData.server.models.phantom.Special;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;

public class PhantominaUI implements UIEntity<Phantomina> {

	@Override
	public Class<Phantomina> getItemClass() {
		return Phantomina.class;
	}

	@Override
	public String getTableName() {
		return "Phantominas";
	}

	@Override
	public List<AbstractColumn<Phantomina, ?>> createColumns() {
		List<AbstractColumn<Phantomina, ?>> columns = new ArrayList<>();
		FilterColumn<Phantomina, ?, ?> column;
		column = new FilterColumn<>("PID", path -> path.getProductId(),
				(path, value) -> path.setProductId(value));
		column.setAscendingSortComparator(
				(p1, p2) -> Phantomina.comparePid(p1.getProductId(), p2.getProductId()));
		columns.add(column);
		columns.add(new FilterColumn<>("Annulus [mm]", item -> item.getAnnulusDiameter(),
				path -> String.valueOf(path.getValue()),
				(path, value) -> path.setValue(Double.valueOf(value))));
		columns.add(new FilterColumn<>("Type", item -> item.getFabricationType(),
				path -> path.getValue(), (path, value) -> path.setValue(value)));
		columns.add(new FilterColumn<>("Literature", item -> item.getLiteratureBase(),
				path -> path.getValue(), (path, value) -> path.setValue(value)));
		columns.add(new FilterColumn<>("Special", item -> item.getSpecial(),
				path -> path.getShortcut(), (path, value) -> path.setShortcut(value)));
		ColumnUtils.createCountingColumn("Files", columns, item -> item.getFiles());
		ColumnUtils.createCountingColumn("Notes", columns, item -> item.getNotes());
//		createPropertyColumns(columns, this.getItems());
		ColumnUtils.createPersonifiedColumns(columns);
		return columns;
	}

	@Override
	public ItemEditFactoryController<Phantomina> createEditFactory() {
		return new PhantominaEditFactoryController();
	}

	private class PhantominaEditFactoryController
			extends GroupedItemEditFactoryController<Phantomina> {
		private Label labelIdValue;
		private ComboBox<AnnulusDiameter> comboBoxAnnulus;
		private ComboBox<FabricationType> comboBoxFabricationType;
		private ComboBox<LiteratureBase> comboBoxLiterature;
		private ComboBox<Special> comboBoxSpecials;

		{
			labelIdValue = new Label("id");
			comboBoxAnnulus = new ComboBox<AnnulusDiameter>();
			comboBoxFabricationType = new ComboBox<FabricationType>();
			comboBoxLiterature = new ComboBox<LiteratureBase>();
			comboBoxSpecials = new ComboBox<Special>();

			labelIdValue.setDisable(true);

			List<TitledPane> panes = new ArrayList<TitledPane>();
			createComboBoxes();
			List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
			generalProperties.add(new PropertyEntry("PID", labelIdValue));
			generalProperties.add(new PropertyEntry("Annulus diameter [mm]", comboBoxAnnulus));
			generalProperties.add(new PropertyEntry("Fabrication Type", comboBoxFabricationType));
			generalProperties.add(new PropertyEntry("Literarure Base", comboBoxLiterature));
			generalProperties.add(new PropertyEntry("Special", comboBoxSpecials));
			TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
			panes.add(generalPane);
			setTitledPanes(panes);

			setItemFactory(this::createItem);
			setTemplateSetter(this::setEditTemplate);
			setChangeApplier(this::applyChanges);
		}

		private void updateId() {
			try {
				labelIdValue.setText(createItem().getProductId());
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
		}

		@Override
		public Phantomina createItem() {
			AnnulusDiameter annulusDiameter = comboBoxAnnulus.getSelectionModel().getSelectedItem();
			FabricationType fabricationType =
					comboBoxFabricationType.getSelectionModel().getSelectedItem();
			LiteratureBase literatureBase =
					comboBoxLiterature.getSelectionModel().getSelectedItem();
			Special special = comboBoxSpecials.getSelectionModel().getSelectedItem();

			return new Phantomina(annulusDiameter, fabricationType, literatureBase, special);
		}

		@Override
		protected void setEditTemplate(Phantomina item) {
			super.selectComboBoxItem(comboBoxAnnulus, item.getAnnulusDiameter());
			super.selectComboBoxItem(comboBoxFabricationType, item.getFabricationType());
			super.selectComboBoxItem(comboBoxLiterature, item.getLiteratureBase());
			super.selectComboBoxItem(comboBoxSpecials, item.getSpecial());
		}

		@Override
		protected void applyChanges(Phantomina item) {
			AnnulusDiameter annulusDiameter = comboBoxAnnulus.getSelectionModel().getSelectedItem();
			FabricationType fabricationType =
					comboBoxFabricationType.getSelectionModel().getSelectedItem();
			LiteratureBase literatureBase =
					comboBoxLiterature.getSelectionModel().getSelectedItem();
			Special special = comboBoxSpecials.getSelectionModel().getSelectedItem();

			item.setAnnulusDiameter(annulusDiameter);
			item.setFabricationType(fabricationType);
			item.setLiteratureBase(literatureBase);
			item.setSpecial(special);
		}

	}

}