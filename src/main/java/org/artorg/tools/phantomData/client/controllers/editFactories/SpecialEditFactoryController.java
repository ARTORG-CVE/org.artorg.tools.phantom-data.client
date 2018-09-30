package org.artorg.tools.phantomData.client.controllers.editFactories;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoEditFilterTableView;
import org.artorg.tools.phantomData.server.model.Special;
import org.artorg.tools.phantomData.server.model.property.DoubleProperty;

import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class SpecialEditFactoryController extends GroupedItemEditFactoryController<Special> {
	private DbUndoRedoEditFilterTableView<Special> table;
	private TextField textFieldShortcut; 
	
	{
		textFieldShortcut = new TextField();
	}
	
	public SpecialEditFactoryController(DbUndoRedoEditFilterTableView<Special> table) {
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
	protected DbUndoRedoEditFilterTableView<Special> getTable() {
		return table;
	}

	@Override
	protected List<TitledPane> createGroupedProperties(Special item) {
		List<TitledPane> panes = new ArrayList<TitledPane>();
		
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("Shortcut", textFieldShortcut));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		
		return panes;
	}

}
