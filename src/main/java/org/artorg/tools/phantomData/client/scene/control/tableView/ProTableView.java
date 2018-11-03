package org.artorg.tools.phantomData.client.scene.control.tableView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.scene.layout.AddableToAnchorPane;
import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.TableBase;
import org.artorg.tools.phantomData.client.util.CollectionUtil;
import org.artorg.tools.phantomData.server.util.Reflect;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.text.Text;

public class ProTableView<ITEM> extends javafx.scene.control.TableView<ITEM> implements AddableToAnchorPane {
	private final Class<ITEM> itemClass;
	private TableBase<ITEM> table;
//	private ListChangeListener<ITEM> listenerChangedListenerRefresh;

	@SuppressWarnings("unchecked")
	public ProTableView() {
		itemClass = (Class<ITEM>) Reflect.findSubClassParameterType(this, ProTableView.class, 0);
	}

	public ProTableView(Class<ITEM> itemClass) {
		this.itemClass = itemClass;
	}

	public void removeHeaderRow() {
		try {
			this.getStyleClass().add("noheader");
		} catch (Exception e) {
			this.setStyle("-fx-max-height: 0; -fx-pref-height: 0; -fx-min-height: 0;");
		}
	}

//	public ListChangeListener<ITEM> getListenerChangedListenerRefresh() {
//		return listenerChangedListenerRefresh;
//	}
//
//	public void setListenerChangedListenerRefresh(ListChangeListener<ITEM> listenerChangedListenerRefresh) {
//		this.listenerChangedListenerRefresh = listenerChangedListenerRefresh;
//	}

	{
//		listenerChangedListenerRefresh = new ListChangeListener<ITEM>() {
//			@Override
//			public void onChanged(Change<? extends ITEM> c) {
//				refresh();
//			}
//		};
	}

	public void initTable() {
		refreshColumns();

		super.setItems(table.getItems());
		autoResizeColumns();
		super.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	private void refreshColumns() {
		CollectionUtil.syncLists(super.getColumns(), table.getColumnCreator().apply(getItems()),
				(toColumn, fromColumn) -> toColumn.getText().equals(fromColumn.getName()), this::createTableColumn);
	}

	private TableColumn<ITEM, String> createTableColumn(AbstractColumn<ITEM> baseColumn, int index) {
		TableColumn<ITEM, String> tableColumn = new TableColumn<ITEM, String>(baseColumn.getName());
		tableColumn.setSortable(false);
		tableColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(String.valueOf(table.getValue(cellData.getValue(), index))));
		return tableColumn;
	}

	protected TableColumn<ITEM, String> createHeaderColumn() {
		TableColumn<ITEM, String> headerColumn = new TableColumn<ITEM, String>();
		headerColumn.setCellFactory(col -> {
			TableCell<ITEM, String> cell = new TableCell<ITEM, String>();
			cell.getStyleClass().add("row-header-cell");
			return cell;
		});
		headerColumn.setSortable(false);
		return headerColumn;
	}

	public void setTable(TableBase<ITEM> table) {
		this.table = table;
		initTable();
//		table.getItems().addListener(listenerChangedListenerRefresh);
	}

	public TableBase<ITEM> getTable() {
		return table;
	}

	public void autoResizeColumns() {
		super.setColumnResizePolicy(javafx.scene.control.TableView.UNCONSTRAINED_RESIZE_POLICY);
		super.getColumns().stream().forEach((column) -> {
			Text t = new Text(column.getText());
			double max = t.getLayoutBounds().getWidth() + 45.0;
			for (int i = 0; i < super.getItems().size(); i++) {
				if (column.getCellData(i) != null) {
					t = new Text(column.getCellData(i).toString());
					double calcwidth = t.getLayoutBounds().getWidth() + 10;
					if (calcwidth > max)
						max = calcwidth;
				}
			}
			column.setPrefWidth(max);
		});
	}

	public javafx.scene.control.TableView<ITEM> getGraphic() {
		return this;
	}

	@Override
	public void refresh() {
		getTable().refresh();
		super.refresh();
		refreshColumns();
	}

	public Class<ITEM> getItemClass() {
		return itemClass;
	}

}
