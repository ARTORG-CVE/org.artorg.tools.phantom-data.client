package org.artorg.tools.phantomData.client.controllers.editFactories;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.server.model.FabricationType;

import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class FabricationTypeEditFactoryController extends GroupedItemEditFactoryController<FabricationType> {
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
		setTemplateSetter(this::setTemplate);
		setItemCopier(this::copy);
	}

	@Override
	protected void setTemplate(FabricationType item) {
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
	protected void copy(FabricationType from, FabricationType to) {
		to.setShortcut(from.getShortcut());
		to.setValue(from.getValue());
	}

}
