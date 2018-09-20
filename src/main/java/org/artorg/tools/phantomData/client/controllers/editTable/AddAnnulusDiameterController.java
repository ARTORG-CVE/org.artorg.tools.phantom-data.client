package org.artorg.tools.phantomData.client.controllers.editTable;

import java.util.List;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.AnnulusDiameterConnector;
import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddAnnulusDiameterController extends AddEditController<AnnulusDiameter> {
	private Label labelShortcut;
	private TextField textFieldValue;
	
	{
		labelShortcut = new Label();
		textFieldValue = new TextField();
		labelShortcut.setDisable(true);
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
	protected HttpConnectorSpring<AnnulusDiameter> getConnector() {
		return AnnulusDiameterConnector.get();
	}

	@Override
	protected void addPropertyEntries(List<PropertyEntry> entries) {
		entries.add(new PropertyEntry("Shortcut [mm]", labelShortcut));
		entries.add(new PropertyEntry("Diameter [mm]", textFieldValue, () -> updateLabel()));
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

}
