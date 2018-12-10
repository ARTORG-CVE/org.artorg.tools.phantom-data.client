package org.artorg.tools.phantomData.client.itemEdit;

import java.util.List;

import javafx.scene.control.TitledPane;

public class TitledPropertyPane extends TitledPane {
	private final List<PropertyEntry> entries;

	public TitledPropertyPane(List<PropertyEntry> entries, String title) {
		this.entries = entries;
		PropertyGridPane gridPane = new PropertyGridPane(entries);
		setText(title);
    	setContent(gridPane);
	}
	
	public List<PropertyEntry> getEntries() {
		return entries;
	}

}
