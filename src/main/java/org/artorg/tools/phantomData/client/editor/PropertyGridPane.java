package org.artorg.tools.phantomData.client.editor;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;

public class PropertyGridPane extends GridPane {

	public PropertyGridPane(List<PropertyEntry> entries) {
		int nRows = entries.size();
		
		for (int row=0; row<entries.size(); row++) {
			Control rightNode = entries.get(row).getRightNode();
			add(entries.get(row).getLeftNode(), 0, row, 1, 1);
			add(rightNode, 1, row, 1, 1);
			setHgrow(rightNode, Priority.ALWAYS);
			rightNode.setMaxWidth(Double.MAX_VALUE);
		}
		
		final RowConstraints row = new RowConstraints();
		row.setVgrow(Priority.NEVER);
		row.setPrefHeight(30.0);
		row.setMinHeight(30.0);
		List<RowConstraints> rowConstraints = new ArrayList<RowConstraints>();
		for (int i=0; i<nRows; i++)
			rowConstraints.add(row);        
	    getRowConstraints().addAll(rowConstraints);
		
	    final ColumnConstraints col1 = new ColumnConstraints();
		col1.setHgrow(Priority.ALWAYS);
	    final ColumnConstraints col2 = new ColumnConstraints();
	    col2.setMaxWidth(Double.MAX_VALUE);
		col2.setHgrow(Priority.ALWAYS);
		col2.setFillWidth(true);
		col2.setMaxWidth(Double.MAX_VALUE);
		col2.setHalignment(HPos.RIGHT);
		getColumnConstraints().addAll(col1, col2);
	    
		Platform.runLater(() -> {
			double textWidth = 0;
			for (int i=0; i<nRows; i++) {
				Text text = new Text(((Label)getChildren().get(2*i)).getText());
				text.applyCss();
				double width = text.getLayoutBounds().getWidth();
				if (width > textWidth) textWidth = width;
			}
			col1.setMinWidth(textWidth+5.0);
			col1.setPrefWidth(textWidth+10.0);
			col1.setMaxWidth(textWidth+45.0);
			
			textWidth = 0;
			for (int i=0; i<nRows; i++) {
				double width = getChildren().get(2*i+1).getLayoutBounds().getWidth();
				if (width > textWidth) textWidth = width;
			}
			col2.setPrefWidth(textWidth+25.0);
		});
	}

}
