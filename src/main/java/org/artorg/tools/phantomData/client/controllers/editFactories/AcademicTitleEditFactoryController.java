package org.artorg.tools.phantomData.client.controllers.editFactories;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.server.model.person.AcademicTitle;

import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class AcademicTitleEditFactoryController extends GroupedItemEditFactoryController<AcademicTitle> {
	private TextField textFieldPrefix;
	private TextField textFieldDescription;
	
	{
		textFieldPrefix = new TextField();
		textFieldDescription = new TextField();
		
		List<TitledPane> panes = new ArrayList<TitledPane>();
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("prefix", textFieldPrefix));
		generalProperties.add(new PropertyEntry("description", textFieldDescription));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		setTitledPanes(panes);
		
		setItemFactory(this::createItem);
		setTemplateSetter(this::setEditTemplate);
		setChangeApplier(this::applyChanges);
	}
	
	@Override
	public void initDefaultValues() {
		textFieldPrefix.setText("");
		textFieldPrefix.setText("");
	}

	@Override
	public AcademicTitle createItem() {
		String prefix = textFieldPrefix.getText();
		String description = textFieldDescription.getText();
		return new AcademicTitle(prefix, description);
	}

	@Override
	protected void setEditTemplate(AcademicTitle item) {
		textFieldPrefix.setText(item.getPrefix());
		textFieldDescription.setText(item.getDescription());
	}

	@Override
	protected void applyChanges(AcademicTitle item) {
		String prefix = textFieldPrefix.getText();
		String description = textFieldDescription.getText();
		
		item.setPrefix(prefix);
		item.setDescription(description);
	}
	
}
