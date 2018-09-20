package org.artorg.tools.phantomData.client.controllers.editFactories;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.server.model.Special;

import javafx.scene.control.TextField;

public class SpecialEditFactoryController extends GroupedItemEditFactoryController<Special> {
	private TableViewSpring<Special> table;
	private TextField textFieldShortcut; 
	
	{
		textFieldShortcut = new TextField();
	}
	
	public SpecialEditFactoryController(TableViewSpring<Special> table) {
		this.table = table;
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
		
		to.setBooleanProperties(from.getBooleanProperties());
		to.setDateProperties(from.getDateProperties());
		to.setDoubleProperties(from.getDoubleProperties());
		to.setIntegerProperties(from.getIntegerProperties());
		to.setStringProperties(from.getStringProperties());
	}

	@Override
	protected TableViewSpring<Special> getTable() {
		return table;
	}

	@Override
	protected List<TitledPropertyPane> createProperties() {
		List<TitledPropertyPane> panes = new ArrayList<TitledPropertyPane>();
		
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("Shortcut", textFieldShortcut));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		
		return panes;
	}

}
