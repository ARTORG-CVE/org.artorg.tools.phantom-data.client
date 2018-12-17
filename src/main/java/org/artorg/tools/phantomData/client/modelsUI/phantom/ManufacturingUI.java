package org.artorg.tools.phantomData.client.modelsUI.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.FilterColumn;
import org.artorg.tools.phantomData.client.editor.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.util.ColumnUtils;
import org.artorg.tools.phantomData.server.models.phantom.Manufacturing;

import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class ManufacturingUI implements UIEntity<Manufacturing> {

	@Override
	public Class<Manufacturing> getItemClass() {
		return Manufacturing.class;
	}

	@Override
	public String getTableName() {
		return "Manufacturings";
	}

	@Override
	public List<AbstractColumn<Manufacturing, ?>> createColumns() {
		List<AbstractColumn<Manufacturing, ?>> columns = new ArrayList<>();
		columns.add(new FilterColumn<>("Name", path -> path.getName(),
				(path, value) -> path.setName(value)));
		columns.add(new FilterColumn<>("Description", path -> path.getDescription(),
				(path, value) -> path.setDescription(value)));
		ColumnUtils.createCountingColumn("Files", columns, item -> item.getFiles());
		ColumnUtils.createCountingColumn("Notes", columns, item -> item.getNotes());
		ColumnUtils.createPersonifiedColumns(columns);
		return columns;
	}

	@Override
	public ItemEditFactoryController<Manufacturing> createEditFactory() {
		return new ManufacturingEditFactoryController();
	}

	private class ManufacturingEditFactoryController
			extends GroupedItemEditFactoryController<Manufacturing> {
		private final TextField textFieldName;
		private final TextField textFieldDescription;

		{
			textFieldName = new TextField();
			textFieldDescription = new TextField();

			List<TitledPane> panes = new ArrayList<TitledPane>();
			List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
			generalProperties.add(new PropertyEntry("Name", textFieldName));
			generalProperties.add(new PropertyEntry("Description", textFieldDescription));
			TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
			panes.add(generalPane);
			setTitledPanes(panes);

			setItemFactory(this::createItem);
			setTemplateSetter(this::setEditTemplate);
			setChangeApplier(this::applyChanges);
		}

		@Override
		public Manufacturing createItem() {
			String name = textFieldName.getText();
			String description = textFieldDescription.getText();
			return new Manufacturing(name, description);
		}

		@Override
		protected void setEditTemplate(Manufacturing item) {
			textFieldName.setText(item.getName());
			textFieldDescription.setText(item.getDescription());
		}

		@Override
		protected void applyChanges(Manufacturing item) {
			String name = textFieldName.getText();
			String description = textFieldDescription.getText();

			item.setName(name);
			item.setDescription(description);
		}
	}

}