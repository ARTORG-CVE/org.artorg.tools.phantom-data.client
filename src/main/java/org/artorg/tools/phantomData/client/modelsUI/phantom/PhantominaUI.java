package org.artorg.tools.phantomData.client.modelsUI.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.column.FilterColumn;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyGridPane;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.exceptions.InvalidUIInputException;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.DbFile;
import org.artorg.tools.phantomData.server.models.phantom.AnnulusDiameter;
import org.artorg.tools.phantomData.server.models.phantom.FabricationType;
import org.artorg.tools.phantomData.server.models.phantom.LiteratureBase;
import org.artorg.tools.phantomData.server.models.phantom.Phantomina;
import org.artorg.tools.phantomData.server.models.phantom.Special;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

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
		ColumnCreator<Phantomina, Phantomina> editor = new ColumnCreator<>(table);
		column = editor.createFilterColumn("PID", path -> path.getProductId(),
				(path, value) -> path.setProductId(value));
		column.setAscendingSortComparator(
				(p1, p2) -> Phantomina.comparePid(p1.getProductId(), p2.getProductId()));
		column.setItemsFilter(false);
		columns.add(column);
		columns.add(editor.createFilterColumn("Annulus [mm]",
				path -> String.valueOf(path.getAnnulusDiameter().getValue()),
				(path, value) -> path.getAnnulusDiameter().setValue(Double.valueOf(value))));
		columns.add(editor.createFilterColumn("Type", path -> path.getFabricationType().getValue(),
				(path, value) -> path.getFabricationType().setValue(value)));
		columns.add(
				editor.createFilterColumn("Literature", path -> path.getLiteratureBase().getValue(),
						(path, value) -> path.getLiteratureBase().setValue(value)));
		columns.add(
				editor.createFilterColumn("Special", path -> path.getLiteratureBase().getShortcut(),
						(path, value) -> path.getLiteratureBase().setShortcut(value)));
		createCountingColumn(table, "Files", columns, item -> item.getFiles());
		createCountingColumn(table, "Notes", columns, item -> item.getNotes());
		createPropertyColumns(table, columns, items);
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditor<Phantomina> createEditFactory() {
		Label labelIdValue = new Label("id");
		ComboBox<AnnulusDiameter> comboBoxAnnulusDiameter = new ComboBox<>();
		ComboBox<FabricationType> comboBoxFabricationType = new ComboBox<>();
		ComboBox<LiteratureBase> comboBoxLiteratureBase = new ComboBox<>();
		ComboBox<Special> comboBoxSpecial = new ComboBox<>();

		ItemEditor<Phantomina> editor = new ItemEditor<Phantomina>(Phantomina.class) {

			@Override
			public void onCreatingClient(Phantomina item) throws InvalidUIInputException {
				String pid = getPid();
				if (existsPid(pid)) throw new InvalidUIInputException(Phantomina.class,
						String.format("Phantomina with product id %s exists already", pid));
			}

			@Override
			public void onUpdatingClient(Phantomina item) throws InvalidUIInputException {
				String pid = getPid();
				if (!pid.equals(item.getProductId())) {
					if (existsPid(pid)) throw new InvalidUIInputException(Phantomina.class,
							String.format("Phantomina with product id %s exists already", pid));
				}
			}

			private String getPid() {
				return Phantomina.createProductId(
						comboBoxAnnulusDiameter.getSelectionModel().getSelectedItem(),
						comboBoxFabricationType.getSelectionModel().getSelectedItem(),
						comboBoxLiteratureBase.getSelectionModel().getSelectedItem(),
						comboBoxSpecial.getSelectionModel().getSelectedItem());
			}

		};

		PropertyGridPane propertyPane = new PropertyGridPane();
		propertyPane.addEntry(new Label("PID"), labelIdValue);
		propertyPane.addEntry("Annulus Diameter",
				editor.createComboBox(comboBoxAnnulusDiameter, AnnulusDiameter.class,
						item -> item.getAnnulusDiameter(),
						(item, value) -> item.setAnnulusDiameter(value))
						.setMapper(a -> String.valueOf(a.getValue())));
		propertyPane.addEntry("Fabrication Type",
				editor.createComboBox(comboBoxFabricationType, FabricationType.class,
						item -> item.getFabricationType(),
						(item, value) -> item.setFabricationType(value))
						.setMapper(f -> String.format("(%s) %s", f.getShortcut(), f.getValue())));
		propertyPane.addEntry("Literautre Base",
				editor.createComboBox(comboBoxLiteratureBase, LiteratureBase.class,
						item -> item.getLiteratureBase(),
						(item, value) -> item.setLiteratureBase(value))
						.setMapper(f -> String.format("(%s) %s", f.getShortcut(), f.getValue())));
		propertyPane.addEntry("Special", editor
				.createComboBox(comboBoxSpecial, Special.class, item -> item.getSpecial(),
						(item, value) -> item.setSpecial(value))
				.setMapper(s -> String.format("(%s) %s", s.getShortcut(), s.getDescription())));
		editor.add(new TitledPropertyPane("General", propertyPane));

		editor.add(new TitledPropertyPane("Files", editor.createSelector(DbFile.class,
				item -> item.getFiles(), (item, files) -> item.setFiles((List<DbFile>) files))));
		editor.add(new TitledPropertyPane("Properties", editor.createPropertySelector()));

		Runnable updateId = () -> {
			labelIdValue.setText(Phantomina.createProductId(
					comboBoxAnnulusDiameter.getSelectionModel().getSelectedItem(),
					comboBoxFabricationType.getSelectionModel().getSelectedItem(),
					comboBoxLiteratureBase.getSelectionModel().getSelectedItem(),
					comboBoxSpecial.getSelectionModel().getSelectedItem()));
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
	
	public static boolean existsPid(String pid) {
		ICrudConnector<Phantomina> connector = Connectors.get(Phantomina.class);
		List<Phantomina> phantominas = connector.readAllAsList();
		return phantominas.stream().filter(p -> p.getProductId().equals(pid)).findFirst()
				.isPresent();
	}

}
