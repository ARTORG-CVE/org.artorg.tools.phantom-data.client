package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.scene.control.table.FilterTableSpringDb;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewSpring;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.scene.layout.AnchorPane;

public class SecondTable extends AnchorPaneAddableTo {
	private FilterTableSpringDb<?> table;
//	private TableViewSpring<?, ?> view;
	
	public <ITEM extends DatabasePersistent> 
		void setTable(
			TableViewSpring<ITEM> table) {
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
		table.getUndoManager().undo();
	}
	
	public void redo() {
		table.getUndoManager().redo();
	}
}
