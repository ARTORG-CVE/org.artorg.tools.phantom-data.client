package org.artorg.tools.phantomData.client.modelsUI.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.phantom.AnnulusDiameter;
import org.artorg.tools.phantomData.server.util.FxUtil;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

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
		ItemEditor<AnnulusDiameter> creator = new ItemEditor<>(getItemClass());
		VBox vBox = new VBox();

		Label labelShortcut = new Label();
		TextField textFieldValue = new TextField();
		textFieldValue.textProperty().addListener((observable, oldValue, newValue) -> {
			try {
				Integer shortcut = Double.valueOf(newValue).intValue();
				labelShortcut.setText(String.valueOf(shortcut));
			} catch (Exception e) {}
		});

		List<PropertyEntry> entries = new ArrayList<>();
		creator.createLabel(labelShortcut, item -> Integer.toString(item.getShortcut()),
				(item, value) -> item.setShortcut(Integer.valueOf(value)))
				.addLabeled("Shortcut", entries);
		creator.createTextField(textFieldValue, item -> Double.toString(item.getValue()),
				(item, value) -> item.setValue(Double.valueOf(value)))
				.addLabeled("Diameter [mm]", entries);
		TitledPane generalPane = creator.createTitledPane(entries, "General");
		vBox.getChildren().add(generalPane);

		vBox.getChildren().add(creator.createButtonPane(creator.getApplyButton()));

		FxUtil.addToPane(creator, vBox);
		return creator;
	}

}
