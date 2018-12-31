package org.artorg.tools.phantomData.client.editor;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;

public class PropertyGridPane<T> extends PropertyNode<T> {
//	private final List<AbstractEditor<T, ?>> abstractEditors;
	private final ColumnConstraints column1;
	private final ColumnConstraints column2;
	private final GridPane gridPane;

	{
//		abstractEditors = new ArrayList<>();
		gridPane = new GridPane();
	}

	public PropertyGridPane(Class<T> itemClass) {
		super(itemClass);
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

	public <U> PropertyGridPane<U> map(Class<U> cls) {
		PropertyGridPane<U> result = new PropertyGridPane<U>(cls);
		
		return result;
	}

	public void addEntry(String title, Node rightNode) {
		addEntry(new Label(title), rightNode);
	}
	
	@SuppressWarnings("unchecked")
	public void addEntry(Node leftNode, Node rightNode) {
		if (leftNode instanceof AbstractEditor)
			this.add((AbstractEditor<T, ?>) rightNode);
		if (rightNode instanceof AbstractEditor)
			this.add((AbstractEditor<T, ?>) rightNode);
		int row = gridPane.getChildren().size() / 2;
		if (rightNode instanceof AbstractEditor)
			rightNode = ((AbstractEditor<?,?>)rightNode).getControlNode();
		gridPane.add(leftNode, 0, row, 1, 1);
		gridPane.add(rightNode, 1, row, 1, 1);
		GridPane.setHgrow(rightNode, Priority.ALWAYS);
		if (rightNode instanceof Control) ((Control) rightNode).setMaxWidth(Double.MAX_VALUE);

		final RowConstraints rowConstraints = new RowConstraints();
		rowConstraints.setVgrow(Priority.NEVER);
		rowConstraints.setPrefHeight(30.0);
		rowConstraints.setMinHeight(30.0);
		gridPane.getRowConstraints().add(rowConstraints);

		Platform.runLater(() -> {
			double textWidth = 0;
			Text text = new Text(((Label) gridPane.getChildren().get(2 * row)).getText());
			text.applyCss();
			double width = text.getLayoutBounds().getWidth();
			if (width > textWidth) textWidth = width;
			column1.setMinWidth(textWidth + 5.0);
			column1.setPrefWidth(textWidth + 10.0);
			column1.setMaxWidth(textWidth + 45.0);

			textWidth = 0;
			width = gridPane.getChildren().get(2 * row + 1).getLayoutBounds().getWidth();
			if (width > textWidth) textWidth = width;
			column2.setPrefWidth(textWidth + 25.0);
		});
	}

	public GridPane getGridPane() {
		return gridPane;
	}

	@Override
	public Node getControlNode() {
		return gridPane;
	}

}
