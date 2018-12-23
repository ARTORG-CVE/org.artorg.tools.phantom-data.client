package org.artorg.tools.phantomData.client.select;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.client.util.Reflect;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class TableViewSelector<ITEM> extends AbstractTableViewSelector<ITEM> {
	private SplitPane splitPane;
	private int height;
	private final Class<?> subItemClass;

	{
		splitPane = new SplitPane();
		splitPane.setOrientation(Orientation.VERTICAL);
		height = 2000;
		splitPane.setPrefHeight(height);

	}

	public TableViewSelector() {
		this.subItemClass = Reflect.findGenericClasstype(this);
		super.setName(subItemClass.getSimpleName());
	}

	public TableViewSelector(Class<?> subItemClass) {
		this.subItemClass = subItemClass;
		super.setName(subItemClass.getSimpleName());
	}

	public void init() {
//		getTableView2().getItems().stream().forEach(item2 -> {
//			List<?> doublettes = (List<?>) getTableView1().getItems().stream()
//				.filter(item1 -> ((DbPersistent<?, ?>) item2).getId()
//					.equals(((DbPersistent<?, ?>) item1).getId()))
//				.collect(Collectors.toList());
//			getTableView1().getItems().removeAll(doublettes);
//		});

		if (getTableView1().getItems().size() != 0
				&& !splitPane.getItems().contains(getTableView1())) {
			splitPane.getItems().add(getTableView1());
			autoResizeColumns(getTableView1());
		}
		if (getTableView2().getItems().size() != 0
				&& !splitPane.getItems().contains(getTableView2())) {
			splitPane.getItems().add(getTableView2());
			autoResizeColumns(getTableView2());
		}

		TableColumn<ITEM, Void> addButtonCellColumn =
				FxUtil.createButtonCellColumn("+", this::moveToSelected);
		TableColumn<ITEM, Void> removeButtonCellColumn =
				FxUtil.createButtonCellColumn("-", this::moveToSelectable);

		if (this.getTableView1() instanceof ProTableView) {
			ProTableView<ITEM> proTableView = (ProTableView<ITEM>) this.getTableView1();
			proTableView.setColumnRemovePolicy((fromColumn, toColumn) -> {
				if (toColumn == addButtonCellColumn) return true;
				return toColumn.getText().equals(fromColumn.getName());
			});
		}

		if (this.getTableView2() instanceof ProTableView) {
			ProTableView<ITEM> proTableView = (ProTableView<ITEM>) this.getTableView2();
			proTableView.setColumnRemovePolicy((fromColumn, toColumn) -> {
				if (toColumn == removeButtonCellColumn) return true;
				return toColumn.getText().equals(fromColumn.getName());
			});
		}

		this.getTableView1().getColumns().add(0, addButtonCellColumn);
		this.getTableView2().getColumns().add(0, removeButtonCellColumn);
		((ProTableView<ITEM>) this.getTableView2()).removeHeaderRow();
		((ProTableView<ITEM>) this.getTableView2()).showHeaderRow();

		getTableView1().getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		getTableView2().getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		getTableView1().setRowFactory(new Callback<TableView<ITEM>, TableRow<ITEM>>() {
			@Override
			public TableRow<ITEM> call(TableView<ITEM> tableView) {
				final TableRow<ITEM> row = new TableRow<>();

				ContextMenu contextMenu = new ContextMenu();
				MenuItem menuItem;
				menuItem = new MenuItem("Add");
				menuItem.setOnAction(event -> moveToSelected());
				contextMenu.getItems().add(menuItem);
				menuItem = new MenuItem("Refresh");
				menuItem.setOnAction(event -> ((ProTableView<?>) tableView).refresh());
				contextMenu.getItems().add(menuItem);

				row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty()))
						.then(contextMenu).otherwise((ContextMenu) null));
				return row;
			};
		});

		getTableView2().setRowFactory(new Callback<TableView<ITEM>, TableRow<ITEM>>() {
			@Override
			public TableRow<ITEM> call(TableView<ITEM> tableView) {
				final TableRow<ITEM> row = new TableRow<>();

				ContextMenu contextMenu = new ContextMenu();
				MenuItem menuItem;
				menuItem = new MenuItem("Remove");
				menuItem.setOnAction(event -> moveToSelectable());
				contextMenu.getItems().addAll(menuItem);
				menuItem = new MenuItem("Refresh");
				menuItem.setOnAction(event -> ((ProTableView<?>) tableView).refresh());
				contextMenu.getItems().add(menuItem);

				row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty()))
						.then(contextMenu).otherwise((ContextMenu) null));
				return row;
			};
		});

	}

	private void moveToSelected() {
		List<ITEM> items = new ArrayList<>();
		items.addAll(getTableView1().getSelectionModel().getSelectedItems());
		moveToSelected(items);
	}

	private void moveToSelectable() {
		List<ITEM> items = new ArrayList<>();
		items.addAll(getTableView2().getSelectionModel().getSelectedItems());
		moveToSelectable(items);
	}

	public void moveToSelected(ITEM item) {
		List<ITEM> list = new ArrayList<>();
		list.add(item);
		moveToSelected(list);
	}

	public void moveToSelected(Collection<ITEM> items) {
//		getSelectableItems().removeAll(items);

		((ProTableView<ITEM>) getTableView2()).getTable().getItems()
				.addListener(new ListChangeListener<ITEM>() {

					@Override
					public void onChanged(Change<? extends ITEM> c) {
						while (c.next()) {
							System.out.println("items changed: addedSize " + c.getAddedSize()
									+ ", new size " + c.getList().size());
						}
					}

				});

		((ProTableView<ITEM>) getTableView2()).getTable().getFilteredItems()
				.addListener(new ListChangeListener<ITEM>() {

					@Override
					public void onChanged(Change<? extends ITEM> c) {
						while (c.next()) {
							System.out.println("filtered items changed: addedSize "
									+ c.getAddedSize() + ", new size " + c.getList().size());
						}

					}

				});

		((ProTableView) getTableView1()).getTable().getItems().removeAll(items);
		((ProTableView) getTableView1()).getTable().getFilteredItems().removeAll(items);
		((ProTableView) getTableView1()).setItems(FXCollections.observableArrayList());
		((ProTableView) getTableView1())
				.setItems(((ProTableView) getTableView1()).getTable().getFilteredItems());
//		flyingHero(((ProTableView) getTableView1()));
//		((ProTableView)getTableView1()).applyFilter();
//		getSelectedItems().addAll(items);
		((ProTableView) getTableView2()).getTable().getItems().addAll(items);
		((ProTableView) getTableView2()).getTable().getFilteredItems().addAll(items);
		((ProTableView) getTableView2()).setItems(FXCollections.observableArrayList());
		((ProTableView) getTableView2())
				.setItems(((ProTableView) getTableView2()).getTable().getFilteredItems());
		flyingHero(((ProTableView) getTableView2()));
//		((ProTableView)getTableView2()).applyFilter();
		if (getTableView1().getItems().size() == 0) {
			splitPane.getItems().remove(getTableView1());
			if (!splitPane.getItems().contains(getTableView2()))
				splitPane.getItems().add(0, getTableView2());
		} else {
			if (!splitPane.getItems().contains(getTableView2()))
				splitPane.getItems().add(1, getTableView2());
			splitPane.setDividerPositions(0.5f);
		}
		((ProTableView<?>) getTableView2()).showHeaderRow();
		autoResizeColumns(getTableView1());
		autoResizeColumns(getTableView2());
	}

	public void moveToSelectable(ITEM item) {
		List<ITEM> list = new ArrayList<>();
		list.add(item);
		moveToSelectable(list);
	}

	public void moveToSelectable(Collection<ITEM> items) {
//		getSelectedItems().removeAll(items);
		((ProTableView) getTableView2()).getTable().getItems().removeAll(items);
		((ProTableView) getTableView2()).getTable().getFilteredItems().removeAll(items);
		((ProTableView) getTableView2()).setItems(FXCollections.observableArrayList());
		((ProTableView) getTableView2())
				.setItems(((ProTableView) getTableView2()).getTable().getFilteredItems());
		flyingHero(((ProTableView) getTableView2()));
//		getSelectableItems().addAll(items);
		((ProTableView) getTableView1()).getTable().getItems().addAll(items);
		((ProTableView) getTableView1()).getTable().getFilteredItems().addAll(items);
		((ProTableView) getTableView1()).setItems(FXCollections.observableArrayList());
		((ProTableView) getTableView1())
				.setItems(((ProTableView) getTableView1()).getTable().getFilteredItems());
//		flyingHero(((ProTableView) getTableView1()));
		if (getTableView2().getItems().size() == 0) {
			splitPane.getItems().remove(getTableView2());
		} else {
			if (!splitPane.getItems().contains(getTableView1())) {
				splitPane.getItems().add(0, getTableView1());
				((ProTableView<?>) getTableView1()).showHeaderRow();
			}
			((ProTableView<?>) getTableView1()).showHeaderRow();
			splitPane.setDividerPositions(0.5f);
			if (getTableView2().getItems().size() < 10) if (getTableView2() instanceof ProTableView)
				((ProTableView<?>) getTableView2()).removeHeaderRow();
		}
		autoResizeColumns(getTableView1());
		autoResizeColumns(getTableView2());
	}

	private <U> void flyingHero(ProTableView<U> tableView) {
		tableView.getTable().setFilterActivated(false);
//		tableView.getTable().getColumns().stream().forEach(column -> {
//			column.setItems(tableView.getItems());
////			column.getItems().clear();
////			column.getItems().addAll(getItems());
//
//		});

//		tableView.updateColumns();
//		tableView.getTable().refresh();
//		tableView.autoResizeColumns();
//
//		tableView.getTable().getColumns().stream().forEach(column -> {
//			column.setItems(tableView.getItems());
////		column.getItems().clear();
////		column.getItems().addAll(getItems());
//
//		});

//		tableView.updateColumns();

//		tableView.applyFilter();
//		tableView.getSelectionModel().selectFirst();
//		tableView.refresh();
//		if (tableView.isFilterable()) {
//			tableView.getFilterMenuButtons().forEach(filterMenuButton -> {
//				filterMenuButton.refreshImage();
//			});
//		}
//		tableView.showFilterButtons();
//		tableView.getFilterMenuButtons().stream().forEach(column -> {
////			column.updateNodes();
//			column.applyFilter();
//			tableView.showFilterButtons();
//		});
//		tableView.showFilterButtons();
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

	@Override
	public Node getGraphic() {
		return splitPane;
	}

	@Override
	public Class<?> getSubItemClass() {
		return subItemClass;
	}

}