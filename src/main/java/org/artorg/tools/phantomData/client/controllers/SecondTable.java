package org.artorg.tools.phantomData.client.controllers;

import org.artorg.tools.phantomData.client.scene.control.ProTableView;
import org.artorg.tools.phantomData.client.scene.layout.AnchorPaneAddableTo;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.scene.layout.AnchorPane;

public class SecondTable extends AnchorPaneAddableTo {
	
	public <ITEM extends DbPersistent<ITEM,?>> 
		void setTableView(
				ProTableView<ITEM> table) {
		super.getChildren().removeAll(super.getChildren());
		super.getChildren().add(table);
		AnchorPane.setBottomAnchor(table, 0.0);
  	  	AnchorPane.setLeftAnchor(table, 0.0);
        AnchorPane.setRightAnchor(table, 0.0);
        AnchorPane.setTopAnchor(table, 0.0);
	}
	
	public void undo() {
//		table.getTable().getUndoManager().undo();
	}
	
	public void redo() {
//		table.getTable().getUndoManager().redo();
	}
}
