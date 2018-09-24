package org.artorg.tools.phantomData.client.scene.control.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class SplittedTableViewSelector<ITEM extends DatabasePersistent & Comparable<ITEM>> extends SplitPane {
	private TableView<ITEM> tableView1;
	private TableView<ITEM> tableView2;
	private SplitPane splitPane;
	private int height;
	
	
	{
		tableView1 = new TableView<ITEM>();
		tableView2 = new TableView<ITEM>();
		splitPane = this;
		splitPane.setOrientation(Orientation.VERTICAL);
		height = 200;
		splitPane.setPrefHeight(height);
		
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
	
	public ObservableList<ITEM> getSelectableItems() {
		return tableView1.getItems();
	}
	
	public void setSelectedItems(Set<ITEM> set) {
		ObservableList<ITEM> items = FXCollections.observableArrayList();
		items.addAll(set);
		tableView2.setItems(items);
	}
	
	public ObservableList<ITEM> getSelectedItems() {
		return tableView2.getItems();
	}
	
	public void init() {
		tableView2.getItems().stream().forEach(item2 -> {
			List<ITEM> doublettes = tableView1.getItems().stream()
					.filter(item1 -> item2.getId().compareTo(item1.getId()) == 0).collect(Collectors.toList());
			tableView1.getItems().removeAll(doublettes);
		});
		
		if (tableView1.getItems().size() != 0 && !splitPane.getItems().contains(tableView1)) {
			splitPane.getItems().add(tableView1);
			autoResizeColumns(tableView1);
		}
		if (tableView2.getItems().size() != 0 && !splitPane.getItems().contains(tableView2)) {
			splitPane.getItems().add(tableView2);
			autoResizeColumns(tableView2);
		}
		
		List<TableColumn<ITEM, ?>> columns1 = new ArrayList<TableColumn<ITEM, ?>>();
		columns1.add(createButtonCellColumn("+", this::moveToSelected));
		columns1.add(createValueColumn("Selectable Items"));
		this.tableView1.getColumns().addAll(columns1);

		
		List<TableColumn<ITEM, ?>> columns2 = new ArrayList<TableColumn<ITEM, ?>>();
		columns2.add(createButtonCellColumn("-", this::moveToSelectable));
		columns2.add(createValueColumn("Selected Items"));
		this.tableView2.getColumns().addAll(columns2);
		
		tableView1.setRowFactory(new Callback<TableView<ITEM>,TableRow<ITEM>>() {
			@Override
			public TableRow<ITEM> call(TableView<ITEM> tableView) {
				final TableRow<ITEM> row = new TableRow<ITEM>();
			
				ContextMenu rowMenu = new ContextMenu();
				MenuItem addMenu = new MenuItem("Add");
				addMenu.setOnAction(event -> {
					moveToSelected(row.getItem());
				});
				rowMenu.getItems().addAll(addMenu);
				
				row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty()))
					.then(rowMenu).otherwise((ContextMenu)null));
				return row;
			};
		});
		
		tableView2.setRowFactory(new Callback<TableView<ITEM>,TableRow<ITEM>>() {
			@Override
			public TableRow<ITEM> call(TableView<ITEM> tableView) {
				final TableRow<ITEM> row = new TableRow<ITEM>();
			
				ContextMenu rowMenu = new ContextMenu();
				MenuItem addMenu = new MenuItem("Remove");
				addMenu.setOnAction(event -> {
					moveToSelectable(row.getItem());
				});
				rowMenu.getItems().addAll(addMenu);
				
				row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty()))
					.then(rowMenu).otherwise((ContextMenu)null));
				return row;
			};
		});
		
	}
	
	private void moveToSelected(ITEM item) {
		tableView1.getItems().remove(item);
		tableView2.getItems().add(item);
		if (tableView1.getItems().size() == 0) {
			splitPane.getItems().remove(tableView1);
			tableView2.setPrefHeight(this.height);
		}
		if (tableView2.getItems().size() != 0 && !splitPane.getItems().contains(tableView2)) {
			splitPane.getItems().add(tableView2);
			tableView2.setPrefHeight(this.height/2);
		}
		autoResizeColumns(tableView1);
		autoResizeColumns(tableView2);
	}
	
	private void moveToSelectable(ITEM item) {
		tableView1.getItems().add(item);
		tableView2.getItems().remove(item);
		if (tableView2.getItems().size() == 0) {
			splitPane.getItems().remove(tableView2);
			tableView1.setPrefHeight(this.height);
		}
		if (tableView1.getItems().size() != 0 && !splitPane.getItems().contains(tableView1)) {
			splitPane.getItems().add(0, tableView1);
			tableView1.setPrefHeight(this.height/2);
		}
		autoResizeColumns(tableView1);
		autoResizeColumns(tableView2);
		tableView1.refresh();
		tableView2.refresh();
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
	
	public void autoResizeColumns(TableView<?> tableView) {
		tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		tableView.getColumns().stream().forEach( (column) -> {
	        Text t = new Text( column.getText() );
	        double max = t.getLayoutBounds().getWidth()+45.0;
	        for ( int i = 0; i < super.getItems().size(); i++ ) {
	            if ( column.getCellData( i ) != null ) {
	                t = new Text( column.getCellData( i ).toString() );
	                double calcwidth = t.getLayoutBounds().getWidth()+10;
	                if ( calcwidth > max )
	                    max = calcwidth;
	            }
	        }
	        column.setPrefWidth( max);
	    } );
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}

}
