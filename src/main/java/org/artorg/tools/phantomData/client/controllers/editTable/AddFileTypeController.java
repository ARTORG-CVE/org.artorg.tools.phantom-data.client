package org.artorg.tools.phantomData.client.controllers.editTable;

import java.util.List;

import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.server.model.FileType;

import javafx.scene.control.TextField;

public class AddFileTypeController extends AddEditController<FileType> {
	private TableViewSpring<FileType> table;
	private TextField textFieldName;
	
	{
		textFieldName = new TextField();
	}

	public AddFileTypeController(TableViewSpring<FileType> table) {
		this.table = table;
	}
	@Override
	public FileType createItem() {
		String name = textFieldName.getText();
		return new FileType(name);
	}
	
	@Override
	protected void addPropertyEntries(List<PropertyEntry> entries) {
		entries.add(new PropertyEntry("Name", textFieldName));
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
	protected TableViewSpring<FileType> getTable() {
		return table;
	}

}
