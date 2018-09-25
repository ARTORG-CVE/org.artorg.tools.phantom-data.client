package org.artorg.tools.phantomData.client.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.scene.control.TitledTableViewSelector;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public abstract class GroupedItemEditFactoryController<ITEM extends DbPersistent<ITEM>> extends ItemEditFactoryController<ITEM> {
	private List<TitledPane> panes;
	private List<PropertyEntry> entries;
	
	{
		panes = new ArrayList<TitledPane>();
		entries = new ArrayList<PropertyEntry>();
	}
	
	protected abstract List<TitledPane> createGroupedProperties(ITEM item);
	
	@Override
	protected void addProperties(ITEM item) {
		panes.addAll(createGroupedProperties(item));
		panes.stream().filter(p -> p instanceof TitledPropertyPane)
			.forEach(p -> entries.addAll(((TitledPropertyPane)p).getEntries()));
		
		List<ISelector<ITEM>> selectors = this.getSelectors();
		panes.addAll(selectors.stream()
				.map(selector -> (TitledTableViewSelector<ITEM>)selector)
				.map(selector -> selector.getTitledPane()).collect(Collectors.toList()));
		
	}
	
	@Override
	public List<PropertyEntry> getPropertyEntries() {
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
