package org.artorg.tools.phantomData.client.table;

import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public abstract class StageTable<TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
		ITEM extends DatabasePersistent<ITEM, ID_TYPE>, 
		ID_TYPE> 
		extends Table<TABLE, ITEM, ID_TYPE> {
	
	private final AnchorPane pane;
	private final Stage stage;
	private AnchorPane tablePane;
	private final Scene scene;
	
	{
		pane = new AnchorPane();
		tablePane = new AnchorPane();
		scene = new Scene(pane);
		stage = new Stage();
		stage.setScene(scene);
		
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
		
	}
	
	public final Stage getStage() {
		return stage;
	}	
	
	public void autoResize(TableView<ITEM> table, Stage stage) {
		double width = table.getColumns().stream().mapToDouble(c -> c.getPrefWidth()).sum();
		stage.setWidth(width + 17.0d + 50.0d);
	}
	
	
	public void setGui(TableGui tableGui) {
		javafx.scene.control.Control control = tableGui.getGraphic();
		tablePane.getChildren().clear();
		tablePane.getChildren().add(control);
		AnchorPane.setTopAnchor(tablePane, 0.0);
        AnchorPane.setLeftAnchor(tablePane, 0.0);
        AnchorPane.setRightAnchor(tablePane, 0.0);
        AnchorPane.setBottomAnchor(tablePane, 0.0);
        
        stage.setHeight(control.getMinHeight());
        stage.setWidth(control.getMinWidth());
     
        control.prefWidthProperty().bind(stage.widthProperty());
        control.prefHeightProperty().bind(stage.heightProperty());
        
		stage.setWidth(800);
		stage.setHeight(500); 
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
