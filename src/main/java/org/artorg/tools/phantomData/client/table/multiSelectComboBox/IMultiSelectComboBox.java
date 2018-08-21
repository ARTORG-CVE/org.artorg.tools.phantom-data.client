package org.artorg.tools.phantomData.client.table.multiSelectComboBox;

import java.util.stream.Stream;

import javafx.scene.Node;
import javafx.scene.image.Image;

public interface IMultiSelectComboBox {
	
	Stream<Node> getNodeStream();
	
	
	void setImage(Image image);
	

}
