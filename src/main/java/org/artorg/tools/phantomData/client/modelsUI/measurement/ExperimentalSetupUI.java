package org.artorg.tools.phantomData.client.modelsUI.measurement;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.PropertyNode;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.DbFile;
import org.artorg.tools.phantomData.server.models.measurement.ExperimentalSetup;
import org.artorg.tools.phantomData.server.util.FxUtil;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

public class ExperimentalSetupUI extends UIEntity<ExperimentalSetup> {

	public Class<ExperimentalSetup> getItemClass() {
		return ExperimentalSetup.class;
	}

	@Override
	public String getTableName() {
		return "Experimental Setups";
	}

	@Override
	public List<AbstractColumn<ExperimentalSetup, ?>> createColumns(Table<ExperimentalSetup> table, List<ExperimentalSetup> items) {
		List<AbstractColumn<ExperimentalSetup, ?>> columns = new ArrayList<>();
		ColumnCreator<ExperimentalSetup, ExperimentalSetup> creator = new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Short name", path -> path.getShortName(),
				(path, value) -> path.setShortName(value)));
		columns.add(creator.createFilterColumn("Long name", path -> path.getLongName(),
				(path, value) -> path.setLongName(value)));
		columns.add(creator.createFilterColumn("Description", path -> path.getDescription(),
				(path, value) -> path.setDescription(value)));
		createCountingColumn(table, "Files", columns, item -> item.getFiles());
		createCountingColumn(table, "Notes", columns, item -> item.getNotes());
		createPropertyColumns(table, columns, items);
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditor<ExperimentalSetup> createEditFactory() {
		ItemEditor<ExperimentalSetup> creator = new ItemEditor<>(getItemClass());
		VBox vBox = new VBox();
		
		List<PropertyEntry> entries = new ArrayList<>();
		creator.createTextField((item,value) -> item.setShortName(value), item -> item.getShortName()).addLabeled("Short name", entries);
		creator.createTextField((item,value) -> item.setLongName(value), item -> item.getLongName()).addLabeled("Long name", entries);
		creator.createTextField((item,value) -> item.setDescription(value), item -> item.getDescription()).addLabeled("Description", entries);
		TitledPane generalPane = creator.createTitledPane(entries, "General");
		vBox.getChildren().add(generalPane);
		
		PropertyNode<ExperimentalSetup,?> selector;
		selector = creator.createSelector(DbFile.class).titled("Files", item -> item.getFiles(),
			(item, files) -> item.setFiles((List<DbFile>) files));
		vBox.getChildren().add(selector.getParentNode());
		
		vBox.getChildren().add(creator.createButtonPane(creator.getApplyButton()));
		
		FxUtil.addToPane(creator, vBox);
		return creator;
	}

}
