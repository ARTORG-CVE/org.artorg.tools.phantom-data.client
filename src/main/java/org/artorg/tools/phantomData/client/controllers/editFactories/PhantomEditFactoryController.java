package org.artorg.tools.phantomData.client.controllers.editFactories;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.connector.PersonalizedHttpConnectorSpring;
import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.server.model.phantom.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.phantom.FabricationType;
import org.artorg.tools.phantomData.server.model.phantom.LiteratureBase;
import org.artorg.tools.phantomData.server.model.phantom.Phantom;
import org.artorg.tools.phantomData.server.model.phantom.Phantomina;
import org.artorg.tools.phantomData.server.model.phantom.Special;

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
    	
    	PersonalizedHttpConnectorSpring<Phantomina> phantominaConn = PersonalizedHttpConnectorSpring.getOrCreate(Phantomina.class);
    	Phantomina phantomina = new Phantomina(annulusDiameter, fabricationType, literatureBase, special);
		final Phantomina finalPhantomina = phantomina;
		List<Phantomina> phantominas = phantominaConn.readAllAsStream().filter(p -> p.equals(finalPhantomina)).collect(Collectors.toList());
		if (phantominas.size() == 0)
			phantominaConn.create(phantomina);
		else if (phantominas.size() == 1)
			phantomina = phantominas.get(0);
		else {
			phantomina = phantominas.get(0);
			throw new UnsupportedOperationException();
		}
		
		phantomina.setAnnulusDiameter(annulusDiameter);
		phantomina.setFabricationType(fabricationType);
		phantomina.setLiteratureBase(literatureBase);
		phantomina.setSpecial(special);
		
		return new Phantom(phantomina, number);
	}

	@Override
	protected void setTemplate(Phantom item) {
		super.selectComboBoxItem(comboBoxAnnulus, item.getPhantomina().getAnnulusDiameter());
		super.selectComboBoxItem(comboBoxFabricationType, item.getPhantomina().getFabricationType());
		super.selectComboBoxItem(comboBoxLiterature, item.getPhantomina().getLiteratureBase());
		super.selectComboBoxItem(comboBoxSpecials, item.getPhantomina().getSpecial());
		textFieldModelNumber.setText(Integer.toString(item.getNumber()));
	}

	@Override
	protected void copy(Phantom from, Phantom to) {
//		to.setPhantomina(from.getPhantomina());
		
		to.getPhantomina().setAnnulusDiameter(from.getPhantomina().getAnnulusDiameter());
		to.getPhantomina().setFabricationType(from.getPhantomina().getFabricationType());
		to.getPhantomina().setLiteratureBase(from.getPhantomina().getLiteratureBase());
		to.getPhantomina().setSpecial(from.getPhantomina().getSpecial());
		
		
		to.setNumber(from.getNumber());
		to.setProductId(from.getProductId());
		
		to.setFiles(from.getFiles());
		
		to.setProperties(from.getProperties());
	}
	
}
