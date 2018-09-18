package org.artorg.tools.phantomData.client.controllers.editTable;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.AnnulusDiameterConnector;
import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddAnnulusDiameterController extends AddEditController<AnnulusDiameter, Integer> {
	private Label labelShortcut;
	private TextField textFieldValue;
	
	{
		labelShortcut = new Label();
		textFieldValue = new TextField();
		labelShortcut.setDisable(true);
		
		super.addProperty("Shortcut [mm]", labelShortcut);
		super.addProperty("Diameter [mm]", textFieldValue, () -> updateLabel());
		
		super.create();
	}

	private void updateLabel() {
		Integer shortcut = Integer.valueOf(textFieldValue.getText());
		labelShortcut.setText(String.valueOf(shortcut));
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
	protected HttpConnectorSpring<AnnulusDiameter, Integer> getConnector() {
		return AnnulusDiameterConnector.get();
	}

}
