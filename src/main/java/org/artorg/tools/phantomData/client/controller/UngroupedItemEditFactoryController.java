package org.artorg.tools.phantomData.client.controller;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public abstract class UngroupedItemEditFactoryController<ITEM extends DbPersistent<ITEM>> extends ItemEditFactoryController<ITEM> {
	private List<PropertyEntry> entries;
	
	{
		entries = new ArrayList<PropertyEntry>();
	}
	
	protected abstract List<PropertyEntry> createUngroupedProperties(ITEM item);
	
	@Override
	protected void addProperties(ITEM item) {
		entries.addAll(createUngroupedProperties(item));
	}
	
	@Override
	public List<PropertyEntry> getPropertyEntries() {
		return entries;
	}
	
	@Override
	protected AnchorPane createRootPane() {
    	AnchorPane rootPane = new AnchorPane();
		VBox vBox = new VBox();
    	PropertyGridPane gridPane = new PropertyGridPane(entries);
    	
    	gridPane.setPadding(new Insets(10, 10, 10, 10));
		AnchorPane buttonPane = createButtonPane(applyButton);
		
		rootPane.getChildren().add(vBox);
		vBox.getChildren().add(gridPane);
		vBox.getChildren().add(buttonPane);
		
		VBox.setVgrow(buttonPane, Priority.ALWAYS);
		FxUtil.setAnchorZero(vBox);
		
		return rootPane;
    }
	
}
