package org.artorg.tools.phantomData.client.modelsUI.base;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.util.ColumnUtils;
import org.artorg.tools.phantomData.server.models.base.FileTag;

import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class FileTagUI implements UIEntity<FileTag> {


	public Class<FileTag> getItemClass() {
		return FileTag.class;
	}

	@Override
	public String getTableName() {
		return "File Tags";
	}

	@Override
	public List<AbstractColumn<FileTag, ?>> createColumns(List<FileTag> items) {
		List<AbstractColumn<FileTag, ?>> columns = new ArrayList<AbstractColumn<FileTag, ?>>();
		ColumnCreator<FileTag, FileTag> creator = new ColumnCreator<>(getItemClass());
		columns.add(creator.createFilterColumn("Name", path -> path.getName(),
				(path, value) -> path.setName(value)));
		ColumnUtils.createPersonifiedColumns(getItemClass(), columns);
		return columns;
	}

	@Override
	public ItemEditFactoryController<FileTag> createEditFactory() {
		return new FileTagEditFactoryController();
	}

	private class FileTagEditFactoryController extends GroupedItemEditFactoryController<FileTag> {
		private TextField textField;

		{
			textField = new TextField();

			List<TitledPane> panes = new ArrayList<TitledPane>();
			List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
			generalProperties.add(new PropertyEntry("Name", textField));
			TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
			panes.add(generalPane);
			setTitledPanes(panes);
		}

		@Override
		public FileTag createItem() {
			String name = textField.getText();
			return new FileTag(name);
		}

		@Override
		protected void setEditTemplate(FileTag item) {
			textField.setText(item.getName());
		}

		@Override
		protected void applyChanges(FileTag item) {
			String message = textField.getText();

			item.setName(message);
		}

	}

}
