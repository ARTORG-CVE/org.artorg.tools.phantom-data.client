package org.artorg.tools.phantomData.client.edit.controllers.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.edit.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.edit.PropertyEntry;
import org.artorg.tools.phantomData.client.edit.TitledPropertyPane;
import org.artorg.tools.phantomData.server.model.phantom.AnnulusDiameter;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class AnnulusDiameterEditFactoryController extends GroupedItemEditFactoryController<AnnulusDiameter> {
	private Label labelShortcut;
	private TextField textFieldValue;
	
	{
		labelShortcut = new Label();
		textFieldValue = new TextField();
		labelShortcut.setDisable(true);
		
		List<TitledPane> panes = new ArrayList<TitledPane>();
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("Shortcut [mm]", labelShortcut));
		generalProperties.add(new PropertyEntry("Diameter [mm]", textFieldValue, () -> updateLabel()));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		setTitledPanes(panes);
		
		setItemFactory(this::createItem);
		setTemplateSetter(this::setEditTemplate);
		setChangeApplier(this::applyChanges);
	}

	private void updateLabel() {
		try {
			Integer shortcut = Double.valueOf(textFieldValue.getText()).intValue();
			labelShortcut.setText(String.valueOf(shortcut));
		} catch (Exception e) {}
	}
	
	@Override
	public void initDefaultValues() {
		labelShortcut.setText("0");
		textFieldValue.setText("0.0");
	}

	@Override
	public AnnulusDiameter createItem() {
		Integer shortcut = Integer.valueOf(labelShortcut.getText());
		Double value = Double.valueOf(textFieldValue.getText());
		return new AnnulusDiameter(shortcut, value);
	}

	@Override
	protected void setEditTemplate(AnnulusDiameter item) {
		textFieldValue.setText(Double.toString(item.getValue()));
		updateLabel();
	}

	@Override
	protected void applyChanges(AnnulusDiameter item) {
		Integer shortcut = Integer.valueOf(labelShortcut.getText());
		Double value = Double.valueOf(textFieldValue.getText());
    	
		item.setShortcut(shortcut);
		item.setValue(value);
	}

}
