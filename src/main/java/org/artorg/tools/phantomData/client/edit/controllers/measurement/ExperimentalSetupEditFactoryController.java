package org.artorg.tools.phantomData.client.edit.controllers.measurement;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.edit.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.edit.PropertyEntry;
import org.artorg.tools.phantomData.client.edit.TitledPropertyPane;
import org.artorg.tools.phantomData.server.model.measurement.ExperimentalSetup;

import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class ExperimentalSetupEditFactoryController extends GroupedItemEditFactoryController<ExperimentalSetup> {
	
	private final TextField textFieldShortName;
	private final TextField textFieldLongName;
	private final TextField textFieldDescription;
	
	{
		textFieldShortName = new TextField();
		textFieldLongName = new TextField();
		textFieldDescription = new TextField();
		
		List<TitledPane> panes = new ArrayList<TitledPane>();
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("Short name", textFieldShortName));
		generalProperties.add(new PropertyEntry("Long name", textFieldLongName));
		generalProperties.add(new PropertyEntry("Description", textFieldDescription));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		setTitledPanes(panes);
		
		setItemFactory(this::createItem);
		setTemplateSetter(this::setEditTemplate);
		setChangeApplier(this::applyChanges);
	}
	
	
	@Override
	public ExperimentalSetup createItem() {
		String shortName = textFieldShortName.getText();
		String longName = textFieldLongName.getText();
		String description = textFieldDescription.getText();
		return new ExperimentalSetup(shortName, longName, description);
	}

	@Override
	protected void setEditTemplate(ExperimentalSetup item) {
		textFieldShortName.setText(item.getShortName());
		textFieldLongName.setText(item.getLongName());
		textFieldDescription.setText(item.getDescription());
	}

	@Override
	protected void applyChanges(ExperimentalSetup item) {
		String shortName = textFieldShortName.getText();
		String longName = textFieldLongName.getText();
		String description = textFieldDescription.getText();
    	
		item.setShortName(shortName);
		item.setLongName(longName);
		item.setDescription(description);
	}
}
