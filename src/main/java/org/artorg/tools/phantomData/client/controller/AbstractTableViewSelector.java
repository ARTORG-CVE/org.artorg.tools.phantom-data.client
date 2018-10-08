package org.artorg.tools.phantomData.client.controller;

import java.util.Set;
import java.util.function.Consumer;

import org.artorg.tools.phantomData.client.util.Reflect;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

public abstract class AbstractTableViewSelector<ITEM> {
	private TableView<?> tableView1;
	private TableView<?> tableView2;
	private String name;

	public abstract Class<?> getSubItemClass();
	
	public abstract void moveToSelected(Object item);

	public abstract void moveToSelectable(Object item);
	
	public abstract Node getGraphic();
	
	public abstract void init();
	
	public void setSelectedChildItems(ITEM item) {
		Class<?> paramTypeClass = getSelectedItems().getClass();
		Object arg = getSelectedItems();
		Reflect.invokeGenericSetter(item, paramTypeClass, getSubItemClass(), arg);
	}
	
	public void setSelectableItems(Set<Object> set) {
		ObservableList<Object> items = FXCollections.observableArrayList();
		items.addAll(set);
		getTableView1().setItems(items);
	}
	
	public ObservableList<Object> getSelectableItems() {
		return getTableView1().getItems();
	}
	
	public ObservableList<Object> getSelectedItems() {
		return getTableView2().getItems();
	}
	
	public void setSelectedItems(Set<Object> set) {
		ObservableList<Object> items = FXCollections.observableArrayList();
		items.addAll(set);
		getTableView2().setItems(items);
	}
	
	protected TableColumn<Object, Void> createButtonCellColumn(String text, Consumer<Object> consumer) {
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
	
	protected TableColumn<Object, String> createValueColumn(String columnName) {
		TableColumn<Object, String> column = new TableColumn<Object, String>(columnName);
		column.setSortable(false);

		column.setCellFactory(TextFieldTableCell.forTableColumn());
		column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toString()));
		
		return column;
	}
	
	@SuppressWarnings("unchecked")
	public TableView<Object> getTableView1() {
		return (TableView<Object>) tableView1;
	}

	public void setTableView1(TableView<?> tableView1) {
		this.tableView1 = tableView1;
	}

	@SuppressWarnings("unchecked")
	public TableView<Object> getTableView2() {
		return (TableView<Object>) tableView2;
	}

	public void setTableView2(TableView<?> tableView2) {
		this.tableView2 = tableView2;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
