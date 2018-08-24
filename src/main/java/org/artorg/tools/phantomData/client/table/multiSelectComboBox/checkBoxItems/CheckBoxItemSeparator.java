package org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItems;

import org.artorg.tools.phantomData.client.table.multiSelectComboBox.IMultiSelectComboBox;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItem.Item;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;

public class CheckBoxItemSeparator extends Separator implements Item {
	private IMultiSelectComboBox parent;
	

	public CheckBoxItemSeparator() {
		this.setOrientation(Orientation.HORIZONTAL);
	}
	
	@Override
	public void setComboBoxParent(IMultiSelectComboBox multiSelectComboBox) {
		this.parent = multiSelectComboBox;
	}

	@Override
	public IMultiSelectComboBox getComboBoxParent() {
		return parent;
	}

	@Override
	public void reset() {
	}

	@Override
	public Node getNode() {
		return this;
	}

	@Override
	public boolean isDefault() {
		return true;
	}

}
