package org.artorg.tools.phantomData.client.controllers.editFactories;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class AnnulusDiameterEditFactoryController extends GroupedItemEditFactoryController<AnnulusDiameter> {
	private TableViewSpring<AnnulusDiameter> table;
	private Label labelShortcut;
	private TextField textFieldValue;
	
	{
		labelShortcut = new Label();
		textFieldValue = new TextField();
		labelShortcut.setDisable(true);
	}
	
	public AnnulusDiameterEditFactoryController(TableViewSpring<AnnulusDiameter> table) {
		this.table = table;
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
	protected void setTemplate(AnnulusDiameter item) {
		textFieldValue.setText(Double.toString(item.getValue()));
		updateLabel();
	}

	@Override
	protected void copy(AnnulusDiameter from, AnnulusDiameter to) {
		to.setShortcut(from.getShortcut());
		to.setValue(from.getValue());
	}

	@Override
	protected TableViewSpring<AnnulusDiameter> getTable() {
		return table;
	}

	@Override
	protected List<TitledPane> createGroupedProperties() {
		List<TitledPane> panes = new ArrayList<TitledPane>();
		
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("Shortcut [mm]", labelShortcut));
		generalProperties.add(new PropertyEntry("Diameter [mm]", textFieldValue, () -> updateLabel()));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		
		return panes;
	}

}
