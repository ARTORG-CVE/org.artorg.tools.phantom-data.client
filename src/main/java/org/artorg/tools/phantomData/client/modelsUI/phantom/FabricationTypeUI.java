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
import org.artorg.tools.phantomData.server.model.phantom.FabricationType;

import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class FabricationTypeUI implements UIEntity<FabricationType> {

	@Override
	public Class<FabricationType> getItemClass() {
		return FabricationType.class;
	}

	@Override
	public String getTableName() {
		return "Fabrication Types";
	}

	@Override
	public List<AbstractColumn<FabricationType, ?>> createColumns() {
		List<AbstractColumn<FabricationType, ?>> columns = new ArrayList<>();
		columns.add(new FilterColumn<>("Shortcut", path -> path.getShortcut(),
				(path, value) -> path.setShortcut(value)));
		columns.add(new FilterColumn<>("Value", path -> path.getValue(),
				(path, value) -> path.setValue(value)));
		ColumnUtils.createCountingColumn("Files", columns, item -> item.getFiles());
		ColumnUtils.createCountingColumn("Notes", columns, item -> item.getNotes());
		ColumnUtils.createPersonifiedColumns(columns);
		return columns;
	}

	@Override
	public ItemEditFactoryController<FabricationType> createEditFactory() {
		return new FabricationTypeEditFactoryController();
	}

	public class FabricationTypeEditFactoryController
			extends GroupedItemEditFactoryController<FabricationType> {
		private TextField textFieldShortcut;
		private TextField textFieldValue;

		{
			textFieldShortcut = new TextField();
			textFieldValue = new TextField();

			List<TitledPane> panes = new ArrayList<TitledPane>();
			List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
			generalProperties.add(new PropertyEntry("Shortcut", textFieldShortcut));
			generalProperties.add(new PropertyEntry("Name", textFieldValue));;
			TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
			panes.add(generalPane);
			setTitledPanes(panes);

			setItemFactory(this::createItem);
			setTemplateSetter(this::setEditTemplate);
			setChangeApplier(this::applyChanges);
		}

		@Override
		protected void setEditTemplate(FabricationType item) {
			textFieldShortcut.setText(item.getShortcut());
			textFieldValue.setText(item.getValue());
		}

		@Override
		public FabricationType createItem() {
			String shortcut = textFieldShortcut.getText();
			String value = textFieldValue.getText();
			return new FabricationType(shortcut, value);
		}

		@Override
		protected void applyChanges(FabricationType item) {
			String shortcut = textFieldShortcut.getText();
			String value = textFieldValue.getText();

			item.setShortcut(shortcut);
			item.setValue(value);
		}

	}

}
