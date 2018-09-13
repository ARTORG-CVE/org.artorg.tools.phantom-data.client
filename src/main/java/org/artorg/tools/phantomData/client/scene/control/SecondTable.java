package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.scene.control.table.FilterTable;
import org.artorg.tools.phantomData.client.scene.control.table.Table;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewCrud;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.scene.layout.AnchorPane;

public class SecondTable extends AnchorPaneAddableTo {
	private FilterTable<?, ?, ?> table;
	private TableViewCrud<?, ?, ?> view;
	
	public <TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
		ITEM extends DatabasePersistent<ID_TYPE>, 
		ID_TYPE> 
		void setTable(
			FilterTable<TABLE, ITEM, ID_TYPE> table) {
		this.table = table;
		TableViewCrud<TABLE, ITEM, ID_TYPE> view = new TableViewCrud<TABLE, ITEM, ID_TYPE>();
		view.setTable(table);
		this.view = view;
		super.getChildren().removeAll(super.getChildren());
		super.getChildren().add(view.getGraphic());
		AnchorPane.setBottomAnchor(view.getGraphic(), 0.0);
  	  	AnchorPane.setLeftAnchor(view.getGraphic(), 0.0);
        AnchorPane.setRightAnchor(view.getGraphic(), 0.0);
        AnchorPane.setTopAnchor(view.getGraphic(), 0.0);
	}
	
	public void undo() {
		table.getUndoManager().undo();
	}
	
	public void redo() {
		table.getUndoManager().redo();
	}
}
