package org.artorg.tools.phantomData.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.client.util.Reflect;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class TableViewSelector<T> extends SplitPane {
	private SplitPane splitPane;
	private int height;
	private final Class<?> subItemClass;
	private final ProTableView<T> tableView1;
	private final ProTableView<T> tableView2;
	private String name;

	{
		splitPane = this;
		splitPane.setOrientation(Orientation.VERTICAL);
		height = 2000;
		splitPane.setPrefHeight(height);

	}

	public TableViewSelector(Class<T> subItemClass, ProTableView<T> tableView1, ProTableView<T> tableView2) {
		this.subItemClass = subItemClass;
		this.tableView1 = tableView1;
		this.tableView2 = tableView2;
		setName(subItemClass.getSimpleName());
		
		if (!splitPane.getItems().contains(getTableView1())) {
			splitPane.getItems().add(getTableView1());
//			autoResizeColumns(getTableView1());
		}
		if (!splitPane.getItems().contains(getTableView2())) {
			splitPane.getItems().add(getTableView2());
//			autoResizeColumns(getTableView2());
		}

		TableColumn<T, Void> addButtonCellColumn =
				FxUtil.createButtonCellColumn("+", this::moveToSelected);
		TableColumn<T, Void> removeButtonCellColumn =
				FxUtil.createButtonCellColumn("-", this::moveToSelectable);

		this.getTableView1().getColumns().add(0, addButtonCellColumn);
		this.getTableView2().getColumns().add(0, removeButtonCellColumn);

		getTableView1().getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		getTableView2().getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		getTableView1().setRowFactory(new Callback<TableView<T>, TableRow<T>>() {
			@Override
			public TableRow<T> call(TableView<T> tableView) {
				final TableRow<T> row = new TableRow<>();

				ContextMenu contextMenu = new ContextMenu();
				MenuItem menuItem;
				menuItem = new MenuItem("Add");
				menuItem.setOnAction(event -> moveToSelected());
				contextMenu.getItems().add(menuItem);
				menuItem = new MenuItem("Refresh");
				menuItem.setOnAction(event -> tableView.refresh());
				contextMenu.getItems().add(menuItem);

				row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty()))
						.then(contextMenu).otherwise((ContextMenu) null));
				return row;
			};
		});

		getTableView2().setRowFactory(new Callback<TableView<T>, TableRow<T>>() {
			@Override
			public TableRow<T> call(TableView<T> tableView) {
				final TableRow<T> row = new TableRow<>();

				ContextMenu contextMenu = new ContextMenu();
				MenuItem menuItem;
				menuItem = new MenuItem("Remove");
				menuItem.setOnAction(event -> moveToSelectable());
				contextMenu.getItems().addAll(menuItem);
				menuItem = new MenuItem("Refresh");
				menuItem.setOnAction(event -> tableView.refresh());
				contextMenu.getItems().add(menuItem);

				row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty()))
						.then(contextMenu).otherwise((ContextMenu) null));
				return row;
			};
		});
	}
	
	public void setSelectedChildItems(Object item) {
		List<Object> items = new ArrayList<>();

		if (items != null) {
			items.addAll(getSelectedItems());

			Class<?> paramTypeClass = items.getClass();

			Reflect.invokeGenericSetter(item, paramTypeClass, getSubItemClass(), items);
		}
	}
	
	public void setSelectedItems(Collection<T> items) {
		getSelectedItems().clear();
		getSelectedItems().addAll(items);
		refreshVisibility();
	}
	
	public void setSelectableItems(Collection<T> items) {
		getSelectableItems().clear();
		getSelectableItems().addAll(items);
		refreshVisibility();
	}

	private void moveToSelected() {
		List<T> items = new ArrayList<>();
		items.addAll(getTableView1().getSelectionModel().getSelectedItems());
		moveToSelected(items);
	}
	
	private void moveToSelectable() {
		List<T> items = new ArrayList<>();
		items.addAll(getTableView2().getSelectionModel().getSelectedItems());
		moveToSelectable(items);
	}

	public void moveToSelected(T item) {
		List<T> list = new ArrayList<>();
		list.add(item);
		moveToSelected(list);
	}
	
	public void moveToSelectable(T item) {
		List<T> list = new ArrayList<>();
		list.add(item);
		moveToSelectable(list);
	}

	public void moveToSelected(Collection<T> items) {
		getTableView1().getTable().getItems().removeAll(items);
		getTableView2().getTable().getItems().addAll(items);
		getTableView2().getTable().applyFilter();
		refreshVisibility();
		autoResizeColumns(getTableView1());
		autoResizeColumns(getTableView2());
	}

	public void moveToSelectable(Collection<T> items) {
		getTableView2().getTable().getItems().removeAll(items);
		getTableView1().getTable().getItems().addAll(items);
		getTableView1().getTable().applyFilter();
		refreshVisibility();
		autoResizeColumns(getTableView1());
		autoResizeColumns(getTableView2());
	}
	
	private void refreshVisibility() {
		if (getTableView1().getItems().isEmpty()) {
			splitPane.getItems().remove(getTableView1());
		} else {
			if (!splitPane.getItems().contains(getTableView1()))
			splitPane.getItems().add(0, getTableView1());
		}
		if (getTableView2().getItems().isEmpty()) {
			splitPane.getItems().remove(getTableView2());
		} else {
			if (!splitPane.getItems().contains(getTableView2()))
			splitPane.getItems().add(getTableView2());
		}
			
		int n = 0;
		if (!getTableView1().getItems().isEmpty()) n++;
		if (!getTableView2().getItems().isEmpty()) n++;
		if (n == 1) splitPane.setDividerPositions(1.0f);
		if (n == 2) splitPane.setDividerPositions(0.5f);
	}

	public void autoResizeColumns(TableView<?> tableView) {
		tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		tableView.getColumns().stream().forEach((column) -> {
			Text t = new Text(column.getText());
			double max = t.getLayoutBounds().getWidth() + 45.0;
			for (int i = 0; i < splitPane.getItems().size(); i++) {
				if (column.getCellData(i) != null) {
					t = new Text(column.getCellData(i).toString());
					double calcwidth = t.getLayoutBounds().getWidth() + 10;
					if (calcwidth > max) max = calcwidth;
				}
			}
			column.setPrefWidth(max);
		});
	}
	
	public ObservableList<T> getSelectableItems() {
		return getTableView1().getTable().getItems();
	}

	public ObservableList<T> getSelectedItems() {
		return getTableView2().getTable().getItems();
	}
	
	public Class<?> getSubItemClass() {
		return subItemClass;
	}
	
	protected ProTableView<T> getTableView1() {
		return tableView1;
	}

	protected ProTableView<T> getTableView2() {
		return tableView2;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}