package org.artorg.tools.phantomData.client.modelsUI.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.column.FilterColumn;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.editor.FxFactory;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.editor2.ItemEditor;
import org.artorg.tools.phantomData.client.editor2.PropertyNode;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.models.base.DbFile;
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
import javafx.scene.layout.VBox;

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
	public FxFactory<Phantom> createEditFactory() {
		ItemEditor<Phantom> creator = new ItemEditor<Phantom>(getItemClass()) {

			@Override
			public void onCreateBeforePost(Phantom item) {
				ICrudConnector<Phantomina> connector = Connectors.get(Phantomina.class);
				if (!connector.exist(item.getPhantomina())) connector.create(item.getPhantomina());
			}

			@Override
			public void onEditInit(Phantom item) {
				throw new UnsupportedOperationException();
			}
		};
		VBox vBox = new VBox();
		List<PropertyEntry> entries = new ArrayList<>();
		Label labelIdValue = new Label("id");
		ComboBox<AnnulusDiameter> comboBoxAnnulusDiameter = new ComboBox<>();
		ComboBox<FabricationType> comboBoxFabricationType = new ComboBox<>();
		ComboBox<LiteratureBase> comboBoxLiteratureBase = new ComboBox<>();
		ComboBox<Special> comboBoxSpecial = new ComboBox<>();
		TextField textFieldNumber = new TextField();

		entries.add(new PropertyEntry("PID", labelIdValue));
		creator.createComboBox(AnnulusDiameter.class, comboBoxAnnulusDiameter)
				.of(item -> item.getPhantomina().getAnnulusDiameter(),
						(item, value) -> item.getPhantomina().setAnnulusDiameter(value))
				.setMapper(a -> String.valueOf(a.getValue()))
				.addLabeled("Annulus diameter", entries);
		creator.createComboBox(FabricationType.class, comboBoxFabricationType)
				.of(item -> item.getPhantomina().getFabricationType(),
						(item, value) -> item.getPhantomina().setFabricationType(value))
				.setMapper(f -> f.getValue()).addLabeled("Fabrication type", entries);
		creator.createComboBox(LiteratureBase.class, comboBoxLiteratureBase)
				.of(item -> item.getPhantomina().getLiteratureBase(),
						(item, value) -> item.getPhantomina().setLiteratureBase(value))
				.setMapper(l -> l.getValue()).addLabeled("Literature type", entries);
		creator.createComboBox(Special.class, comboBoxSpecial)
				.of(item -> item.getPhantomina().getSpecial(),
						(item, value) -> item.getPhantomina().setSpecial(value))
				.setMapper(s -> s.getShortcut()).addLabeled("Special", entries);
		creator.createTextField(textFieldNumber, item -> Integer.toString(item.getNumber()),
				(item, value) -> item.setNumber(Integer.valueOf(value)))
				.addLabeled("Phantom specific Number", entries);
		creator.createComboBox(Manufacturing.class)
				.of(item -> item.getManufacturing(), (item, value) -> item.setManufacturing(value))
				.setMapper(m -> m.getName()).addLabeled("Manufacturing", entries);
		creator.createTextField((item, value) -> item.setThickness(Float.valueOf(value)),
				item -> Float.toString(item.getThickness()))
				.addLabeled("Nominal thickness", entries);

		Runnable updateId = () -> {
			String sNumber = textFieldNumber.getText();
			int number;
			if (sNumber.isEmpty()) number = 0;
			else
				number = Integer.valueOf(sNumber);
			String phantominaProductId = Phantomina.createProductId(
					comboBoxAnnulusDiameter.getSelectionModel().getSelectedItem(),
					comboBoxFabricationType.getSelectionModel().getSelectedItem(),
					comboBoxLiteratureBase.getSelectionModel().getSelectedItem(),
					comboBoxSpecial.getSelectionModel().getSelectedItem());
			labelIdValue.setText(Phantom.createProductId(phantominaProductId, number));
		};
		comboBoxAnnulusDiameter.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> updateId.run());
		comboBoxFabricationType.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> updateId.run());
		comboBoxLiteratureBase.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> updateId.run());
		comboBoxSpecial.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> updateId.run());

		TitledPropertyPane generalPane = new TitledPropertyPane(entries, "General");
		vBox.getChildren().add(generalPane);

		PropertyNode<Phantom, ?> selector;
		selector = creator.createSelector(DbFile.class).titled("Files", item -> item.getFiles(),
				(item, files) -> item.setFiles((List<DbFile>) files));
		vBox.getChildren().add(selector.getParentNode());

		vBox.getChildren().add(creator.createButtonPane(creator.getApplyButton()));

		FxUtil.addToPane(creator, vBox);
		return creator;

	}

}
