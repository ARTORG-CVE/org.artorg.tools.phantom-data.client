package org.artorg.tools.phantomData.client.controllers.editFactories.measurement;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.server.model.measurement.MeasuredValue;
import org.artorg.tools.phantomData.server.model.measurement.Unit;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class MeasuredValueEditFactoryController
	extends GroupedItemEditFactoryController<MeasuredValue> {
	private TextField textFieldValue;
	private TextField textFieldDescription;
	private ComboBox<Unit> comboBoxUnit;

	{
		textFieldValue = new TextField();
		textFieldDescription = new TextField();
		comboBoxUnit = new ComboBox<Unit>();

		List<TitledPane> panes = new ArrayList<TitledPane>();
		createComboBoxes();
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("Unit", comboBoxUnit));
		generalProperties.add(new PropertyEntry("Value", textFieldValue));
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
		createComboBox(comboBoxUnit, Unit.class,
			item -> item.getShortcut());
	}

	@Override
	protected void setEditTemplate(MeasuredValue item) {
		textFieldValue.setText(item.getValue().toString());
		textFieldDescription.setText(item.getDescription());
		super.selectComboBoxItem(comboBoxUnit, item.getUnit());
	}

	@Override
	public MeasuredValue createItem() {
		Double value = Double.valueOf(textFieldValue.getText());
		String description = textFieldDescription.getText();
		Unit unit =
			comboBoxUnit.getSelectionModel().getSelectedItem();
		return new MeasuredValue(unit, value, description);
	}

	@Override
	protected void applyChanges(MeasuredValue item) {
		Double value = Double.valueOf(textFieldValue.getText());
		String description = textFieldDescription.getText();
		Unit unit =
			comboBoxUnit.getSelectionModel().getSelectedItem();

		item.setValue(value);
		item.setDescription(description);
		item.setUnit(unit);
	}

}
