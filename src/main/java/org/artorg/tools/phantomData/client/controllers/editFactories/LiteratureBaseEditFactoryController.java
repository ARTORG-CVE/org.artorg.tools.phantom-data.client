package org.artorg.tools.phantomData.client.controllers.editFactories;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.server.model.LiteratureBase;

import javafx.scene.control.TextField;

public class LiteratureBaseEditFactoryController extends GroupedItemEditFactoryController<LiteratureBase> {
	private TableViewSpring<LiteratureBase> table;
	private TextField textFieldShortcut;
	private TextField textFieldValue;

	{
		textFieldShortcut = new TextField();
		textFieldValue = new TextField();
	}
	
	public LiteratureBaseEditFactoryController(TableViewSpring<LiteratureBase> table) {
		this.table = table;
	}

	@Override
	protected void setTemplate(LiteratureBase item) {
		textFieldShortcut.setText(item.getShortcut());
		textFieldValue.setText(item.getValue());
	}

	@Override
	public LiteratureBase createItem() {
		String shortcut = textFieldShortcut.getText();
		String value = textFieldValue.getText();
		return new LiteratureBase(shortcut, value);
	}

	@Override
	protected void copy(LiteratureBase from, LiteratureBase to) {
		to.setShortcut(from.getShortcut());
		to.setValue(from.getValue());
	}

	@Override
	protected TableViewSpring<LiteratureBase> getTable() {
		return table;
	}

	@Override
	protected List<TitledPropertyPane> createProperties() {
		List<TitledPropertyPane> panes = new ArrayList<TitledPropertyPane>();
		
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("Shortcut", textFieldShortcut));
		generalProperties.add(new PropertyEntry("Name", textFieldValue));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		
		return panes;
	}

}