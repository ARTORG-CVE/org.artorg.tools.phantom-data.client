package org.artorg.tools.phantomData.client.modelsUI.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.Creator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyGridPane;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.DbFile;
import org.artorg.tools.phantomData.server.models.phantom.Material;

public class MaterialUI extends UIEntity<Material> {

	@Override
	public Class<Material> getItemClass() {
		return Material.class;
	}

	@Override
	public String getTableName() {
		return "Materials";
	}

	@Override
	public List<AbstractColumn<Material, ? extends Object>> createColumns(Table<Material> table,
			List<Material> items) {
		List<AbstractColumn<Material, ?>> columns = new ArrayList<>();
		ColumnCreator<Material, Material> creator = new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Name", path -> path.getName(),
				(path, value) -> path.setName(value)));
		columns.add(creator.createFilterColumn("Description", path -> path.getDescription(),
				(path, value) -> path.setDescription(value)));
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditor<Material> createEditFactory() {
		ItemEditor<Material> editor = new ItemEditor<Material>(getItemClass()) {

			@Override
			public void createPropertyGridPanes(Creator<Material> creator) {
				PropertyGridPane<Material> propertyPane =
						new PropertyGridPane<Material>(Material.class);
				creator.createTextField(item -> item.getName(),
						(item, value) -> item.setName(value)).addOn(propertyPane, "Name");
				creator.createTextArea(item -> item.getDescription(),
						(item, value) -> item.setDescription(value))
						.addOn(propertyPane, "Description");
				propertyPane.setTitled("General");
				propertyPane.addOn(this);
			}

			@Override
			public void createSelectors(Creator<Material> creator) {
				creator.createSelector(DbFile.class, item -> item.getFiles(),
						(item, subItems) -> item.setFiles((List<DbFile>) subItems))
						.setTitled("Files").addOn(this);
			}

		};
		editor.addApplyButton();
		return editor;
	}

}
