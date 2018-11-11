package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.scene.layout.AnchorPaneAddableTo;
import org.artorg.tools.phantomData.client.util.FxUtil;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

public class VGridBoxPane extends AnchorPaneAddableTo {
	private final AnchorPane rootPane;
	private final VBox vBox;
	private final GridPane gridPane;
	private int nRows = 0;
	private double prefRowHeight;
	
	{
		rootPane = this;
		vBox = new VBox();
		gridPane = new GridPane();
		prefRowHeight = 30.0;
		
		FxUtil.addToPane(rootPane, vBox);
		vBox.getChildren().add(gridPane);
	}
	
	public void addColumn(double width) {
		ColumnConstraints columnContraints = new ColumnConstraints();
		columnContraints.setPrefWidth(width);
		gridPane.getColumnConstraints().add(columnContraints);
	}
	
	public void setPrefRowHeight(double height) {
		prefRowHeight = height;
		gridPane.getRowConstraints().forEach(rowContraints -> {
			rowContraints.setPrefHeight(height);
		});
	}
	
	public static AnchorPane createButtonPane(Button button) {
    	button.setPrefHeight(25.0);
    	button.setMaxWidth(Double.MAX_VALUE);
		AnchorPane buttonPane = new AnchorPane();
		buttonPane.setPrefHeight(button.getPrefHeight()+20);
		buttonPane.setMaxHeight(buttonPane.getPrefHeight());
		buttonPane.setPadding(new Insets(5, 10, 5, 10));
		buttonPane.getChildren().add(button);
		FxUtil.setAnchorZero(button);
		return buttonPane;
    }
	
	public void addRow(String labelText, TextField textField, Runnable rc) {
		textField.textProperty().addListener(event -> {
			rc.run();
		});
		addRow(labelText, textField);

	}
	
	public void addRow(String labelText, Control node) {
    	int row = nRows;
    	nRows++;
    	
    	gridPane.add(new Label(labelText), 0, row, 1, 1);
		gridPane.add(node, 1, row, 1, 1);
		GridPane.setHgrow(node, Priority.ALWAYS);
		node.setMaxWidth(Double.MAX_VALUE);
		
		
		RowConstraints rowContraints = new RowConstraints();
		rowContraints.setPrefHeight(prefRowHeight);
		gridPane.getRowConstraints().add(rowContraints);
    }

	public AnchorPane getRootPane() {
		return rootPane;
	}

	public VBox getvBox() {
		return vBox;
	}

	public GridPane getGridPane() {
		return gridPane;
	}

}
