package org.artorg.tools.phantomData.client.editors.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.editor.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.server.model.phantom.Special;

import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class SpecialEditFactoryController extends GroupedItemEditFactoryController<Special> {
	private final TextField textFieldShortcut;
	private final TextField textFieldDescription;
	
	{
		textFieldShortcut = new TextField();
		textFieldDescription = new TextField();
		
		List<TitledPane> panes = new ArrayList<TitledPane>();
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("Shortcut", textFieldShortcut));
		generalProperties.add(new PropertyEntry("Description", textFieldDescription));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		setTitledPanes(panes);
		
		setItemFactory(this::createItem);
		setTemplateSetter(this::setEditTemplate);
		setChangeApplier(this::applyChanges);
	}

	@Override
	public Special createItem() {
		String shortcut = textFieldShortcut.getText();
		String description = textFieldDescription.getText();
		return new Special(shortcut, description);
	}

	@Override
	protected void setEditTemplate(Special item) {
		textFieldShortcut.setText(item.getShortcut());
		textFieldDescription.setText(item.getDescription());
	}

	@Override
	protected void applyChanges(Special item) {
		String shortcut = textFieldShortcut.getText();
		String description = textFieldDescription.getText();
    	
		item.setShortcut(shortcut);
		item.setDescription(description);
	}
	
}
