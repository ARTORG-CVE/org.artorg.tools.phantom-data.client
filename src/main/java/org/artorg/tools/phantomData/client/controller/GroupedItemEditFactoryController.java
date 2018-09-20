package org.artorg.tools.phantomData.client.controller;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public abstract class GroupedItemEditFactoryController<ITEM extends DatabasePersistent> extends ItemEditFactoryController<ITEM> {
	private List<TitledPropertyPane> panes;
	private List<PropertyEntry> entries;
	
	{
		panes = new ArrayList<TitledPropertyPane>();
		entries = new ArrayList<PropertyEntry>();
	}
	
	protected abstract List<TitledPropertyPane> createProperties();
	
	@Override
	protected void addProperties() {
		panes.addAll(createProperties());
		panes.stream().forEach(p -> entries.addAll(p.getEntries()));
	}
	
	@Override
	protected List<PropertyEntry> getPropertyEntries() {
		return entries;
	}
	
	@Override
	protected AnchorPane createRootPane() {
    	AnchorPane rootPane = new AnchorPane();
		VBox vBox = new VBox();

		AnchorPane buttonPane = createButtonPane(applyButton);
		
		
		rootPane.getChildren().add(vBox);
		vBox.getChildren().addAll(panes);
		vBox.getChildren().add(buttonPane);
		
		VBox.setVgrow(buttonPane, Priority.ALWAYS);
		FxUtil.setAnchorZero(vBox);
		
		return rootPane;
    }
	
	
}
