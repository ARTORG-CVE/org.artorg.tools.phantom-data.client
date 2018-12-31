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
import org.artorg.tools.phantomData.server.models.phantom.Manufacturing;

public class ManufacturingUI extends UIEntity<Manufacturing> {

	public Class<Manufacturing> getItemClass() {
		return Manufacturing.class;
	}

	@Override
	public String getTableName() {
		return "Manufacturings";
	}

	@Override
	public List<AbstractColumn<Manufacturing, ?>> createColumns(Table<Manufacturing> table,
			List<Manufacturing> items) {
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
	public ItemEditor<Manufacturing> createEditFactory() {
		ItemEditor<Manufacturing> creator = new ItemEditor<Manufacturing>(getItemClass()) {

			@Override
			public void createPropertyGridPanes(Creator<Manufacturing> creator) {
				PropertyGridPane<Manufacturing> propertyPane =
						new PropertyGridPane<Manufacturing>(Manufacturing.class);
				creator.createTextField(item -> item.getName(),
						(item, value) -> item.setName(value)).addOn(propertyPane, "Shortcut");
				creator.createTextArea(item -> item.getDescription(),
						(item, value) -> item.setDescription(value))
						.addOn(propertyPane, "Description");
				propertyPane.setTitled("General");
				propertyPane.addOn(this);
			}

			@Override
			public void createSelectors(Creator<Manufacturing> creator) {
				creator.createSelector(DbFile.class, item -> item.getFiles(),
						(item, files) -> item.setFiles((List<DbFile>) files)).setTitled("Files")
						.addOn(this);

			}

		};
		creator.addApplyButton();
		return creator;
	}

}
