package org.artorg.tools.phantomData.client.modelsUI.base.person;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.FxFactory;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.editor2.ItemEditor;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.person.AcademicTitle;
import org.artorg.tools.phantomData.server.util.FxUtil;

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
	public FxFactory<AcademicTitle> createEditFactory() {
		ItemEditor<AcademicTitle> creator = new ItemEditor<>(getItemClass());
		VBox vBox = new VBox();

		List<PropertyEntry> entries = new ArrayList<>();
		creator.createTextField((item, value) -> item.setPrefix(value), item -> item.getPrefix())
				.addLabeled("Prefix", entries);
		creator.createTextField((item, value) -> item.setDescription(value),
				item -> item.getDescription()).addLabeled("Description", entries);
		TitledPropertyPane generalPane = new TitledPropertyPane(entries, "General");
		vBox.getChildren().add(generalPane);

		vBox.getChildren().add(creator.createButtonPane(creator.getApplyButton()));

		FxUtil.addToPane(creator, vBox);
		return creator;
	}

}
