package org.artorg.tools.phantomData.client.modelsUI.base.person;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.person.AcademicTitle;
import org.artorg.tools.phantomData.server.util.FxUtil;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

public class AcademicTitleUI extends UIEntity<AcademicTitle> {

	public Class<AcademicTitle> getItemClass() {
		return AcademicTitle.class;
	}

	@Override
	public String getTableName() {
		return "Academic Titles";
	}

	@Override
	public List<AbstractColumn<AcademicTitle, ?>> createColumns(Table<AcademicTitle> table,
			List<AcademicTitle> items) {
		List<AbstractColumn<AcademicTitle, ?>> columns = new ArrayList<>();
		ColumnCreator<AcademicTitle, AcademicTitle> creator = new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Prefix", path -> path.getPrefix(),
				(path, value) -> path.setPrefix((String) value)));
		columns.add(creator.createFilterColumn("Descirption", path -> path.getDescription(),
				(path, value) -> path.setDescription((String) value)));
		return columns;
	}

	@Override
	public ItemEditor<AcademicTitle> createEditFactory() {
		ItemEditor<AcademicTitle> creator = new ItemEditor<>(getItemClass());
		VBox vBox = new VBox();

		List<PropertyEntry> entries = new ArrayList<>();
		creator.createTextField(item -> item.getPrefix(), (item, value) -> item.setPrefix(value))
				.addLabeled("Prefix", entries);
		creator.createTextField(item -> item.getDescription(),
				(item, value) -> item.setDescription(value)).addLabeled("Description", entries);
		TitledPane generalPane = creator.createTitledPane(entries, "General");
		vBox.getChildren().add(generalPane);

		vBox.getChildren().add(creator.createButtonPane(creator.getApplyButton()));

		FxUtil.addToPane(creator, vBox);
		return creator;
	}

}
