package org.artorg.tools.phantomData.client.modelsUI.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.phantom.AnnulusDiameter;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class AnnulusDiameterUI extends UIEntity<AnnulusDiameter> {

	public Class<AnnulusDiameter> getItemClass() {
		return AnnulusDiameter.class;
	}

	@Override
	public String getTableName() {
		return "Annulus Diameters";
	}

	@Override
	public List<AbstractColumn<AnnulusDiameter, ?>> createColumns(Table<AnnulusDiameter> table, List<AnnulusDiameter> items) {
		List<AbstractColumn<AnnulusDiameter, ?>> columns = new ArrayList<>();
		ColumnCreator<AnnulusDiameter, AnnulusDiameter> creator =
				new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Sortcut", path -> String.valueOf(path.getShortcut()),
				(path, value) -> path.setShortcut(Integer.valueOf(value))));
		columns.add(creator.createFilterColumn("Value", path -> String.valueOf(path.getValue()),
				(path, value) -> path.setValue(Double.valueOf(value))));
		createCountingColumn(table, "Files", columns, item -> item.getFiles());
		createCountingColumn(table, "Notes", columns, item -> item.getNotes());
		createPropertyColumns(table, columns, items);
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditFactoryController<AnnulusDiameter> createEditFactory() {
		return new AnnulusDiameterEditFactoryController();
	}

	private class AnnulusDiameterEditFactoryController
			extends GroupedItemEditFactoryController<AnnulusDiameter> {
		private Label labelShortcut;
		private TextField textFieldValue;

		{
			labelShortcut = new Label();
			textFieldValue = new TextField();
			labelShortcut.setDisable(true);

			List<TitledPane> panes = new ArrayList<TitledPane>();
			List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
			generalProperties.add(new PropertyEntry("Shortcut [mm]", labelShortcut));
			generalProperties
					.add(new PropertyEntry("Diameter [mm]", textFieldValue, () -> updateLabel()));
			TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
			panes.add(generalPane);
			setTitledPanes(panes);
		}

		private void updateLabel() {
			try {
				Integer shortcut = Double.valueOf(textFieldValue.getText()).intValue();
				labelShortcut.setText(String.valueOf(shortcut));
			} catch (Exception e) {}
		}

		@Override
		public void initDefaultValues() {
			labelShortcut.setText("0");
			textFieldValue.setText("0.0");
		}

		@Override
		public AnnulusDiameter createItem() {
			Integer shortcut = Integer.valueOf(labelShortcut.getText());
			Double value = Double.valueOf(textFieldValue.getText());
			return new AnnulusDiameter(shortcut, value);
		}

		@Override
		protected void setEditTemplate(AnnulusDiameter item) {
			textFieldValue.setText(Double.toString(item.getValue()));
			updateLabel();
		}

		@Override
		protected void applyChanges(AnnulusDiameter item) {
			Integer shortcut = Integer.valueOf(labelShortcut.getText());
			Double value = Double.valueOf(textFieldValue.getText());

			item.setShortcut(shortcut);
			item.setValue(value);
		}

	}

}
