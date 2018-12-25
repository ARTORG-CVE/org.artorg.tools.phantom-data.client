package org.artorg.tools.phantomData.client.modelsUI.base.person;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.person.AcademicTitle;

import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class AcademicTitleUI extends UIEntity<AcademicTitle> {

	public Class<AcademicTitle> getItemClass() {
		return AcademicTitle.class;
	}

	@Override
	public String getTableName() {
		return "Academic Titles";
	}

	@Override
	public List<AbstractColumn<AcademicTitle, ?>> createColumns(Table<AcademicTitle> table, List<AcademicTitle> items) {
		List<AbstractColumn<AcademicTitle, ?>> columns = new ArrayList<>();
		ColumnCreator<AcademicTitle, AcademicTitle> creator = new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Prefix", path -> path.getPrefix(),
				(path, value) -> path.setPrefix((String) value)));
		columns.add(creator.createFilterColumn("Prefix", path -> path.getPrefix(),
				(path, value) -> path.setPrefix((String) value)));
		columns.add(creator.createFilterColumn("Descirption", path -> path.getDescription(),
				(path, value) -> path.setDescription((String) value)));
		return columns;
	}

	@Override
	public ItemEditFactoryController<AcademicTitle> createEditFactory() {
		return new AcademicTitleEditFactoryController();
	}

	private class AcademicTitleEditFactoryController
			extends GroupedItemEditFactoryController<AcademicTitle> {
		private TextField textFieldPrefix;
		private TextField textFieldDescription;

		{
			textFieldPrefix = new TextField();
			textFieldDescription = new TextField();

			List<TitledPane> panes = new ArrayList<TitledPane>();
			List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
			generalProperties.add(new PropertyEntry("prefix", textFieldPrefix));
			generalProperties.add(new PropertyEntry("description", textFieldDescription));
			TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
			panes.add(generalPane);
			setTitledPanes(panes);
		}

		@Override
		public void initDefaultValues() {
			textFieldPrefix.setText("");
			textFieldPrefix.setText("");
		}

		@Override
		public AcademicTitle createItem() {
			String prefix = textFieldPrefix.getText();
			String description = textFieldDescription.getText();
			return new AcademicTitle(prefix, description);
		}

		@Override
		protected void setEditTemplate(AcademicTitle item) {
			textFieldPrefix.setText(item.getPrefix());
			textFieldDescription.setText(item.getDescription());
		}

		@Override
		protected void applyChanges(AcademicTitle item) {
			String prefix = textFieldPrefix.getText();
			String description = textFieldDescription.getText();

			item.setPrefix(prefix);
			item.setDescription(description);
		}

	}

}
