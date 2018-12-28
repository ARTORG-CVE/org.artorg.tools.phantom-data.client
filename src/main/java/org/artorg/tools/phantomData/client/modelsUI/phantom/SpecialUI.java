package org.artorg.tools.phantomData.client.modelsUI.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.phantom.Special;
import org.artorg.tools.phantomData.server.util.FxUtil;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

public class SpecialUI extends UIEntity<Special> {

	public Class<Special> getItemClass() {
		return Special.class;
	}
	
	@Override
	public String getTableName() {
		return "Specials";
	}

	@Override
	public List<AbstractColumn<Special, ?>> createColumns(Table<Special> table, List<Special> items) {
		List<AbstractColumn<Special, ?>> columns = new ArrayList<>();
		ColumnCreator<Special, Special> creator = new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Shortcut", path -> path.getShortcut(),
				(path, value) -> path.setShortcut(value)));
		columns.add(creator.createFilterColumn("Description", path -> path.getDescription(),
				(path, value) -> path.setDescription(value)));
		createCountingColumn(table, "Files", columns, item -> item.getFiles());
		createCountingColumn(table, "Notes", columns, item -> item.getNotes());
		createPropertyColumns(table, columns, items);
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditor<Special> createEditFactory() {
		ItemEditor<Special> creator = new ItemEditor<>(getItemClass());
		VBox vBox = new VBox();

		List<PropertyEntry> entries = new ArrayList<>();
		creator.createTextField(item -> item.getShortcut(),
			(item, value) -> item.setShortcut(value)).addLabeled("Shortcut", entries);
		creator.createTextField(item -> item.getDescription(),
			(item, value) -> item.setDescription(value)).addLabeled("Description", entries);
		TitledPane generalPane = creator.createTitledPane(entries, "General");
		vBox.getChildren().add(generalPane);

		vBox.getChildren().add(creator.createButtonPane(creator.getApplyButton()));

		FxUtil.addToPane(creator, vBox);
		return creator;
	}

}
