package org.artorg.tools.phantomData.client.scene.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.controller.AbstractTableViewSelector;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

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

import java.util.UUID;

public class TableViewSelectorSimple<ITEM extends DbPersistent<ITEM,?>> extends AbstractTableViewSelector<ITEM> {
	
	private SplitPane splitPane;
	private int height;
	private final Class<Object> subItemClass;
	
	{
		splitPane = new SplitPane();
		splitPane.setOrientation(Orientation.VERTICAL);
		height = 200;
		splitPane.setPrefHeight(height);
	}
	
	@SuppressWarnings("unchecked")
	public TableViewSelectorSimple() {
		this.subItemClass = (Class<Object>) Reflect.findGenericClasstype(this);
	}
	
	public TableViewSelectorSimple(Class<Object> subItemClass) {
		this.subItemClass = subItemClass;
	}
	

	@Override
	public void setTableView1(TableView<?> tableView1) {
		super.setTableView1(tableView1);
		super.getTableView1().getStyleClass().add("noheader");
	}

	@Override
	public void setTableView2(TableView<?> tableView2) {
		super.setTableView2(tableView2);
		super.getTableView2().getStyleClass().add("noheader");
	}
	
	public void init() {
		getTableView2().getItems().stream().forEach(item2 -> {
			List<Object> doublettes = ((ObservableList<Object>)getTableView1().getItems()).stream()
					.filter(item1 -> ((DbPersistent<?,UUID>)item2).getId().compareTo(((DbPersistent<?,UUID>)item1).getId()) == 0)
					.collect(Collectors.toList());
			getTableView1().getItems().removeAll(doublettes);
		});
		
		if (getTableView1().getItems().size() != 0 && !splitPane.getItems().contains(getTableView1())) {
			splitPane.getItems().add(getTableView1());
			autoResizeColumns(getTableView1());
		}
		if (getTableView2().getItems().size() != 0 && !splitPane.getItems().contains(getTableView2())) {
			splitPane.getItems().add(getTableView2());
			autoResizeColumns(getTableView2());
		}
		
		List<TableColumn<Object, ?>> columns1 = new ArrayList<TableColumn<Object, ?>>();
		columns1.add(createButtonCellColumn("+", this::moveToSelected));
		columns1.add(createValueColumn("Selectable Items"));
		this.getTableView1().getColumns().addAll(columns1);

		
		List<TableColumn<Object, ?>> columns2 = new ArrayList<TableColumn<Object, ?>>();
		columns2.add(createButtonCellColumn("-", this::moveToSelectable));
		columns2.add(createValueColumn("Selected Items"));
		this.getTableView2().getColumns().addAll(columns2);
		
		getTableView1().setRowFactory(new Callback<TableView<Object>,TableRow<Object>>() {
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
		
		getTableView2().setRowFactory(new Callback<TableView<Object>,TableRow<Object>>() {
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
		getTableView1().getItems().remove(item);
		getTableView2().getItems().add(item);
		if (getTableView1().getItems().size() == 0) {
			splitPane.getItems().remove(getTableView1());
			getTableView2().setPrefHeight(this.height);
		}
		if (getTableView2().getItems().size() != 0 && !splitPane.getItems().contains(getTableView2())) {
			splitPane.getItems().add(getTableView2());
			getTableView2().setPrefHeight(this.height/2);
		}
		autoResizeColumns(getTableView1());
		autoResizeColumns(getTableView2());
	}
	
	public void moveToSelectable(Object item) {
		getTableView1().getItems().add(item);
		getTableView2().getItems().remove(item);
		if (getTableView2().getItems().size() == 0) {
			splitPane.getItems().remove(getTableView2());
			getTableView1().setPrefHeight(this.height);
		}
		if (getTableView1().getItems().size() != 0 && !splitPane.getItems().contains(getTableView1())) {
			splitPane.getItems().add(0, getTableView1());
			getTableView1().setPrefHeight(this.height/2);
		}
		autoResizeColumns(getTableView1());
		autoResizeColumns(getTableView2());
		getTableView1().refresh();
		getTableView2().refresh();
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
	public Class<Object> getSubItemClass() {
		return subItemClass;
	}

}
