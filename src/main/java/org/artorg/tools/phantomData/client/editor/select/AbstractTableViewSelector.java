package org.artorg.tools.phantomData.client.editor.select;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.util.Reflect;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;

public abstract class AbstractTableViewSelector<ITEM> {
	private ProTableView<ITEM> tableView1;
	private ProTableView<ITEM> tableView2;
	private String name;

	public abstract Class<?> getSubItemClass();

	public abstract void moveToSelected(ITEM item);

	public abstract void moveToSelectable(ITEM item);

	public abstract Node getGraphic();

	public void setSelectedChildItems(Object item) {
		List<Object> items = new ArrayList<>();

		if (items != null) {
			items.addAll(getSelectedItems());

			Class<?> paramTypeClass = items.getClass();

			Reflect.invokeGenericSetter(item, paramTypeClass, getSubItemClass(), items);
		}
	}

	public ObservableList<ITEM> getSelectableItems() {
		return getTableView1().getTable().getItems();
	}

	public ObservableList<ITEM> getSelectedItems() {
		return getTableView2().getTable().getItems();
	}

	protected TableColumn<Object, String> createValueColumn(String columnName) {
		TableColumn<Object, String> column = new TableColumn<Object, String>(columnName);
		column.setSortable(false);

		column.setCellFactory(TextFieldTableCell.forTableColumn());
		column.setCellValueFactory(
			cellData -> new SimpleStringProperty(cellData.getValue().toString()));

		return column;
	}

	protected ProTableView<ITEM> getTableView1() {
		return tableView1;
	}
	
	public void setTableView1(ProTableView<ITEM> tableView1) {
		this.tableView1 = tableView1;
	}

	protected ProTableView<ITEM> getTableView2() {
		return tableView2;
	}

	public void setTableView2(ProTableView<ITEM> tableView2) {
		this.tableView2 = tableView2;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
