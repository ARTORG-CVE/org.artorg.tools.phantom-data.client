package org.artorg.tools.phantomData.client.select;

import java.util.ArrayList;
import java.util.List;

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

	public abstract void moveToSelected(ITEM item);

	public abstract void moveToSelectable(ITEM item);

	public abstract Node getGraphic();

	public abstract void init();

	public void setSelectedChildItems(Object item) {
		List<Object> items = new ArrayList<>();

		if (items != null) {
			items.addAll(getSelectedItems());

			Class<?> paramTypeClass = items.getClass();

			Reflect.invokeGenericSetter(item, paramTypeClass, getSubItemClass(), items);
		}
	}

//	public void setSelectableItems(Set<Object> set) {
//		ObservableList<Object> items = FXCollections.observableArrayList();
//		items.addAll(set);
//		getTableView1().setItems(items);
//	}

	public ObservableList<ITEM> getSelectableItems() {
		return ((ProTableView<ITEM>) getTableView1()).getItems();
	}

	public ObservableList<ITEM> getSelectedItems() {
		return ((ProTableView<ITEM>) getTableView2()).getItems();
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
		column.setCellValueFactory(
			cellData -> new SimpleStringProperty(cellData.getValue().toString()));

		return column;
	}

	public TableView<ITEM> getTableView1() {
		return tableView1;
	}
	
	public void setTableView1(TableView<ITEM> tableView1) {
		this.tableView1 = (TableView<ITEM>) tableView1;
	}

	public TableView<ITEM> getTableView2() {
		return tableView2;
	}

	public void setTableView2(TableView<ITEM> tableView2) {
		this.tableView2 = (TableView<ITEM>) tableView2;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
