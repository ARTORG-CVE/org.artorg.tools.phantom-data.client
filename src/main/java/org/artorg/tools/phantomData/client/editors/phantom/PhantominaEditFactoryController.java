package org.artorg.tools.phantomData.client.editors.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.editor.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.server.model.phantom.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.phantom.FabricationType;
import org.artorg.tools.phantomData.server.model.phantom.LiteratureBase;
import org.artorg.tools.phantomData.server.model.phantom.Phantomina;
import org.artorg.tools.phantomData.server.model.phantom.Special;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;

public class PhantominaEditFactoryController extends GroupedItemEditFactoryController<Phantomina> {
	private Label labelIdValue;
    private ComboBox<AnnulusDiameter> comboBoxAnnulus;
    private ComboBox<FabricationType> comboBoxFabricationType;
    private ComboBox<LiteratureBase> comboBoxLiterature;
    private ComboBox<Special> comboBoxSpecials;
	
	{
		labelIdValue = new Label("id");
		comboBoxAnnulus = new ComboBox<AnnulusDiameter>();
		comboBoxFabricationType = new ComboBox<FabricationType>();
		comboBoxLiterature = new ComboBox<LiteratureBase>();
		comboBoxSpecials = new ComboBox<Special>();
		
		labelIdValue.setDisable(true);
		
		List<TitledPane> panes = new ArrayList<TitledPane>();
		createComboBoxes();
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("PID", labelIdValue));
		generalProperties.add(new PropertyEntry("Annulus diameter [mm]", comboBoxAnnulus));
		generalProperties.add(new PropertyEntry("Fabrication Type", comboBoxFabricationType));
		generalProperties.add(new PropertyEntry("Literarure Base", comboBoxLiterature));
		generalProperties.add(new PropertyEntry("Special", comboBoxSpecials));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		setTitledPanes(panes);
		
		setItemFactory(this::createItem);
		setTemplateSetter(this::setEditTemplate);
		setChangeApplier(this::applyChanges);
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
	public Phantomina createItem() {
		AnnulusDiameter annulusDiameter = comboBoxAnnulus.getSelectionModel().getSelectedItem();
    	FabricationType fabricationType = comboBoxFabricationType.getSelectionModel().getSelectedItem();
    	LiteratureBase literatureBase = comboBoxLiterature.getSelectionModel().getSelectedItem();
    	Special special = comboBoxSpecials.getSelectionModel().getSelectedItem();
		
		return new Phantomina(annulusDiameter, fabricationType, literatureBase, special);
	}

	@Override
	protected void setEditTemplate(Phantomina item) {
		super.selectComboBoxItem(comboBoxAnnulus, item.getAnnulusDiameter());
		super.selectComboBoxItem(comboBoxFabricationType, item.getFabricationType());
		super.selectComboBoxItem(comboBoxLiterature, item.getLiteratureBase());
		super.selectComboBoxItem(comboBoxSpecials, item.getSpecial());
	}
	
	@Override
	protected void applyChanges(Phantomina item) {
		AnnulusDiameter annulusDiameter = comboBoxAnnulus.getSelectionModel().getSelectedItem();
    	FabricationType fabricationType = comboBoxFabricationType.getSelectionModel().getSelectedItem();
    	LiteratureBase literatureBase = comboBoxLiterature.getSelectionModel().getSelectedItem();
    	Special special = comboBoxSpecials.getSelectionModel().getSelectedItem();
    	
    	item.setAnnulusDiameter(annulusDiameter);
    	item.setFabricationType(fabricationType);
    	item.setLiteratureBase(literatureBase);
    	item.setSpecial(special);
	}
	
}
