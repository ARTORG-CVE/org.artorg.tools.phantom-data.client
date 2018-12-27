package org.artorg.tools.phantomData.client.modelsUI.base;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.FxFactory;
import org.artorg.tools.phantomData.client.editor.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.editor2.ItemEditor;
import org.artorg.tools.phantomData.client.editor2.PropertyNode;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.FileTag;
import org.artorg.tools.phantomData.server.models.base.person.AcademicTitle;
import org.artorg.tools.phantomData.server.util.FxUtil;

import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

public class FileTagUI extends UIEntity<FileTag> {


	public Class<FileTag> getItemClass() {
		return FileTag.class;
	}

	@Override
	public String getTableName() {
		return "File Tags";
	}

	@Override
	public List<AbstractColumn<FileTag, ?>> createColumns(Table<FileTag> table, List<FileTag> items) {
		List<AbstractColumn<FileTag, ?>> columns = new ArrayList<AbstractColumn<FileTag, ?>>();
		ColumnCreator<FileTag, FileTag> creator = new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Name", path -> path.getName(),
				(path, value) -> path.setName(value)));
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public FxFactory<FileTag> createEditFactory() {
//		return new FileTagEditFactoryController();
		
		ItemEditor<FileTag> creator = new ItemEditor<>(getItemClass());
		VBox vBox = new VBox();
		PropertyNode<FileTag,?> propertyNode;
		
		List<PropertyEntry> generalProperties = new ArrayList<>();
		propertyNode = creator.createTextField((item,value) -> item.setName(value), item -> item.getName());
		generalProperties.add(new PropertyEntry("Name", propertyNode.getNode()));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		vBox.getChildren().add(generalPane);
		
		vBox.getChildren().add(creator.createButtonPane(creator.getApplyButton()));
		
		FxUtil.addToPane(creator, vBox);
		return creator;
		
	}

}
