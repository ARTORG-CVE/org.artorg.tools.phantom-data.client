package org.artorg.tools.phantomData.client.editor;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;

public class PropertyGridPane extends AnchorPane implements IPropertyNode {
	private final ColumnConstraints column1;
	private final ColumnConstraints column2;
	private final GridPane gridPane;
	private final List<IPropertyNode> propertyChildren;

	{
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
		gridPane.sceneProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) Platform.runLater(() -> autosizeColumnWidths());
		});
	}

	private void autosizeColumnWidths() {
		final ColumnConstraints col1 = new ColumnConstraints();
		col1.setHgrow(Priority.ALWAYS);
		final ColumnConstraints col2 = new ColumnConstraints();
		col2.setHgrow(Priority.ALWAYS);
		gridPane.getColumnConstraints().clear();
		gridPane.getColumnConstraints().addAll(col1, col2);

		double textWidth = 0;
		for (int i = 0; i < gridPane.getChildren().size() / 2; i++) {
			Text text = new Text(((Label) gridPane.getChildren().get(2 * i)).getText());
			text.applyCss();
			double width = text.getLayoutBounds().getWidth();
			if (width > textWidth) textWidth = width;
		}
		col1.setMinWidth(textWidth + 5.0);
		col1.setPrefWidth(textWidth + 10.0);
		col1.setMaxWidth(textWidth + 45.0);

		textWidth = 0;
		for (int i = 0; i < gridPane.getChildren().size() / 2; i++) {
			double width = gridPane.getChildren().get(2 * i + 1).getLayoutBounds().getWidth();
			if (width > textWidth) textWidth = width;
		}
		col2.setMinWidth(0);
		col2.setPrefWidth(textWidth + 15.0);
		col2.setMaxWidth(350.0);
	}

	public void addEntry(String title, IPropertyNode propertyNode) {
		addPropertyNode(propertyNode);
		addEntry(new Label(title), propertyNode.getNode());
	}

	public void addEntry(Node rowSpanNode) {
		int row = gridPane.getChildren().size() / 2;
		gridPane.add(rowSpanNode, 0, row, 2, 1);
		formatNodes(rowSpanNode);
		gridPane.getRowConstraints().add(createRowConstraints(rowSpanNode));
	}

	public void addEntry(Node leftNode, Node rightNode) {
		int row = gridPane.getChildren().size() / 2;
		gridPane.add(leftNode, 0, row, 1, 1);
		gridPane.add(rightNode, 1, row, 1, 1);
		formatNodes(leftNode, rightNode);
		gridPane.getRowConstraints().add(createRowConstraints(leftNode, rightNode));
	}

	private static void formatNodes(Node... nodes) {
		for (Node node : nodes) {
			if (node instanceof Control) ((Control) node).setMaxWidth(Double.MAX_VALUE);
			if (node instanceof TextArea) {
				((TextArea) node).setPrefRowCount(10);
				GridPane.setVgrow(node, Priority.ALWAYS);
				((TextArea) node).setPrefHeight(400.0);
			}
		}
	}

	private static RowConstraints createRowConstraints(Node... nodes) {
		boolean hasTextArea = false;
		for (Node node : nodes)
			if (node instanceof TextArea) hasTextArea = true;
		final RowConstraints rowConstraints = new RowConstraints();
		if (hasTextArea) {
			rowConstraints.setMinHeight(40.0);
			rowConstraints.setVgrow(Priority.ALWAYS);
		} else {
			rowConstraints.setMinHeight(25.0);
			rowConstraints.setVgrow(Priority.NEVER);
			rowConstraints.setPrefHeight(25.0);
		}
		return rowConstraints;
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
