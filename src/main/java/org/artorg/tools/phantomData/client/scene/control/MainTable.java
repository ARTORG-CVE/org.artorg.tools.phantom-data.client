package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.scene.control.table.FilterTable;
import org.artorg.tools.phantomData.client.scene.control.table.Table;
import org.artorg.tools.phantomData.client.scene.control.table.TableViewCrud;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class MainTable extends AnchorPaneAddableTo {
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
        
//        ContextMenu cm = new ContextMenu();
//        MenuItem mi1 = new MenuItem("Menu 1");
//        cm.getItems().add(mi1);
//        MenuItem mi2 = new MenuItem("Menu 2");
//        cm.getItems().add(mi2);

        
//        view.getGraphic().setContextMenu(cm);
        
//        
//        view.getGraphic().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
//
//            @Override
//            public void handle(MouseEvent t) {
//                if(t.getButton() == MouseButton.SECONDARY) {
//                    cm.show(view.getGraphic(), t.getScreenX(), t.getScreenY());
//                }
//            }
//        });
	}
	
	public void undo() {
		table.getUndoManager().undo();
	}
	
	public void redo() {
		table.getUndoManager().redo();
	}

}
