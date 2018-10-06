package org.artorg.tools.phantomData.client.scene.control;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.controller.AbstractTableViewSelector;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class TableViewSelector<ITEM extends DbPersistent<ITEM,?>> extends AbstractTableViewSelector<ITEM> {
	private TableView<Object> tableView1;
	private TableView<Object> tableView2;
	private SplitPane splitPane;
	private int height;
	private final Class<?> subItemClass;
	
	{
		splitPane = new SplitPane();
		splitPane.setOrientation(Orientation.VERTICAL);
		height = 200;
		splitPane.setPrefHeight(height);
	}
	
	public TableViewSelector() {
		this.subItemClass = Reflect.findGenericClasstype(this);
	}
	
	public TableViewSelector(Class<?> subItemClass) {
		this.subItemClass = subItemClass;
	}
	
	@SuppressWarnings("unchecked")
	public void init() {
		tableView2.getItems().stream().forEach(item2 -> {
			List<?> doublettes = tableView1.getItems().stream()
					.filter(item1 -> ((DbPersistent<?,UUID>)item2).getId().compareTo(((DbPersistent<?,UUID>)item1).getId()) == 0).collect(Collectors.toList());
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
	public Class<?> getSubItemClass() {
		return subItemClass;
	}

}