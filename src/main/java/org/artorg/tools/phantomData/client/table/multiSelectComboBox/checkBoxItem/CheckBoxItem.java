package org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItem;

import org.artorg.tools.phantomData.client.table.multiSelectComboBox.IMultiSelectComboBox;

import javafx.scene.control.CheckBox;

public abstract class CheckBoxItem extends CheckBox implements Item {
	private IMultiSelectComboBox parent;
	
	@Override
	public void setComboBoxParent(IMultiSelectComboBox multiSelectComboBox) {
		this.parent = multiSelectComboBox;
	}

	@Override
	public IMultiSelectComboBox getComboBoxParent() {
		return parent;
	}

}
