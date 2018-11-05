package org.artorg.tools.phantomData.client.controllers.editFactories.measurement;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.server.model.measurement.PhysicalQuantity;

import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class PhysicalQuantityEditFactoryController extends GroupedItemEditFactoryController<PhysicalQuantity> {
	private TextField textFieldName;
	private TextField textFieldSymbol;
	private TextField textFieldDescription;

	{
		textFieldName = new TextField();
		textFieldSymbol = new TextField();
		textFieldDescription = new TextField();
		
		List<TitledPane> panes = new ArrayList<TitledPane>();
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("Name", textFieldName));
		generalProperties.add(new PropertyEntry("Symbol", textFieldSymbol));
		generalProperties.add(new PropertyEntry("Description", textFieldDescription));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		setTitledPanes(panes);
		
		setItemFactory(this::createItem);
		setTemplateSetter(this::setEditTemplate);
		setChangeApplier(this::applyChanges);
	}

	@Override
	protected void setEditTemplate(PhysicalQuantity item) {
		textFieldName.setText(item.getName());
		textFieldSymbol.setText(item.getCommonSymbols());
		textFieldDescription.setText(item.getDescription());
	}

	@Override
	public PhysicalQuantity createItem() {
		String name = textFieldName.getText();
		String symbol = textFieldSymbol.getText();
		String description = textFieldDescription.getText();
		return new PhysicalQuantity(name, symbol, description);
	}

	@Override
	protected void applyChanges(PhysicalQuantity item) {
		String name = textFieldName.getText();
		String symbol = textFieldSymbol.getText();
		String description = textFieldDescription.getText();
    	
		item.setName(name);
		item.setCommonSymbols(symbol);
		item.setDescription(description);
	}

}