package org.artorg.tools.phantomData.client.scene.control.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

public class SplittedTableViewSelector<ITEM extends DatabasePersistent> extends SplitPane {
	private TableView<ITEM> tableView1;
	private TableView<ITEM> tableView2;
	private SplitPane splitPane;
	
	
	{
		tableView1 = new TableView<ITEM>();
		tableView2 = new TableView<ITEM>();
		splitPane = this;
		splitPane.setOrientation(Orientation.VERTICAL);
		splitPane.setPrefHeight(200);
		splitPane.getItems().add(tableView1);
		splitPane.getItems().add(tableView2);
		
		removeColumnHeaders();
		tableView1.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableView2.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}
	
	public void removeColumnHeaders() {
		tableView1.getStyleClass().add("noheader");
		tableView2.getStyleClass().add("noheader");
	}
	
	public void setSelectableItems(Set<ITEM> set) {
		ObservableList<ITEM> items = FXCollections.observableArrayList();
		items.addAll(set);
		tableView1.setItems(items);
	}
	
	public void setSelectedItems(Set<ITEM> set) {
		ObservableList<ITEM> items = FXCollections.observableArrayList();
		items.addAll(set);
		tableView2.setItems(items);
	}
	
	public void init() {
		List<TableColumn<ITEM, ?>> columns1 = new ArrayList<TableColumn<ITEM, ?>>();
		columns1.add(createButtonCellColumn("+", item -> {
			tableView1.getItems().remove(item);
			tableView2.getItems().add(item);
		}));
		columns1.add(createValueColumn("Selectable Items"));
		this.tableView1.getColumns().addAll(columns1);

		
		List<TableColumn<ITEM, ?>> columns2 = new ArrayList<TableColumn<ITEM, ?>>();
		columns2.add(createButtonCellColumn("-", item -> {
			tableView1.getItems().add(item);
			tableView2.getItems().remove(item);
		}));
		columns2.add(createValueColumn("Selected Items"));
		this.tableView2.getColumns().addAll(columns2);
		
	}
	
	private TableColumn<ITEM, Void> createButtonCellColumn(String text, Consumer<ITEM> consumer) {
		TableColumn<ITEM, Void> column = new TableColumn<ITEM, Void>();
		column.setCellFactory(new Callback<TableColumn<ITEM, Void>, TableCell<ITEM, Void>>() {
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
							cell.setOnMouseClicked(event -> consumer.accept(getTableView().getItems().get(cell.getIndex())));
							cell.setAlignment(Pos.CENTER);
							cell.setText(text);
						}
					}
				};
			}
		});
		double width = 15.0;
		column.setMinWidth(width);
		column.setPrefWidth(width);
		column.setMaxWidth(width);
		column.setSortable(false);
		
		return column;
	}
	
	private TableColumn<ITEM, String> createValueColumn(String columnName) {
		TableColumn<ITEM, String> column = new TableColumn<ITEM, String>(columnName);
		column.setSortable(false);

		column.setCellFactory(TextFieldTableCell.forTableColumn());
		column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toString()));

		return column;
	}
	
	
	

}
