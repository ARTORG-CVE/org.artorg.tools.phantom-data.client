package org.artorg.tools.phantomData.client.controllers.editFactories;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoEditFilterTableView;
import org.artorg.tools.phantomData.server.model.FileType;
import org.artorg.tools.phantomData.server.model.property.DoubleProperty;

import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class FileTypeEditFactoryController extends GroupedItemEditFactoryController<FileType> {
	private DbUndoRedoEditFilterTableView<FileType> table;
	private TextField textFieldName;
	
	{
		textFieldName = new TextField();
	}

	public FileTypeEditFactoryController(DbUndoRedoEditFilterTableView<FileType> table) {
		this.table = table;
	}
	@Override
	public FileType createItem() {
		String name = textFieldName.getText();
		return new FileType(name);
	}

	@Override
	protected void setTemplate(FileType item) {
		textFieldName.setText(item.getName());
	}

	@Override
	protected void copy(FileType from, FileType to) {
		to.setName(from.getName());
	}

	@Override
	protected DbUndoRedoEditFilterTableView<FileType> getTable() {
		return table;
	}
	
	@Override
	protected List<TitledPane> createGroupedProperties(FileType item) {
		List<TitledPane> panes = new ArrayList<TitledPane>();
		
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("Name", textFieldName));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		
		return panes;
	}

}
