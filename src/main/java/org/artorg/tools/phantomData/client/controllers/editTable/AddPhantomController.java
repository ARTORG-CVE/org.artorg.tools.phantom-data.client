package org.artorg.tools.phantomData.client.controllers.editTable;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.AnnulusDiameterConnector;
import org.artorg.tools.phantomData.client.connectors.FabricationTypeConnector;
import org.artorg.tools.phantomData.client.connectors.LiteratureBaseConnector;
import org.artorg.tools.phantomData.client.connectors.PhantomConnector;
import org.artorg.tools.phantomData.client.connectors.SpecialConnector;
import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.FabricationType;
import org.artorg.tools.phantomData.server.model.LiteratureBase;
import org.artorg.tools.phantomData.server.model.Phantom;
import org.artorg.tools.phantomData.server.model.Special;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddPhantomController extends AddEditController<Phantom, Integer> {
	private Label labelIdValue;
    private ComboBox<AnnulusDiameter> comboBoxAnnulus;
    private ComboBox<FabricationType> comboBoxFabricationType;
    private ComboBox<LiteratureBase> comboBoxLiterature;
    private ComboBox<Special> comboBoxSpecials;
	private TextField textFieldModelNumber;
	
	{
		labelIdValue = new Label("id");
		comboBoxAnnulus = new ComboBox<AnnulusDiameter>();
		comboBoxFabricationType = new ComboBox<FabricationType>();
		comboBoxLiterature = new ComboBox<LiteratureBase>();
		comboBoxSpecials = new ComboBox<Special>();
		textFieldModelNumber = new TextField();
		
		labelIdValue.setDisable(true);
		
		super.addProperty("Id", labelIdValue);
		super.addProperty("Annulus diameter [mm]", comboBoxAnnulus);
		super.addProperty("Fabrication Type", comboBoxFabricationType);
		super.addProperty("Literarure Base", comboBoxLiterature);
		super.addProperty("Special", comboBoxSpecials);
		super.addProperty("Phantom specific Number", textFieldModelNumber, () -> updateId());
		
		createComboBoxes();
        
        super.create();
	}
	
	private void updateId() {
    	labelIdValue.setText(createItem().getProductId());
    }
	
	private void createComboBoxes() {
        createComboBox(comboBoxAnnulus, AnnulusDiameterConnector.get(), d -> String.valueOf(d.getValue()), item -> updateId());
        createComboBox(comboBoxFabricationType, FabricationTypeConnector.get(), f -> f.getValue(), item -> updateId());
        createComboBox(comboBoxLiterature, LiteratureBaseConnector.get(), l -> l.getValue(), item -> updateId());
        createComboBox(comboBoxSpecials, SpecialConnector.get(), s -> s.getShortcut(), item -> updateId());
    }
	
	@Override
	public void initDefaultValues() {
		comboBoxAnnulus.getSelectionModel().clearSelection();
		comboBoxFabricationType.getSelectionModel().clearSelection();
		comboBoxLiterature.getSelectionModel().clearSelection();
		comboBoxSpecials.getSelectionModel().clearSelection();
		textFieldModelNumber.setText("1");
	}

	@Override
	public Phantom createItem() {
		AnnulusDiameter annulusDiameter = comboBoxAnnulus.getSelectionModel().getSelectedItem();
    	FabricationType fabricationType = comboBoxFabricationType.getSelectionModel().getSelectedItem();
    	LiteratureBase literatureBase = comboBoxLiterature.getSelectionModel().getSelectedItem();
    	Special special = comboBoxSpecials.getSelectionModel().getSelectedItem();
    	String sNumber = textFieldModelNumber.getText();
    	int number = Integer.valueOf(sNumber);
    	
    	return new Phantom(annulusDiameter, fabricationType, literatureBase, special, number);
	}

	@Override
	public HttpConnectorSpring<Phantom, Integer> getConnector() {
		return PhantomConnector.get();
	}
    
}
