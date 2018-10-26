package org.artorg.tools.phantomData.client.controllers.editFactories;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.server.model.phantom.Special;

import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class SpecialEditFactoryController extends GroupedItemEditFactoryController<Special> {
	private TextField textFieldShortcut; 
	
	{
		textFieldShortcut = new TextField();
		
		List<TitledPane> panes = new ArrayList<TitledPane>();
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("Shortcut", textFieldShortcut));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		setTitledPanes(panes);
		
		setItemFactory(this::createItem);
		setTemplateSetter(this::setTemplate);
		setItemCopier(this::copy);
	}

	@Override
	public Special createItem() {
		String shortcut = textFieldShortcut.getText();
		return new Special(shortcut);
	}

	@Override
	protected void setTemplate(Special item) {
		textFieldShortcut.setText(item.getShortcut());
	}

	@Override
	protected void copy(Special from, Special to) {
		to.setShortcut(from.getShortcut());
		
		to.setProperties(from.getProperties());
		
	}
	
}
