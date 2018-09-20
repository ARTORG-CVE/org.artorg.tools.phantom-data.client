package org.artorg.tools.phantomData.client.controllers.editFactories;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.server.model.FabricationType;

import javafx.scene.control.TextField;

public class FabricationTypeEditFactoryController extends GroupedItemEditFactoryController<FabricationType> {
	private TableViewSpring<FabricationType> table;
	private TextField textFieldShortcut;
	private TextField textFieldValue;

	{
		textFieldShortcut = new TextField();
		textFieldValue = new TextField();
	}
	
	public FabricationTypeEditFactoryController(TableViewSpring<FabricationType> table) {
		this.table = table;
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

	@Override
	protected TableViewSpring<FabricationType> getTable() {
		return table;
	}

	@Override
	protected List<TitledPropertyPane> createProperties() {
		List<TitledPropertyPane> panes = new ArrayList<TitledPropertyPane>();
		
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("Shortcut", textFieldShortcut));
		generalProperties.add(new PropertyEntry("Name", textFieldValue));;
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		
		return panes;
	}

}
