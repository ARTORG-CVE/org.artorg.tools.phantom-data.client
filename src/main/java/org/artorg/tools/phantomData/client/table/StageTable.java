package org.artorg.tools.phantomData.client.table;

import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StageTable<TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
		ITEM extends DatabasePersistent<ITEM, ID_TYPE>, 
		ID_TYPE> {
	
	private AnchorPane pane;
	private Stage stage;
	private AnchorPane tablePane;
	private Scene scene;
	private FilterTable<TABLE, ITEM, ID_TYPE> table;
	private TableGui<TABLE, ITEM, ID_TYPE> view;
	
	private void initMenutItemHelper(String name, Runnable actionEvent, Menu parentMenu) {
		MenuItem menuItem = new MenuItem(name);
		menuItem.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		        actionEvent.run();
		    }
		});
		parentMenu.getItems().add(menuItem);
	}
	
	public final Stage getStage() {
		return stage;
	}	
	
	public void autoResize(TableView<ITEM> table, Stage stage) {
		double width = table.getColumns().stream().mapToDouble(c -> c.getPrefWidth()).sum();
		stage.setWidth(width + 17.0d + 50.0d);
	}
	
	public void setView(TableGui<TABLE, ITEM, ID_TYPE> view) {
		view.setTable(table);
		
		view.refresh();
		this.view = view;
		createStage();
		refresh();
		view.addRefreshListener(() -> refresh());
	}	
	
	public void createStage() {
		pane = new AnchorPane();
		tablePane = new AnchorPane();
		scene = new Scene(pane);
		stage = new Stage();
		stage.setScene(scene);
		
		VBox vbox = new VBox();
		pane.getChildren().add(vbox);
		MenuBar menuBar = new MenuBar();
		
		Menu menuFile = new Menu("File");
		initMenutItemHelper("Save", () -> {save();}, menuFile);
		initMenutItemHelper("Refresh", () -> {refresh();}, menuFile);
//		initMenutItemHelper("Reload", () -> {reload();}, menuFile);
		initMenutItemHelper("Close", () -> {stage.close();}, menuFile);
		menuBar.getMenus().add(menuFile);
		
		Menu menuEdit = new Menu("Edit");
		initMenutItemHelper("Undo", () -> {table.getUndoManager().undo();}, menuEdit);
		initMenutItemHelper("Redo", () -> {table.getUndoManager().redo();}, menuEdit);
		menuBar.getMenus().add(menuEdit);
		
		
		
		vbox.getChildren().add(menuBar);
		vbox.getChildren().add(tablePane);
	}
	
	
	public void refresh() {
		System.out.println("stagetable refresh");
		Control control = view.getGraphic();
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
		table.getUndoManager().save();
	}
	
	public void setTable(FilterTable<TABLE, ITEM, ID_TYPE> table) {
		this.table = table;
	}

}
