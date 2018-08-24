package org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItem;


import org.artorg.tools.phantomData.client.table.multiSelectComboBox.IMultiSelectComboBox;

import javafx.scene.Node;

public interface Item {
	
	void setComboBoxParent(IMultiSelectComboBox multiSelectComboBox);

	IMultiSelectComboBox getComboBoxParent();
	
	void reset();
	
	Node getNode();
	
	boolean isDefault();

}
