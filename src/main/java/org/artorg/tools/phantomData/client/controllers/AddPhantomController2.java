package org.artorg.tools.phantomData.client.controllers;

import org.artorg.tools.phantomData.client.connectors.AnnulusDiameterConnector;
import org.artorg.tools.phantomData.client.connectors.FabricationTypeConnector;
import org.artorg.tools.phantomData.client.connectors.LiteratureBaseConnector;
import org.artorg.tools.phantomData.client.connectors.SpecialConnector;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.FabricationType;
import org.artorg.tools.phantomData.server.model.LiteratureBase;
import org.artorg.tools.phantomData.server.model.Phantom;
import org.artorg.tools.phantomData.server.model.Special;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class AddPhantomController2 extends AddItemController {
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
	}
	
	public AnchorPane create() {
		AnchorPane pane = new AnchorPane();
		VBox vBox = new VBox();
		GridPane gridPane = new GridPane();
		
		super.setGridPane(gridPane);
		
		super.addProperty("Id", labelIdValue);
		super.addProperty("Annulus diameter [mm]", comboBoxAnnulus);
		super.addProperty("Fabrication Type", comboBoxFabricationType);
		super.addProperty("Literarure Base", comboBoxLiterature);
		super.addProperty("Special", comboBoxSpecials);
		super.addProperty("Phantom specific Number", textFieldModelNumber);
		
		pane.getChildren().add(vBox);
		vBox.getChildren().add(gridPane);
		
		FxUtil.setAnchorZero(vBox);
		gridPane.prefWidthProperty().bind(pane.widthProperty());
		
		updateComboBoxes();
        
        textFieldModelNumber.setText("1");
        textFieldModelNumber.textProperty().addListener(event -> {
        	try {
        		updateId();
        	} catch( Exception e) {}
    	});
        
        updateId();
		
		return pane;
		
	}
	
	private Phantom createPhantom() {
    	AnnulusDiameter annulusDiameter = comboBoxAnnulus.getSelectionModel().getSelectedItem();
    	FabricationType fabricationType = comboBoxFabricationType.getSelectionModel().getSelectedItem();
    	LiteratureBase literatureBase = comboBoxLiterature.getSelectionModel().getSelectedItem();
    	Special special = comboBoxSpecials.getSelectionModel().getSelectedItem();
    	String sNumber = textFieldModelNumber.getText();
    	int number = Integer.valueOf(sNumber);
    	
    	return new Phantom(annulusDiameter, fabricationType, literatureBase, special, number);
    }
    
    private void updateComboBoxes() {
        createComboBox(comboBoxAnnulus, AnnulusDiameterConnector.get(), d -> String.valueOf(d.getValue()), item -> updateId());
        createComboBox(comboBoxFabricationType, FabricationTypeConnector.get(), f -> f.getValue(), item -> updateId());
        createComboBox(comboBoxLiterature, LiteratureBaseConnector.get(), l -> l.getValue(), item -> updateId());
        createComboBox(comboBoxSpecials, SpecialConnector.get(), s -> s.getShortcut(), item -> updateId());
    }
    
    private void updateId() {
    	labelIdValue.setText(createPhantom().getProductId());
    }
    
}
