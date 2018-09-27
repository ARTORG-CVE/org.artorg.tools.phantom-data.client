package org.artorg.tools.phantomData.client.scene.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.controller.ISelector;
import org.artorg.tools.phantomData.client.table.FilterableTable;
import org.artorg.tools.phantomData.client.util.TableViewUtils;
import org.artorg.tools.phantomData.server.specification.DbPersistentUUID;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class TableViewSelector<ITEM, SUB_ITEM> implements ISelector<ITEM, SUB_ITEM> {
	private TableViewReadOnly<SUB_ITEM> tableView1;
	private TableViewReadOnly<SUB_ITEM> tableView2;
	private SplitPane splitPane;
	private int height;
	private Class<Object> subItemClass;
	
	{
		tableView1 = new TableViewReadOnly<SUB_ITEM>();
		tableView2 = new TableViewReadOnly<SUB_ITEM>();
		splitPane = new SplitPane();
		splitPane.setOrientation(Orientation.VERTICAL);
		height = 200;
		splitPane.setPrefHeight(height);
		
		tableView1.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableView2.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}
	
	public void setSelectableItems(Set<SUB_ITEM> set) {
		ObservableList<SUB_ITEM> items = FXCollections.observableArrayList();
		items.addAll(set);
		
		tableView1.getFilterTable().setItems(items);
		tableView1.setItems(items);
	}
	
	public ObservableList<SUB_ITEM> getSelectableItems() {
		return tableView1.getItems();
	}
	
	public void setSelectedItems(Set<SUB_ITEM> set) {
		ObservableList<SUB_ITEM> items = FXCollections.observableArrayList();
		items.addAll(set);
		
		tableView2.getFilterTable().setItems(items);
		tableView2.setItems(items);
	}
	
	public ObservableList<SUB_ITEM> getSelectedItems() {
		return tableView2.getItems();
	}
	
	public void setTable1(FilterableTable<SUB_ITEM> table) {
		this.tableView1.setTable(table);
	}
	
	public void setTable2(FilterableTable<SUB_ITEM> table) {
		this.tableView2.setTable(table);
	}
	
//	public void reload(TableViewSpringReadOnly<SUB_ITEM> tableView) {
//		tableView.getFilterTable().readAllData();
//		tableView.setItems(tableView.getFilterTable().getItems());
//	}
//	
	private List<TableColumn<SUB_ITEM,?>> createColumns(TableViewReadOnly<SUB_ITEM> tableView) {
		List<TableColumn<SUB_ITEM,?>> columns = new ArrayList<TableColumn<SUB_ITEM,?>>();
		
		List<String> columnNames = tableView.getFilterTable().getFilteredColumnNames();
		
		int nCols = tableView.getFilterTable().getFilteredNcols();
		for ( int col=0; col<nCols; col++) {
			TableColumn<SUB_ITEM, String> column = new TableColumn<SUB_ITEM, String>(columnNames.get(col));
			column.setSortable(false);
			
			final int localCol = col;
			addFilterMenuButtonToColumn(tableView, col, columnNames.get(col));
			
		    column.setCellValueFactory(cellData -> new SimpleStringProperty(
		    		String.valueOf(tableView.getFilterTable().getFilteredValue(cellData.getValue(), localCol))));

		    columns.add(column);
		}
		
		return columns;
		
	}
	
	private void addFilterMenuButtonToColumn(TableViewReadOnly<SUB_ITEM> tableView, int col, String columnName) {
		FilterMenuButton filterMenuButton = new FilterMenuButton();
		filterMenuButton.setText(columnName);
		filterMenuButton.setTable(tableView.getFilterTable(), col, () -> tableView.getFilterTable().applyFilter()); 
		tableView.getFilterMenuButtons().add(filterMenuButton);
	}
	
	public void init() {
		tableView2.getItems().stream().forEach(item2 -> {
			List<Object> doublettes = tableView1.getItems().stream()
					.filter(item1 -> ((DbPersistentUUID<?>)item2).getId().compareTo(((DbPersistentUUID<?>)item1).getId()) == 0).collect(Collectors.toList());
			tableView1.getItems().removeAll(doublettes);
		});
		
		if (tableView1.getItems().size() != 0 && !splitPane.getItems().contains(tableView1)) {
			splitPane.getItems().add(tableView1);
			TableViewUtils.autoResizeColumns(tableView1);
		}
		if (tableView2.getItems().size() != 0 && !splitPane.getItems().contains(tableView2)) {
			splitPane.getItems().add(tableView2);
			TableViewUtils.autoResizeColumns(tableView2);
		}
		
		initTable(tableView1, "Add");
		initTable(tableView2, "Remove");
		
	}
	
	private void initTable(TableViewReadOnly<SUB_ITEM> tableView, String contextMenuText) {
//		reload(tableView);
		List<TableColumn<SUB_ITEM, ?>> columns = new ArrayList<TableColumn<SUB_ITEM, ?>>();
		tableView.getColumns().removeAll(tableView.getColumns());
		columns.add(TableViewUtils.createButtonCellColumn("+", this::moveToSelected));
	    columns.addAll(createColumns(tableView));
	    tableView.getColumns().addAll(columns);
	    tableView.setItems(tableView.getFilterTable().getItems());
	    TableViewUtils.autoResizeColumns(tableView);
	    Platform.runLater(() -> tableView.showFilterButtons());
		
		tableView.setRowFactory(new Callback<TableView<SUB_ITEM>,TableRow<SUB_ITEM>>() {
			@Override
			public TableRow<SUB_ITEM> call(TableView<SUB_ITEM> tableView) {
				final TableRow<SUB_ITEM> row = new TableRow<SUB_ITEM>();
			
				ContextMenu rowMenu = new ContextMenu();
				MenuItem addMenu = new MenuItem(contextMenuText);
				addMenu.setOnAction(event -> {
					moveToSelected(row.getItem());
				});
				rowMenu.getItems().addAll(addMenu);
				
				row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty()))
					.then(rowMenu).otherwise((ContextMenu)null));
				return row;
			};
		});
		
	}
	
	public void moveToSelected(SUB_ITEM item) {
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
		TableViewUtils.autoResizeColumns(tableView1);
		TableViewUtils.autoResizeColumns(tableView2);
	}
	
	public void moveToSelectable(SUB_ITEM item) {
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
		TableViewUtils.autoResizeColumns(tableView1);
		TableViewUtils.autoResizeColumns(tableView2);
		tableView1.refresh();
		tableView2.refresh();
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
