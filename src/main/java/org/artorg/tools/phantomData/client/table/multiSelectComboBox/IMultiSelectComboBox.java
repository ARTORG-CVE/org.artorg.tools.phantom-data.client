package org.artorg.tools.phantomData.client.table.multiSelectComboBox;

import java.util.stream.Stream;

import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItem.Item;

import javafx.scene.Node;
import javafx.scene.image.Image;

public interface IMultiSelectComboBox {
	
	Stream<Item> getBoxItemStream();
	
	
	void setImage(Image image);
	
	void reset();
	

}
