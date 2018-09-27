package org.artorg.tools.phantomData.client.scene.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.controller.ISelector;
import org.artorg.tools.phantomData.server.specification.DbPersistentUUID;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

public class TableViewSelectorSimple<ITEM extends DbPersistentUUID<ITEM>> implements ISelector<ITEM, Object> {
	private TableView<Object> tableView1;
	private TableView<Object> tableView2;
	private SplitPane splitPane;
	private int height;
	private Class<Object> subItemClass;
	
	{
		tableView1 = new TableView<Object>();
		tableView2 = new TableView<Object>();
		splitPane = new SplitPane();
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
	
	public void setSelectableItems(Set<Object> set) {
		ObservableList<Object> items = FXCollections.observableArrayList();
		items.addAll(set);
		tableView1.setItems(items);
	}
	
	public ObservableList<Object> getSelectableItems() {
		return tableView1.getItems();
	}
	
	public void setSelectedItems(Set<Object> set) {
		ObservableList<Object> items = FXCollections.observableArrayList();
		items.addAll(set);
		tableView2.setItems(items);
	}
	
	public ObservableList<Object> getSelectedItems() {
		return tableView2.getItems();
	}
	
	public void init() {
		tableView2.getItems().stream().forEach(item2 -> {
			List<Object> doublettes = tableView1.getItems().stream()
					.filter(item1 -> ((DbPersistentUUID<?>)item2).getId().compareTo(((DbPersistentUUID<?>)item1).getId()) == 0).collect(Collectors.toList());
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
		
		List<TableColumn<Object, ?>> columns1 = new ArrayList<TableColumn<Object, ?>>();
		columns1.add(createButtonCellColumn("+", this::moveToSelected));
		columns1.add(createValueColumn("Selectable Items"));
		this.tableView1.getColumns().addAll(columns1);

		
		List<TableColumn<Object, ?>> columns2 = new ArrayList<TableColumn<Object, ?>>();
		columns2.add(createButtonCellColumn("-", this::moveToSelectable));
		columns2.add(createValueColumn("Selected Items"));
		this.tableView2.getColumns().addAll(columns2);
		
		tableView1.setRowFactory(new Callback<TableView<Object>,TableRow<Object>>() {
			@Override
			public TableRow<Object> call(TableView<Object> tableView) {
				final TableRow<Object> row = new TableRow<Object>();
			
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
		
		tableView2.setRowFactory(new Callback<TableView<Object>,TableRow<Object>>() {
			@Override
			public TableRow<Object> call(TableView<Object> tableView) {
				final TableRow<Object> row = new TableRow<Object>();
			
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
	
	public void moveToSelected(Object item) {
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
	
	public void moveToSelectable(Object item) {
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
	
	private TableColumn<Object, Void> createButtonCellColumn(String text, Consumer<Object> consumer) {
		TableColumn<Object, Void> column = new TableColumn<Object, Void>();
		column.setCellFactory(new Callback<TableColumn<Object, Void>, TableCell<Object, Void>>() {
			@Override
			public TableCell<Object, Void> call(final TableColumn<Object, Void> param) {
				return new TableCell<Object, Void>() {
					TableCell<Object, Void> cell = this;

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
	
	private TableColumn<Object, String> createValueColumn(String columnName) {
		TableColumn<Object, String> column = new TableColumn<Object, String>(columnName);
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
	        for ( int i = 0; i < splitPane.getItems().size(); i++ ) {
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

	@Override
	public Node getGraphic() {
		return splitPane;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setSubItemClass(Class<?> subItemClass) {
		this.subItemClass = (Class<Object>) subItemClass;
	}

	@Override
	public Class<Object> getSubItemClass() {
		return subItemClass;
	}

}
