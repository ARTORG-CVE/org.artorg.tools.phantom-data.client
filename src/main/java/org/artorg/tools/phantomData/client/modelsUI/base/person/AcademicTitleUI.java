package org.artorg.tools.phantomData.client.modelsUI.base.person;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.Creator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyGridPane;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.person.AcademicTitle;

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
		ItemEditor<AcademicTitle> editor = new ItemEditor<AcademicTitle>(getItemClass()) {

			@Override
			public void createPropertyGridPanes(Creator<AcademicTitle> creator) {
				PropertyGridPane<AcademicTitle> propertyPane =
						new PropertyGridPane<AcademicTitle>(AcademicTitle.class);
				creator.createTextField(item -> item.getPrefix(),
						(item, value) -> item.setPrefix(value)).addOn(propertyPane,"Prefix");
				creator.createTextField(item -> item.getDescription(),
						(item, value) -> item.setDescription(value)).addOn(propertyPane, "Description");
				propertyPane.setTitled("General");
				propertyPane.addOn(this);
			}

			@Override
			public void createSelectors(Creator<AcademicTitle> creator) {}

		};		
		editor.addApplyButton();
		return editor;
	}

}
