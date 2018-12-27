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
import org.artorg.tools.phantomData.server.models.phantom.LiteratureBase;
import org.artorg.tools.phantomData.server.util.FxUtil;

import javafx.scene.layout.VBox;

public class LiteratureBaseUI extends UIEntity<LiteratureBase> {


	public Class<LiteratureBase> getItemClass() {
		return LiteratureBase.class;
	}
	
	@Override
	public String getTableName() {
		return "Literature Bases";
	}

	@Override
	public List<AbstractColumn<LiteratureBase, ?>> createColumns(Table<LiteratureBase> table, List<LiteratureBase> items) {
		List<AbstractColumn<LiteratureBase, ?>> columns = new ArrayList<>();
		ColumnCreator<LiteratureBase, LiteratureBase> creator =
				new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Shortcut", path -> path.getShortcut(),
				(path, value) -> path.setShortcut((String) value)));
		columns.add(creator.createFilterColumn("Value", path -> path.getValue(),
				(path, value) -> path.setValue((String) value)));
		createCountingColumn(table, "Files", columns, item -> item.getFiles());
		createCountingColumn(table, "Notes", columns, item -> item.getNotes());
		createPropertyColumns(table, columns, items);
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public FxFactory<LiteratureBase> createEditFactory() {
		ItemEditor<LiteratureBase> creator = new ItemEditor<>(getItemClass());
		VBox vBox = new VBox();
		PropertyNode<LiteratureBase, ?> propertyNode;

		List<PropertyEntry> generalProperties = new ArrayList<>();
		propertyNode = creator.createTextField((item, value) -> item.setShortcut(value),
			item -> item.getShortcut());
		generalProperties.add(new PropertyEntry("Shortcut", propertyNode.getNode()));
		propertyNode = creator.createTextField((item, value) -> item.setValue(value),
			item -> item.getValue());
		generalProperties.add(new PropertyEntry("Name", propertyNode.getNode()));
		TitledPropertyPane generalPane =
			new TitledPropertyPane(generalProperties, "General");
		vBox.getChildren().add(generalPane);

		vBox.getChildren().add(creator.createButtonPane(creator.getApplyButton()));

		FxUtil.addToPane(creator, vBox);
		return creator;
	}

}
