package org.artorg.tools.phantomData.client.modelsUI.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.FxFactory;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.editor2.ItemEditor;
import org.artorg.tools.phantomData.client.editor2.PropertyNode;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.phantom.Manufacturing;
import org.artorg.tools.phantomData.server.util.FxUtil;

import javafx.scene.layout.VBox;

public class ManufacturingUI extends UIEntity<Manufacturing> {

	public Class<Manufacturing> getItemClass() {
		return Manufacturing.class;
	}

	@Override
	public String getTableName() {
		return "Manufacturings";
	}

	@Override
	public List<AbstractColumn<Manufacturing, ?>> createColumns(Table<Manufacturing> table, List<Manufacturing> items) {
		List<AbstractColumn<Manufacturing, ?>> columns = new ArrayList<>();
		ColumnCreator<Manufacturing, Manufacturing> creator = new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Name", path -> path.getName(),
				(path, value) -> path.setName(value)));
		columns.add(creator.createFilterColumn("Description", path -> path.getDescription(),
				(path, value) -> path.setDescription(value)));
		createCountingColumn(table, "Files", columns, item -> item.getFiles());
		createCountingColumn(table, "Notes", columns, item -> item.getNotes());
		createPropertyColumns(table, columns, items);
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public FxFactory<Manufacturing> createEditFactory() {
		ItemEditor<Manufacturing> creator = new ItemEditor<>(getItemClass());
		VBox vBox = new VBox();

		List<PropertyEntry> generalProperties = new ArrayList<>();
		creator.createTextField((item, value) -> item.setName(value),
			item -> item.getName()).addLabeled("Shortcut", generalProperties);
		creator.createTextField((item, value) -> item.setDescription(value),
			item -> item.getDescription()).addLabeled("Description", generalProperties);
		TitledPropertyPane generalPane =
			new TitledPropertyPane(generalProperties, "General");
		vBox.getChildren().add(generalPane);

		vBox.getChildren().add(creator.createButtonPane(creator.getApplyButton()));

		FxUtil.addToPane(creator, vBox);
		return creator;
	}

}
