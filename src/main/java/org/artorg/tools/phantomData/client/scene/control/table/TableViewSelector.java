package org.artorg.tools.phantomData.client.scene.control.table;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.Connectors;
import org.artorg.tools.phantomData.client.controller.ISelector;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

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

public class TableViewSelector<ITEM extends DatabasePersistent & Comparable<ITEM>, SUB_ITEM extends DatabasePersistent> implements ISelector<ITEM,SUB_ITEM> {
	private TableView<SUB_ITEM> tableView1;
	private TableView<SUB_ITEM> tableView2;
	private SplitPane splitPane;
	private int height;
	private ITEM item;
	private Class<ITEM> itemClass;
	private Class<SUB_ITEM> subItemClass;
	
	{
		tableView1 = new TableView<SUB_ITEM>();
		tableView2 = new TableView<SUB_ITEM>();
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
	
	public void setSelectableItems(Set<SUB_ITEM> set) {
		ObservableList<SUB_ITEM> items = FXCollections.observableArrayList();
		items.addAll(set);
		tableView1.setItems(items);
	}
	
	public ObservableList<SUB_ITEM> getSelectableItems() {
		return tableView1.getItems();
	}
	
	public void setSelectedItems(Set<SUB_ITEM> set) {
		ObservableList<SUB_ITEM> items = FXCollections.observableArrayList();
		items.addAll(set);
		tableView2.setItems(items);
	}
	
	public ObservableList<SUB_ITEM> getSelectedItems() {
		return tableView2.getItems();
	}
	
	@SuppressWarnings("unchecked")
	public void init() {
		HttpConnectorSpring<SUB_ITEM> connector = Connectors.getConnector(getSubItemClass());
		setSelectableItems(connector.readAllAsSet());
		
		Method selectedMethod = Reflect.getMethodByGenericReturnType(getItem(), getSubItemClass());
		Function<ITEM, Collection<SUB_ITEM>> subItemGetter2; 
		subItemGetter2 = i -> {
			try {
				return (Collection<SUB_ITEM>)(selectedMethod.invoke(i));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		};
		setSelectedItems(subItemGetter2.apply(getItem()).stream().collect(Collectors.toSet()));
		
		tableView2.getItems().stream().forEach(item2 -> {
			List<SUB_ITEM> doublettes = tableView1.getItems().stream()
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
		
		List<TableColumn<SUB_ITEM, ?>> columns1 = new ArrayList<TableColumn<SUB_ITEM, ?>>();
		columns1.add(createButtonCellColumn("+", this::moveToSelected));
		columns1.add(createValueColumn("Selectable Items"));
		this.tableView1.getColumns().addAll(columns1);

		
		List<TableColumn<SUB_ITEM, ?>> columns2 = new ArrayList<TableColumn<SUB_ITEM, ?>>();
		columns2.add(createButtonCellColumn("-", this::moveToSelectable));
		columns2.add(createValueColumn("Selected Items"));
		this.tableView2.getColumns().addAll(columns2);
		
		tableView1.setRowFactory(new Callback<TableView<SUB_ITEM>,TableRow<SUB_ITEM>>() {
			@Override
			public TableRow<SUB_ITEM> call(TableView<SUB_ITEM> tableView) {
				final TableRow<SUB_ITEM> row = new TableRow<SUB_ITEM>();
			
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
		
		tableView2.setRowFactory(new Callback<TableView<SUB_ITEM>,TableRow<SUB_ITEM>>() {
			@Override
			public TableRow<SUB_ITEM> call(TableView<SUB_ITEM> tableView) {
				final TableRow<SUB_ITEM> row = new TableRow<SUB_ITEM>();
			
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
		autoResizeColumns(tableView1);
		autoResizeColumns(tableView2);
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
		autoResizeColumns(tableView1);
		autoResizeColumns(tableView2);
		tableView1.refresh();
		tableView2.refresh();
	}
	
	private TableColumn<SUB_ITEM, Void> createButtonCellColumn(String text, Consumer<SUB_ITEM> consumer) {
		TableColumn<SUB_ITEM, Void> column = new TableColumn<SUB_ITEM, Void>();
		column.setCellFactory(new Callback<TableColumn<SUB_ITEM, Void>, TableCell<SUB_ITEM, Void>>() {
			@Override
			public TableCell<SUB_ITEM, Void> call(final TableColumn<SUB_ITEM, Void> param) {
				return new TableCell<SUB_ITEM, Void>() {
					TableCell<SUB_ITEM, Void> cell = this;

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
	
	private TableColumn<SUB_ITEM, String> createValueColumn(String columnName) {
		TableColumn<SUB_ITEM, String> column = new TableColumn<SUB_ITEM, String>(columnName);
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

	@Override
	public Class<ITEM> getItemClass() {
		return itemClass;
	}

	@Override
	public ITEM getItem() {
		return item;
	}

	@Override
	public void setItem(ITEM item) {
		this.item = item;
	}

	@Override
	public void setItemClass(Class<ITEM> itemClass) {
		this.itemClass = itemClass;
		
	}

	@Override
	public Class<SUB_ITEM> getSubItemClass() {
		return this.subItemClass;
	}

	@Override
	public void setSubItemClass(Class<SUB_ITEM> subItemClass) {
		this.subItemClass = subItemClass;
	}

}
