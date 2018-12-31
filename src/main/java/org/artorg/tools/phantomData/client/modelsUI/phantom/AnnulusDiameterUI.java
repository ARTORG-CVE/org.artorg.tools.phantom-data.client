package org.artorg.tools.phantomData.client.modelsUI.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.Creator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyGridPane;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.DbFile;
import org.artorg.tools.phantomData.server.models.phantom.AnnulusDiameter;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AnnulusDiameterUI extends UIEntity<AnnulusDiameter> {

	public Class<AnnulusDiameter> getItemClass() {
		return AnnulusDiameter.class;
	}

	@Override
	public String getTableName() {
		return "Annulus Diameters";
	}

	@Override
	public List<AbstractColumn<AnnulusDiameter, ?>> createColumns(Table<AnnulusDiameter> table,
			List<AnnulusDiameter> items) {
		List<AbstractColumn<AnnulusDiameter, ?>> columns = new ArrayList<>();
		ColumnCreator<AnnulusDiameter, AnnulusDiameter> creator = new ColumnCreator<>(table);
		columns.add(
				creator.createFilterColumn("Sortcut", path -> String.valueOf(path.getShortcut()),
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
	public ItemEditor<AnnulusDiameter> createEditFactory() {
		Label labelShortcut = new Label();
		TextField textFieldValue = new TextField();
		textFieldValue.textProperty().addListener((observable, oldValue, newValue) -> {
			try {
				Integer shortcut = Double.valueOf(newValue).intValue();
				labelShortcut.setText(String.valueOf(shortcut));
			} catch (Exception e) {}
		});

		ItemEditor<AnnulusDiameter> creator = new ItemEditor<AnnulusDiameter>(getItemClass()) {

			@Override
			public void createPropertyGridPanes(Creator<AnnulusDiameter> creator) {
				PropertyGridPane<AnnulusDiameter> propertyPane =
						new PropertyGridPane<AnnulusDiameter>(AnnulusDiameter.class);
				creator.create(labelShortcut, item -> Integer.toString(item.getShortcut()),
						(item, value) -> item.setShortcut(Integer.valueOf(value)))
						.addOn(propertyPane, "Shortcut");
				creator.create(textFieldValue, item -> Double.toString(item.getValue()),
						(item, value) -> item.setValue(Double.valueOf(value)))
						.addOn(propertyPane, "Diameter [mm]");
				propertyPane.setTitled("General");
				propertyPane.addOn(this);
			}

			@Override
			public void createSelectors(Creator<AnnulusDiameter> creator) {
				creator.createSelector(DbFile.class, item -> item.getFiles(),
						(item, files) -> item.setFiles((List<DbFile>) files)).setTitled("Files")
						.addOn(this);
			}

		};
		creator.addApplyButton();
		return creator;
	}

}
