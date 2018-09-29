package org.artorg.tools.phantomData.client.controllers;

import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoEditFilterTable;
import org.artorg.tools.phantomData.client.scene.control.DbEditFilterTableView;
import org.artorg.tools.phantomData.client.scene.layout.AnchorPaneAddableTo;
import org.artorg.tools.phantomData.server.specification.DbPersistentUUID;

import javafx.scene.layout.AnchorPane;

public class SecondTable extends AnchorPaneAddableTo {
	private DbUndoRedoEditFilterTable<?> table;
//	private TableViewSpring<?, ?> view;
	
	public <ITEM extends DbPersistentUUID<ITEM>> 
		void setTable(
			DbEditFilterTableView<ITEM> table) {
//		this.table = table;
//		TableViewSpring<ITEM, ID_TYPE> view = new TableViewSpring<ITEM, ID_TYPE>();
//		view.setTable(table);
//		this.view = view;
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
