package org.artorg.tools.phantomData.client.controllers.editFactories;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.FabricationType;
import org.artorg.tools.phantomData.server.model.LiteratureBase;
import org.artorg.tools.phantomData.server.model.Phantom;
import org.artorg.tools.phantomData.server.model.Special;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class PhantomEditFactoryController extends GroupedItemEditFactoryController<Phantom> {
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
		
		List<TitledPane> panes = new ArrayList<TitledPane>();
		createComboBoxes();
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("PID", labelIdValue));
		generalProperties.add(new PropertyEntry("Annulus diameter [mm]", comboBoxAnnulus));
		generalProperties.add(new PropertyEntry("Fabrication Type", comboBoxFabricationType));
		generalProperties.add(new PropertyEntry("Literarure Base", comboBoxLiterature));
		generalProperties.add(new PropertyEntry("Special", comboBoxSpecials));
		generalProperties.add(new PropertyEntry("Phantom specific Number", textFieldModelNumber, () -> updateId()));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		setTitledPanes(panes);
		
		setItemFactory(this::createItem);
		setTemplateSetter(this::setTemplate);
		setItemCopier(this::copy);
		
	}
	
	private void updateId() {
		try {
			labelIdValue.setText(createItem().getProductId());
		} catch (Exception e) {}
    }
	
	private void createComboBoxes() {
        createComboBox(comboBoxAnnulus, AnnulusDiameter.class, d -> String.valueOf(d.getValue()), item -> updateId());
        createComboBox(comboBoxFabricationType, FabricationType.class, f -> f.getValue(), item -> updateId());
        createComboBox(comboBoxLiterature, LiteratureBase.class, l -> l.getValue(), item -> updateId());
        createComboBox(comboBoxSpecials, Special.class, s -> s.getShortcut(), item -> updateId());
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
	protected void setTemplate(Phantom item) {
		super.selectComboBoxItem(comboBoxAnnulus, item.getAnnulusDiameter());
		super.selectComboBoxItem(comboBoxFabricationType, item.getFabricationType());
		super.selectComboBoxItem(comboBoxLiterature, item.getLiteratureBase());
		super.selectComboBoxItem(comboBoxSpecials, item.getSpecial());
		textFieldModelNumber.setText(Integer.toString(item.getNumber()));
	}

	@Override
	protected void copy(Phantom from, Phantom to) {
		to.setAnnulusDiameter(from.getAnnulusDiameter());
		to.setFabricationType(from.getFabricationType());
		to.setFiles(from.getFiles());
		to.setLiteratureBase(from.getLiteratureBase());
		to.setNumber(from.getNumber());
		to.setProductId(from.getProductId());
		to.setSpecial(from.getSpecial());
		
		to.setBooleanProperties(from.getBooleanProperties());
		to.setDateProperties(from.getDateProperties());
		to.setDoubleProperties(from.getDoubleProperties());
		to.setIntegerProperties(from.getIntegerProperties());
		to.setStringProperties(from.getStringProperties());
	}
	
}
