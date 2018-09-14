package org.artorg.tools.phantomData.client.controllers;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.util.Callback;

public abstract class AddItemController {
	private GridPane gridPane;
	
	{
		gridPane = new GridPane();
	}
	
	protected <T extends DatabasePersistent<ID_TYPE>, ID_TYPE> void createComboBox(ComboBox<T> comboBox, 
			HttpDatabaseCrud<T, ID_TYPE> connector, Function<T,String> mapper, Consumer<T> selectedItemChangedConsumer) {
    	List<T> fabricationType = connector.readAllAsStream()
        		.distinct().collect(Collectors.toList());
    	comboBox.setItems(FXCollections.observableList(fabricationType));
    	comboBox.getSelectionModel().selectFirst();
        Callback<ListView<T>, ListCell<T>> cellFactory = createComboBoxCellFactory(mapper);
        comboBox.setButtonCell(cellFactory.call(null));
        comboBox.setCellFactory(cellFactory);
        
        ChangeListener<T> listener = (observable, oldValue, newValue) -> selectedItemChangedConsumer.accept(newValue);
        comboBox.getSelectionModel().selectedItemProperty().addListener(listener);
    }
    
    protected <T> Callback<ListView<T>, ListCell<T>> createComboBoxCellFactory(Function<T,String> mapper) {
    	return param -> {
        	return new ListCell<T>() {
				@Override
	            protected void updateItem(T item, boolean empty) {
	                super.updateItem(item, empty);
	                if (item == null || empty) {
	                    setGraphic(null);
	                } else {
	                    setText(mapper.apply(item));
	                }
	            }
			};
        };
    }
    
    private int nRows = 0;
    
    protected void addProperty(String labelText, Control node) {
    	int row = nRows;
    	nRows++;
    	
    	gridPane.add(new Label(labelText), 0, row, 1, 1);
		gridPane.add(node, 1, row, 1, 1);
		GridPane.setHgrow(node, Priority.ALWAYS);
		node.setMaxWidth(Double.MAX_VALUE);
    }
    
//    private int getRowCount(GridPane pane) {
//        int numRows = pane.getRowConstraints().size();
//        for (int i = 0; i < pane.getChildren().size(); i++) {
//            Node child = pane.getChildren().get(i);
//            if (child.isManaged()) {
//                Integer rowIndex = GridPane.getRowIndex(child);
//                if(rowIndex != null){
//                    numRows = Math.max(numRows,rowIndex+1);
//                }
//            }
//        }
//        return numRows;
//    }
    
    protected void setGridPane(GridPane gridPane) {
    	this.gridPane = gridPane;
    	
//    	final ColumnConstraints col1 = new ColumnConstraints(140, 160, 180);
//    	col1.setHgrow(Priority.ALWAYS);
//        final ColumnConstraints col2 = new ColumnConstraints();
//        col2.setMaxWidth(Double.MAX_VALUE);
//    	col2.setHgrow(Priority.ALWAYS);
//    	col2.setFillWidth(true);
//    	col2.setMaxWidth(Double.MAX_VALUE);
//    	col2.setHalignment(HPos.RIGHT);
//    	
//    	final RowConstraints row1 = new RowConstraints();
//    	row1.setVgrow(Priority.NEVER);
//    	row1.setPrefHeight(30.);
//    	row1.setMinHeight(30.0);
//    	final RowConstraints row2 = new RowConstraints();
//    	row2.setVgrow(Priority.NEVER);
//    	row2.setPrefHeight(30.);
//    	row2.setMinHeight(30.0);
//    	final RowConstraints row3 = new RowConstraints();
//    	row3.setVgrow(Priority.NEVER);
//    	row3.setPrefHeight(30.);
//    	row3.setMinHeight(30.0);
//    	final RowConstraints row4 = new RowConstraints();
//    	row4.setVgrow(Priority.NEVER);
//    	row4.setPrefHeight(30.);
//    	row4.setMinHeight(30.0);
//    	final RowConstraints row5 = new RowConstraints();
//    	row5.setVgrow(Priority.NEVER);
//    	row5.setPrefHeight(30.);
//    	row5.setMinHeight(30.0);
//    	final RowConstraints row6 = new RowConstraints();
//    	row6.setVgrow(Priority.NEVER);
//    	row6.setPrefHeight(30.);
//    	row6.setMinHeight(30.0);
//    	
//    	gridPane.setGridLinesVisible(true);
//        
//        gridPane.getRowConstraints().addAll(row1, row2, row3, row4, row5, row6);
//        gridPane.getColumnConstraints().addAll(col1, col2);
    	
    }
}
