package org.artorg.tools.phantomData.client.controllers.editFactories.measurement;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.server.model.measurement.UnitPrefix;

import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class UnitPrefixEditFactoryController extends GroupedItemEditFactoryController<UnitPrefix> {
	private TextField textFieldName;
	private TextField textFieldPrefix;
	private TextField textFieldExponent;

	{
		textFieldName = new TextField();
		textFieldPrefix = new TextField();
		textFieldExponent = new TextField();
		
		List<TitledPane> panes = new ArrayList<TitledPane>();
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("Name", textFieldName));
		generalProperties.add(new PropertyEntry("Prefix", textFieldPrefix));
		generalProperties.add(new PropertyEntry("Exponent", textFieldExponent));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		setTitledPanes(panes);
		
		setItemFactory(this::createItem);
		setTemplateSetter(this::setEditTemplate);
		setChangeApplier(this::applyChanges);
	}

	@Override
	protected void setEditTemplate(UnitPrefix item) {
		textFieldName.setText(item.getName());
		textFieldPrefix.setText(item.getPrefix());
		textFieldExponent.setText(item.getExponent().toString());
	}

	@Override
	public UnitPrefix createItem() {
		String name = textFieldName.getText();
		String prefix = textFieldPrefix.getText();
		int exponent = Integer.valueOf(textFieldExponent.getText());
		return new UnitPrefix(name, prefix, exponent);
	}

	@Override
	protected void applyChanges(UnitPrefix item) {
		String name = textFieldName.getText();
		String prefix = textFieldPrefix.getText();
		int exponent = Integer.valueOf(textFieldExponent.getText());
    	
		item.setName(name);
		item.setPrefix(prefix);
		item.setExponent(exponent);
	}

}
