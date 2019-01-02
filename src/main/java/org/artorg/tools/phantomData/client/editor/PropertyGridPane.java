package org.artorg.tools.phantomData.client.editor;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.logging.Logger;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;

public class PropertyGridPane extends AnchorPane implements IPropertyNode {
//	private final List<AbstractEditor<T, ?>> abstractEditors;
	private final ColumnConstraints column1;
	private final ColumnConstraints column2;
	private final GridPane gridPane;
	private final List<IPropertyNode> propertyChildren;

	{
//		abstractEditors = new ArrayList<>();
		gridPane = new GridPane();
		propertyChildren = new ArrayList<>();
	}

	public PropertyGridPane() {
		column1 = new ColumnConstraints();
		column1.setHgrow(Priority.ALWAYS);
		column2 = new ColumnConstraints();
		column2.setMaxWidth(Double.MAX_VALUE);
		column2.setHgrow(Priority.ALWAYS);
		column2.setFillWidth(true);
		column2.setMaxWidth(Double.MAX_VALUE);
		column2.setHalignment(HPos.RIGHT);
		gridPane.getColumnConstraints().addAll(column1, column2);
	}

	public void setTitled(String title) {

	}

	public void autosizeColumnWidths() {
//		Platform.runLater(() -> {
//			Logger.debug.println("textWidth1");
//			double textWidth = 0;
//			for (int i = 0; i < getChildren().size() / 2; i++) {
//				Text text = new Text(((Label) getChildren().get(2 * i)).getText());
//				text.applyCss();
//				double width = text.getLayoutBounds().getWidth();
//				if (width > textWidth) textWidth = width;
//			}
//			column1.setMinWidth(textWidth + 5.0);
//			column1.setPrefWidth(textWidth + 10.0);
//			column1.setMaxWidth(textWidth + 45.0);
//			Logger.debug.println("textWidth2 = " +textWidth);
//
//			textWidth = 0;
//			for (int i = 0; i < getChildren().size() / 2; i++) {
//				double width = getChildren().get(2 * i + 1).getLayoutBounds().getWidth();
//				if (width > textWidth) textWidth = width;
//			}
//			column2.setPrefWidth(textWidth + 25.0);
//		});
	}

	public void addEntry(String title, IPropertyNode propertyNode) {
		addPropertyNode(propertyNode);
		addEntry(new Label(title), propertyNode.getNode());
	}

	public void addEntry(Node leftNode, Node rightNode) {

		int row = gridPane.getChildren().size() / 2;
		gridPane.add(leftNode, 0, row, 1, 1);
		gridPane.add(rightNode, 1, row, 1, 1);
		GridPane.setHgrow(rightNode, Priority.ALWAYS);
		if (rightNode instanceof Control) ((Control) rightNode).setMaxWidth(Double.MAX_VALUE);

		final RowConstraints rowConstraints = new RowConstraints();
		rowConstraints.setVgrow(Priority.NEVER);
		rowConstraints.setPrefHeight(30.0);
		rowConstraints.setMinHeight(30.0);
		gridPane.getRowConstraints().add(rowConstraints);
	}

	public GridPane getGridPane() {
		return gridPane;
	}

	@Override
	public Node getNode() {
		return gridPane;
	}

	@Override
	public List<IPropertyNode> getChildrenProperties() {
		return propertyChildren;
	}

}
