package org.artorg.tools.phantomData.client.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public abstract class AddEditController<ITEM extends DatabasePersistent<ID_TYPE>, ID_TYPE> {
	private GridPane gridPane;
	private Button applyButton;
	private AnchorPane rootPane;
	private int nRows = 0;
	
	{
		gridPane = new GridPane();
		applyButton = new Button("Apply");
		System.out.println("AddEditController: ");
	}
	
	public abstract void initDefaultValues();
	
	public abstract ITEM createItem();
	
	protected abstract HttpConnectorSpring<ITEM, ID_TYPE> getConnector();
	
	public void applyButton() {
		getConnector().create(createItem());
	}
	
	protected <T extends DatabasePersistent<SUB_ID_TYPE>, SUB_ID_TYPE> void createComboBox(ComboBox<T> comboBox, 
			HttpConnectorSpring<T, SUB_ID_TYPE> connector, Function<T,String> mapper, Consumer<T> selectedItemChangedConsumer) {
    	createComboBox(comboBox, connector, mapper);
        
        ChangeListener<T> listener = (observable, oldValue, newValue) -> {
        	try {
        		selectedItemChangedConsumer.accept(newValue);
        	} catch(Exception e) {}
        };
        comboBox.getSelectionModel().selectedItemProperty().addListener(listener);
    }
	
	protected <T extends DatabasePersistent<SUB_ID_TYPE>, SUB_ID_TYPE> void createComboBox(ComboBox<T> comboBox, 
			HttpConnectorSpring<T, SUB_ID_TYPE> connector, Function<T,String> mapper) {
    	List<T> fabricationType = connector.readAllAsStream()
        		.distinct().collect(Collectors.toList());
    	comboBox.setItems(FXCollections.observableList(fabricationType));
    	comboBox.getSelectionModel().selectFirst();
        Callback<ListView<T>, ListCell<T>> cellFactory = createComboBoxCellFactory(mapper);
        comboBox.setButtonCell(cellFactory.call(null));
        comboBox.setCellFactory(cellFactory);
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
    
    protected void addProperty(String labelText, TextField textField, Runnable rc) {
    	textField.textProperty().addListener(event -> {
    		rc.run();
    	});
    	addProperty(labelText, textField);
    	
    }
    
    protected void addProperty(String labelText, Control node) {
    	int row = nRows;
    	nRows++;
    	
    	gridPane.add(new Label(labelText), 0, row, 1, 1);
		gridPane.add(node, 1, row, 1, 1);
		GridPane.setHgrow(node, Priority.ALWAYS);
		node.setMaxWidth(Double.MAX_VALUE);
    }
    
    protected AnchorPane create(ITEM item) {
		return rootPane;
    }
    
    protected void init() {
    	final RowConstraints row = new RowConstraints();
    	row.setVgrow(Priority.NEVER);
    	row.setPrefHeight(30.0);
    	row.setMinHeight(30.0);
    	List<RowConstraints> rowConstraints = new ArrayList<RowConstraints>();
    	for (int i=0; i<nRows; i++)
    		rowConstraints.add(row);        
        gridPane.getRowConstraints().addAll(rowConstraints);
    	
        final ColumnConstraints col1 = new ColumnConstraints();
    	col1.setHgrow(Priority.ALWAYS);
        final ColumnConstraints col2 = new ColumnConstraints();
        col2.setMaxWidth(Double.MAX_VALUE);
    	col2.setHgrow(Priority.ALWAYS);
    	col2.setFillWidth(true);
    	col2.setMaxWidth(Double.MAX_VALUE);
    	col2.setHalignment(HPos.RIGHT);
    	gridPane.getColumnConstraints().addAll(col1, col2);
        
    	Platform.runLater(() -> {
    		try {
	    		double textWidth = 0;
	    		for (int i=0; i<nRows; i++) {
	    			double width = gridPane.getChildren().get(2*i).getLayoutBounds().getWidth();
	    			if (width > textWidth) textWidth = width;
	    		}
	    		
	    		col1.setMinWidth(textWidth+5.0);
	    		col1.setPrefWidth(textWidth+25.0);
	    		col1.setMaxWidth(textWidth+45.0);
	    		
	    		textWidth = 0;
	    		for (int i=0; i<nRows; i++) {
	    			double width = gridPane.getChildren().get(2*i+1).getLayoutBounds().getWidth();
	    			if (width > textWidth) textWidth = width;
	    		}
	    		
	    		col2.setPrefWidth(textWidth+25.0);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		
		});
    	
        rootPane = new AnchorPane();
		VBox vBox = new VBox();
		rootPane.getChildren().add(vBox);
		vBox.getChildren().add(gridPane);
		
		Pane spacePane = new Pane(); 
		spacePane.setPrefHeight(10.0);
		vBox.getChildren().add(spacePane);
		
		applyButton.setText("Add");
		applyButton.setMaxWidth(Double.MAX_VALUE);
		applyButton.setOnAction(event -> {
			applyButton();
			initDefaultValues();
		});
		VBox.setVgrow(applyButton, Priority.ALWAYS);
		vBox.getChildren().add(applyButton);
		
		FxUtil.setAnchorZero(vBox);
		rootPane.setPadding(new Insets(10, 10, 10, 10));
		
		initDefaultValues();
    }
    
    public GridPane getGridPane() {
		return gridPane;
	}
    
    protected void setGridPane(GridPane gridPane) {
    	this.gridPane = gridPane;
    }
}
