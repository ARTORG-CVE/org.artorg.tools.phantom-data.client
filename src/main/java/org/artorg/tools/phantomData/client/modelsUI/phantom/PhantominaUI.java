package org.artorg.tools.phantomData.client.modelsUI.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.column.FilterColumn;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.PropertyNode;
import org.artorg.tools.phantomData.client.exceptions.InvalidUIInputException;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.DbFile;
import org.artorg.tools.phantomData.server.models.phantom.AnnulusDiameter;
import org.artorg.tools.phantomData.server.models.phantom.FabricationType;
import org.artorg.tools.phantomData.server.models.phantom.LiteratureBase;
import org.artorg.tools.phantomData.server.models.phantom.Phantomina;
import org.artorg.tools.phantomData.server.models.phantom.Special;
import org.artorg.tools.phantomData.server.util.FxUtil;

import com.google.common.base.Supplier;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

public class PhantominaUI extends UIEntity<Phantomina> {

	public Class<Phantomina> getItemClass() {
		return Phantomina.class;
	}

	@Override
	public String getTableName() {
		return "Phantominas";
	}

	@Override
	public List<AbstractColumn<Phantomina, ?>> createColumns(Table<Phantomina> table,
			List<Phantomina> items) {
		List<AbstractColumn<Phantomina, ?>> columns = new ArrayList<>();
		FilterColumn<Phantomina, ?, ?> column;
		ColumnCreator<Phantomina, Phantomina> creator = new ColumnCreator<>(table);
		column = creator.createFilterColumn("PID", path -> path.getProductId(),
				(path, value) -> path.setProductId(value));
		column.setAscendingSortComparator(
				(p1, p2) -> Phantomina.comparePid(p1.getProductId(), p2.getProductId()));
		column.setItemsFilter(false);
		columns.add(column);
		columns.add(creator.createFilterColumn("Annulus [mm]",
				path -> String.valueOf(path.getAnnulusDiameter().getValue()),
				(path, value) -> path.getAnnulusDiameter().setValue(Double.valueOf(value))));
		columns.add(creator.createFilterColumn("Type", path -> path.getFabricationType().getValue(),
				(path, value) -> path.getFabricationType().setValue(value)));
		columns.add(creator.createFilterColumn("Literature",
				path -> path.getLiteratureBase().getValue(),
				(path, value) -> path.getLiteratureBase().setValue(value)));
		columns.add(creator.createFilterColumn("Special",
				path -> path.getLiteratureBase().getShortcut(),
				(path, value) -> path.getLiteratureBase().setShortcut(value)));
		createCountingColumn(table, "Files", columns, item -> item.getFiles());
		createCountingColumn(table, "Notes", columns, item -> item.getNotes());
		createPropertyColumns(table, columns, items);
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditor<Phantomina> createEditFactory() {
		VBox vBox = new VBox();
		List<PropertyEntry> entries = new ArrayList<>();
		Label labelIdValue = new Label("id");
		ComboBox<AnnulusDiameter> comboBoxAnnulusDiameter = new ComboBox<>();
		ComboBox<FabricationType> comboBoxFabricationType = new ComboBox<>();
		ComboBox<LiteratureBase> comboBoxLiteratureBase = new ComboBox<>();
		ComboBox<Special> comboBoxSpecial = new ComboBox<>();

		Supplier<String> pidGetter = () -> {
			return Phantomina.createProductId(
					comboBoxAnnulusDiameter.getSelectionModel().getSelectedItem(),
					comboBoxFabricationType.getSelectionModel().getSelectedItem(),
					comboBoxLiteratureBase.getSelectionModel().getSelectedItem(),
					comboBoxSpecial.getSelectionModel().getSelectedItem());
		};
		Runnable updateId = () -> {
			labelIdValue.setText(pidGetter.get());
		};

		ItemEditor<Phantomina> creator = new ItemEditor<Phantomina>(getItemClass()) {

			@Override
			public void onCreateBeforeApplyChanges(Phantomina item) throws InvalidUIInputException {
				ICrudConnector<Phantomina> connector = Connectors.get(Phantomina.class);
				List<Phantomina> phantominas = connector.readAllAsList();
				String pid = pidGetter.get();
				if (phantominas.stream().filter(p -> p.getProductId().equals(pid)).findFirst()
						.isPresent())
					throw new InvalidUIInputException(
							String.format("Phantomina with product id %s exists already", pid));
			}

		};

		entries.add(new PropertyEntry("PID", labelIdValue));
		creator.createComboBox(AnnulusDiameter.class, comboBoxAnnulusDiameter)
				.of(item -> item.getAnnulusDiameter(),
						(item, value) -> item.setAnnulusDiameter(value))
				.setMapper(a -> String.valueOf(a.getValue()))
				.addLabeled("Annulus diameter", entries);
		creator.createComboBox(FabricationType.class, comboBoxFabricationType)
				.of(item -> item.getFabricationType(),
						(item, value) -> item.setFabricationType(value))
				.setMapper(f -> String.format("(%s) %s", f.getShortcut(), f.getValue()))
				.addLabeled("Fabrication type", entries);
		creator.createComboBox(LiteratureBase.class, comboBoxLiteratureBase)
				.of(item -> item.getLiteratureBase(),
						(item, value) -> item.setLiteratureBase(value))
				.setMapper(f -> String.format("(%s) %s", f.getShortcut(), f.getValue()))
				.addLabeled("Literature type", entries);
		creator.createComboBox(Special.class, comboBoxSpecial)
				.of(item -> item.getSpecial(), (item, value) -> item.setSpecial(value))
				.setMapper(s -> String.format("%s: %s", s.getShortcut(), s.getDescription()))
				.addLabeled("Special", entries);

		comboBoxAnnulusDiameter.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> updateId.run());
		comboBoxFabricationType.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> updateId.run());
		comboBoxLiteratureBase.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> updateId.run());
		comboBoxSpecial.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> updateId.run());

		TitledPane generalPane = creator.createTitledPane(entries, "General");
		vBox.getChildren().add(generalPane);

		PropertyNode<Phantomina, ?> selector;
		selector = creator.createSelector(DbFile.class).titled("Protocol Files",
				item -> item.getFiles(), (item, files) -> item.setFiles((List<DbFile>) files));
		vBox.getChildren().add(selector.getParentNode());

		vBox.getChildren().add(creator.createButtonPane(creator.getApplyButton()));

		FxUtil.addToPane(creator, vBox);
		return creator;
	}

}
