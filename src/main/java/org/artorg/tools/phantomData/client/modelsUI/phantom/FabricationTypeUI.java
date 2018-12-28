package org.artorg.tools.phantomData.client.modelsUI.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.phantom.FabricationType;
import org.artorg.tools.phantomData.server.util.FxUtil;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

public class FabricationTypeUI extends UIEntity<FabricationType> {

	public Class<FabricationType> getItemClass() {
		return FabricationType.class;
	}

	@Override
	public String getTableName() {
		return "Fabrication Types";
	}

	@Override
	public List<AbstractColumn<FabricationType, ?>>
		createColumns(Table<FabricationType> table, List<FabricationType> items) {
		List<AbstractColumn<FabricationType, ?>> columns = new ArrayList<>();
		ColumnCreator<FabricationType, FabricationType> creator =
			new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Shortcut", path -> path.getShortcut(),
			(path, value) -> path.setShortcut(value)));
		columns.add(creator.createFilterColumn("Value", path -> path.getValue(),
			(path, value) -> path.setValue(value)));
		createCountingColumn(table, "Files", columns, item -> item.getFiles());
		createCountingColumn(table, "Notes", columns, item -> item.getNotes());
		createPropertyColumns(table, columns, items);
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditor<FabricationType> createEditFactory() {
		ItemEditor<FabricationType> creator = new ItemEditor<>(getItemClass());
		VBox vBox = new VBox();

		List<PropertyEntry> entries = new ArrayList<>();
		creator.createTextField((item, value) -> item.setShortcut(value),
			item -> item.getShortcut()).addLabeled("Shortcut", entries);
		creator.createTextField((item, value) -> item.setValue(value),
			item -> item.getValue()).addLabeled("Name", entries);
		TitledPane generalPane = creator.createTitledPane(entries, "General");
		vBox.getChildren().add(generalPane);

		vBox.getChildren().add(creator.createButtonPane(creator.getApplyButton()));

		FxUtil.addToPane(creator, vBox);
		return creator;
	}

}
