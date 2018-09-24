package org.artorg.tools.phantomData.client.scene.control.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class TableViewEditFactory<ITEM extends DatabasePersistent> extends TableView<ITEM> {
	String headerColumnText;
	
	{
		headerColumnText = "";
	}
	
	public TableViewEditFactory(String headerColumnText) {
		this.headerColumnText = headerColumnText;
	}
	
	
	private void initTable() {
		super.getColumns().removeAll(super.getColumns());

		// creating columns
		List<TableColumn<ITEM, ?>> columns = new ArrayList<TableColumn<ITEM, ?>>();

		TableColumn<ITEM, Void> headerColumn = new TableColumn<ITEM, Void>();
		headerColumn.setCellFactory(new Callback<TableColumn<ITEM, Void>, TableCell<ITEM, Void>>() {
			@Override
			public TableCell<ITEM, Void> call(final TableColumn<ITEM, Void> param) {
				return new TableCell<ITEM, Void>() {
					TableCell<ITEM, Void> cell = this;

					@Override
					public void updateItem(Void item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							cell.setGraphic(null);
							cell.setText("");
							cell.setOnMouseClicked(null);
						} else {
							cell.setOnMouseClicked(event -> getTableView().getItems().remove(cell.getIndex()));
							cell.setAlignment(Pos.CENTER);
							cell.setText(headerColumnText);
						}
					}
				};
			}
		});

		double width = 15.0;
		headerColumn.setMinWidth(width);
		headerColumn.setPrefWidth(width);
		headerColumn.setMaxWidth(width);

		headerColumn.setSortable(false);
		columns.add(headerColumn);

		TableColumn<ITEM, String> column = new TableColumn<ITEM, String>("Column name");
		column.setSortable(false);

		column.setCellFactory(TextFieldTableCell.forTableColumn());
		column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toString()));
		columns.add(column);

		super.getColumns().addAll(columns);
		autoResizeColumns();

	}

	@Override
	public void resize(double width, double height) {
		super.resize(width, height);
		Pane header = (Pane) lookup("TableHeaderRow");
		header.setMinHeight(0);
		header.setPrefHeight(0);
		header.setMaxHeight(0);
		header.setVisible(false);
	}

	public void autoResizeColumns() {
		super.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		super.getColumns().stream().forEach((column) -> {
			Text t = new Text(column.getText());
			double max = t.getLayoutBounds().getWidth() + 45.0;
			for (int i = 0; i < super.getItems().size(); i++) {
				if (column.getCellData(i) != null) {
					t = new Text(column.getCellData(i).toString());
					double calcwidth = t.getLayoutBounds().getWidth() + 10;
					if (calcwidth > max)
						max = calcwidth;
				}
			}
			column.setPrefWidth(max);
		});
	}

	public void setItems(Set<ITEM> set) {
		ObservableList<ITEM> items = FXCollections.observableArrayList();
		items.addAll(set);
		super.setItems(items);
		initTable();
	}

}
