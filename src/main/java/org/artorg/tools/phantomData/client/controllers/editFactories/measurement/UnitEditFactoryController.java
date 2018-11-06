package org.artorg.tools.phantomData.client.controllers.editFactories.measurement;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.server.model.measurement.PhysicalQuantity;
import org.artorg.tools.phantomData.server.model.measurement.Unit;
import org.artorg.tools.phantomData.server.model.measurement.UnitPrefix;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class UnitEditFactoryController
	extends GroupedItemEditFactoryController<Unit> {
	private TextField textFieldShortcut;
	private TextField textFieldDescription;
	private ComboBox<PhysicalQuantity> comboBoxPhysicalQuantity;
	private ComboBox<UnitPrefix> comboBoxUnitPrefix;

	{
		textFieldShortcut = new TextField();
		textFieldDescription = new TextField();
		comboBoxPhysicalQuantity = new ComboBox<PhysicalQuantity>();
		comboBoxUnitPrefix = new ComboBox<UnitPrefix>();

		List<TitledPane> panes = new ArrayList<TitledPane>();
		createComboBoxes();
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("PhysicalQuantity", comboBoxPhysicalQuantity));
		generalProperties.add(new PropertyEntry("UnitPrefix", comboBoxUnitPrefix));
		generalProperties.add(new PropertyEntry("Shortcut", textFieldShortcut));
		generalProperties.add(new PropertyEntry("Description", textFieldDescription));
		
		TitledPropertyPane generalPane =
			new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		setTitledPanes(panes);

		setItemFactory(this::createItem);
		setTemplateSetter(this::setEditTemplate);
		setChangeApplier(this::applyChanges);
	}

	private void createComboBoxes() {
		createComboBox(comboBoxPhysicalQuantity, PhysicalQuantity.class,
			item -> String.valueOf(item.getName()));
		createComboBox(comboBoxUnitPrefix, UnitPrefix.class,
			item -> item.getPrefix() + ": " + item.getName());
	}

	@Override
	protected void setEditTemplate(Unit item) {
		textFieldShortcut.setText(item.getShortcut());
		textFieldDescription.setText(item.getDescription());
		super.selectComboBoxItem(comboBoxPhysicalQuantity, item.getPhysicalQuantity());
		super.selectComboBoxItem(comboBoxUnitPrefix, item.getUnitPrefix());
	}

	@Override
	public Unit createItem() {
		String shortcut = textFieldShortcut.getText();
		String description = textFieldDescription.getText();
		PhysicalQuantity physicalQuantity = comboBoxPhysicalQuantity.getSelectionModel().getSelectedItem();
		UnitPrefix unitPrefix = comboBoxUnitPrefix.getSelectionModel().getSelectedItem();
		return new Unit(shortcut, description, physicalQuantity, unitPrefix);
	}

	@Override
	protected void applyChanges(Unit item) {
		String shortcut = textFieldShortcut.getText();
		String description = textFieldDescription.getText();
		PhysicalQuantity physicalQuantity = comboBoxPhysicalQuantity.getSelectionModel().getSelectedItem();
		UnitPrefix unitPrefix = comboBoxUnitPrefix.getSelectionModel().getSelectedItem();

		item.setShortcut(shortcut);
		item.setDescription(description);
		item.setPhysicalQuantity(physicalQuantity);
		item.setUnitPrefix(unitPrefix);
	}

}
