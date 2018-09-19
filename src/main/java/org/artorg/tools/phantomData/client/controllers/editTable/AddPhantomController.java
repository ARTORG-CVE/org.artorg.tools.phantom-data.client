package org.artorg.tools.phantomData.client.controllers.editTable;

import java.util.List;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.AnnulusDiameterConnector;
import org.artorg.tools.phantomData.client.connectors.FabricationTypeConnector;
import org.artorg.tools.phantomData.client.connectors.LiteratureBaseConnector;
import org.artorg.tools.phantomData.client.connectors.PhantomConnector;
import org.artorg.tools.phantomData.client.connectors.SpecialConnector;
import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.FabricationType;
import org.artorg.tools.phantomData.server.model.LiteratureBase;
import org.artorg.tools.phantomData.server.model.Phantom;
import org.artorg.tools.phantomData.server.model.Special;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddPhantomController extends AddEditController<Phantom> {
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
		super.initDefaultValues();
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
	public HttpConnectorSpring<Phantom> getConnector() {
		return PhantomConnector.get();
	}

	@Override
	protected void addPropertyEntries(List<PropertyEntry> entries) {
		createComboBoxes();
		entries.add(new PropertyEntry("Id", labelIdValue));
		entries.add(new PropertyEntry("Annulus diameter [mm]", comboBoxAnnulus));
		entries.add(new PropertyEntry("Fabrication Type", comboBoxFabricationType));
		entries.add(new PropertyEntry("Literarure Base", comboBoxLiterature));
		entries.add(new PropertyEntry("Special", comboBoxSpecials));
		entries.add(new PropertyEntry("Phantom specific Number", textFieldModelNumber, () -> updateId()));
	}

	@Override
	protected void setTemplate(Phantom item) {
		super.selectComboBoxItem(comboBoxAnnulus, item.getAnnulusDiameter());
		super.selectComboBoxItem(comboBoxFabricationType, item.getFabricationType());
		super.selectComboBoxItem(comboBoxLiterature, item.getLiteratureBase());
		super.selectComboBoxItem(comboBoxSpecials, item.getSpecial());
		textFieldModelNumber.setText(Integer.toString(item.getNumber()));
	}
    
}
