package org.artorg.tools.phantomData.client.controllers.editFactories.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.server.model.phantom.Manufacturing;

import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class ManufacturingEditFactoryController extends GroupedItemEditFactoryController<Manufacturing> {
	private final TextField textFieldName;
	private final TextField textFieldDescription;
	
	{
		textFieldName = new TextField();
		textFieldDescription = new TextField();
		
		List<TitledPane> panes = new ArrayList<TitledPane>();
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("Name", textFieldName));
		generalProperties.add(new PropertyEntry("Description", textFieldDescription));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		setTitledPanes(panes);
		
		setItemFactory(this::createItem);
		setTemplateSetter(this::setEditTemplate);
		setChangeApplier(this::applyChanges);
	}
	
	
	@Override
	public Manufacturing createItem() {
		String name = textFieldName.getText();
		String description = textFieldDescription.getText();
		return new Manufacturing(name, description);
	}

	@Override
	protected void setEditTemplate(Manufacturing item) {
		textFieldName.setText(item.getName());
		textFieldDescription.setText(item.getDescription());
	}

	@Override
	protected void applyChanges(Manufacturing item) {
		String name = textFieldName.getText();
		String description = textFieldDescription.getText();
    	
		item.setName(name);
		item.setDescription(description);
	}
}
