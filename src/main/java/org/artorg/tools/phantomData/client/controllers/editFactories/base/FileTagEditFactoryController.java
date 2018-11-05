package org.artorg.tools.phantomData.client.controllers.editFactories.base;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.server.model.base.FileTag;

import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class FileTagEditFactoryController extends GroupedItemEditFactoryController<FileTag> {
	private TextField textField; 
	
	{
		textField = new TextField();
		
		List<TitledPane> panes = new ArrayList<TitledPane>();
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("Name", textField));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		setTitledPanes(panes);
		
		setItemFactory(this::createItem);
		setTemplateSetter(this::setEditTemplate);
		setChangeApplier(this::applyChanges);
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
