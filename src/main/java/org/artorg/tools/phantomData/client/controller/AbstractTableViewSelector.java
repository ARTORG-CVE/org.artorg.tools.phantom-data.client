package org.artorg.tools.phantomData.client.controller;

import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.util.Reflect;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;

public abstract class AbstractTableViewSelector<ITEM> {
	private TableView<ITEM> tableView1;
	private TableView<ITEM> tableView2;
	private String name;

	public abstract Class<?> getSubItemClass();

	public abstract void moveToSelected(Object item);

	public abstract void moveToSelectable(Object item);

	public abstract Node getGraphic();

	public abstract void init();

	public void setSelectedChildItems(ITEM item) {
		Class<?> paramTypeClass = getSelectedItems().getClass();
		Object arg = getSelectedItems();
		if (arg != null)
			Reflect.invokeGenericSetter(item, paramTypeClass, getSubItemClass(), arg);
	}

//	public void setSelectableItems(Set<Object> set) {
//		ObservableList<Object> items = FXCollections.observableArrayList();
//		items.addAll(set);
//		getTableView1().setItems(items);
//	}
	
	public ObservableList<Object> getSelectableItems() {
		return ((ProTableView<Object>)getTableView1()).getTable().getItems();
	}

	public ObservableList<Object> getSelectedItems() {
		return ((ProTableView<Object>)getTableView2()).getTable().getItems();
	}

//	public void setSelectedItems(Set<Object> set) {
//		ObservableList<Object> items = FXCollections.observableArrayList();
//		items.addAll(set);
//		getTableView2().setItems(items);
//	}

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

	@SuppressWarnings("unchecked")
	public void setTableView1(TableView<?> tableView1) {
		this.tableView1 = (TableView<ITEM>) tableView1;
	}

	@SuppressWarnings("unchecked")
	public TableView<Object> getTableView2() {
		return (TableView<Object>) tableView2;
	}

	@SuppressWarnings("unchecked")
	public void setTableView2(TableView<?> tableView2) {
		this.tableView2 = (TableView<ITEM>) tableView2;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
