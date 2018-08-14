package org.artorg.tools.phantomData.client.table;

import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public abstract class StageTable<TABLE extends Table<TABLE, ITEM, ID_TYPE>, ITEM extends DatabasePersistent<ITEM, ID_TYPE>, ID_TYPE> extends Table<TABLE, ITEM, ID_TYPE> {
	
	public final Stage createStage(javafx.scene.control.Control n, String name) {
		AnchorPane pane = new AnchorPane();
		AnchorPane tablePane = new AnchorPane();
		Scene scene = new Scene(pane);
		Stage stage = new Stage();
		
		stage.setScene(scene);
		stage.setTitle(name);
		
		VBox vbox = new VBox();
		pane.getChildren().add(vbox);
		MenuBar menuBar = new MenuBar();
		Menu menuFile = new Menu("File");
		MenuItem menuItemSave = new MenuItem("Save");
		menuItemSave.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		        save();
		    }
		});
		menuFile.getItems().add(menuItemSave);
		menuBar.getMenus().add(menuFile);
		
		vbox.getChildren().add(menuBar);
		vbox.getChildren().add(tablePane);
		
		
		tablePane.getChildren().add(n);
		AnchorPane.setTopAnchor(tablePane, 0.0);
        AnchorPane.setLeftAnchor(tablePane, 0.0);
        AnchorPane.setRightAnchor(tablePane, 0.0);
        AnchorPane.setBottomAnchor(tablePane, 0.0);
        
        stage.setHeight(n.getMinHeight());
        stage.setWidth(n.getMinWidth());
     
        n.prefWidthProperty().bind(stage.widthProperty());
        n.prefHeightProperty().bind(stage.heightProperty());
        
		stage.setWidth(800);
		stage.setHeight(500);        
        
		return stage;
	}
	
	public void save() {
		super.getUndoManager().save();
		
////		System.out.println("//////////////////////");
////		this.getItems().forEach(i -> System.out.println(i.toString()));
////		System.out.println("//////////////////////");
////		
////		this.getConnector().update(this.getItems());
////		
////		System.out.println("//////////////////////");
////		this.getItems().forEach(i -> System.out.println(i.toString()));
////		System.out.println("//////////////////////");
////		
//		System.out.println("//////////////////////");
////		this.getData().stream().flatMap(c -> c.stream()).forEach(i -> {
////			System.out.println(i.toString());
////		});
//		System.out.println("//////////////////////");
////		
////		
////		List<List<String>> rows = this.getData();
		
		
	}

}
