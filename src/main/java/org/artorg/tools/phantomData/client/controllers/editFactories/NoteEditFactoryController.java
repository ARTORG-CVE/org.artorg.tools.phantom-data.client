package org.artorg.tools.phantomData.client.controllers.editFactories;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.server.model.Note;

import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class NoteEditFactoryController extends GroupedItemEditFactoryController<Note> {
	private TextField textFieldMessage; 
	
	{
		textFieldMessage = new TextField();
		
		List<TitledPane> panes = new ArrayList<TitledPane>();
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("Message", textFieldMessage));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		setTitledPanes(panes);
		
		setItemFactory(this::createItem);
		setTemplateSetter(this::setEditTemplate);
		setChangeApplier(this::applyChanges);
	}

	@Override
	public Note createItem() {
		String note = textFieldMessage.getText();
		return new Note(note);
	}

	@Override
	protected void setEditTemplate(Note item) {
		textFieldMessage.setText(item.getName());
	}

	@Override
	protected void applyChanges(Note item) {
		String message = textFieldMessage.getText();
    	
		item.setName(message);
	}
	
}
