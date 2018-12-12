package org.artorg.tools.phantomData.client.scene.control.tableView;

import java.util.function.BiPredicate;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.scene.layout.AddableToPane;
import org.artorg.tools.phantomData.client.table.TableBase;
import org.artorg.tools.phantomData.client.util.CollectionUtil;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.client.util.TableViewUtils;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public class ProTableView<T> extends javafx.scene.control.TableView<T>
	implements AddableToPane {
	private BiPredicate<AbstractColumn<T, ? extends Object>,
		TableColumn<T, ?>> columnAddPolicy;
	private BiPredicate<AbstractColumn<T, ? extends Object>,
		TableColumn<T, ?>> columnRemovePolicy;
	private final Class<T> itemClass;
	private TableBase<T> table;

	{
		columnAddPolicy =
			(fromColumn, toColumn) -> toColumn.getText().equals(fromColumn.getName());
		columnRemovePolicy =
			(fromColumn, toColumn) -> toColumn.getText().equals(fromColumn.getName());
	}

	@SuppressWarnings("unchecked")
	public ProTableView() {
		itemClass =
			(Class<T>) Reflect.findSubClassParameterType(this, ProTableView.class, 0);
	}

	public ProTableView(Class<T> itemClass) {
		this.itemClass = itemClass;
	}

	public void removeHeaderRow() {
		this.getStyleClass().remove("header");
		this.getStyleClass().add("noheader");
	}

	public void showHeaderRow() {
		this.getStyleClass().remove("noheader");
		this.getStyleClass().add("header");
		
		((DbFilterTableView<?>)this).getFilterMenuButtons().forEach(filterMenuButton -> {
		filterMenuButton.refreshImage();
	});
	}

	public void initTable() {
		refreshColumns();

		super.setItems(table.getItems());
//		super.getItems().clear();
//		super.getItems().addAll(table.getItems());
		
		
		autoResizeColumns();
		super.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		super.getSelectionModel().selectFirst();
	}

	public void refreshColumns() {
		CollectionUtil.addIfAbsent(table.getColumnCreator().apply(getItems()),
			super.getColumns(), columnAddPolicy,
			(baseColumn, index) -> createTableColumn(table, index));

		CollectionUtil.removeIfAbsent(table.getColumnCreator().apply(getItems()),
			super.getColumns(), columnRemovePolicy);

	}

	protected TableColumn<T, ?> createTableColumn(TableBase<T> table, int index) {
		TableColumn<T, Object> tableColumn =
			new TableColumn<T, Object>(table.getColumns().get(index).getName());

		tableColumn.setSortable(false);
		tableColumn.setCellFactory(createCellFactory(tableColumn));
		tableColumn.setCellValueFactory(createCellValueFactory(tableColumn,
			cellData -> table.getValue(cellData.getValue(), index)));
		return tableColumn;
	}

	protected <U> Callback<TableColumn<T, U>, TableCell<T, U>>
		createCellFactory(TableColumn<T, U> tableColumn) {
		return column -> new TableCell<T, U>() {
			@Override
			protected void updateItem(U item, boolean empty) {
				if (item == null) {
					setText("");
					setGraphic(null);
					return;
				}
				if (item instanceof Node) {
					Node node = (Node) item;
					setGraphic(node);
					Platform.runLater(() -> {
						tableColumn.setPrefWidth(this.getTableRow().getHeight());
					});
				} else {
					setText(item.toString());
				}
			}
		};
	}

	protected <U> Callback<CellDataFeatures<T, U>, ObservableValue<U>>
		createCellValueFactory(TableColumn<T, U> tableColumn,
			Function<CellDataFeatures<T, U>, U> valueGetter) {
		return cellData -> new ReadOnlyObjectWrapper<U>(valueGetter.apply(cellData));
	}

//	protected TableColumn<ITEM, ?> createHeaderColumn() {
//		TableColumn<ITEM, ?> headerColumn = new TableColumn<ITEM, Object>();
//
////		headerColumn.setCellFactory(col -> {
////			TableCell<ITEM, String> cell = new TableCell<ITEM, String>();
////			cell.getStyleClass().add("row-header-cell");
////			return cell;
////		});
//
//		headerColumn.setSortable(false);
//		return headerColumn;
//	}

	public void setTable(TableBase<T> table) {
		this.table = table;
		initTable();
	}

	public TableBase<T> getTable() {
		return table;
	}

	public void autoResizeColumns() {
		TableViewUtils.autoResizeColumns(this);
	}

	public javafx.scene.control.TableView<T> getGraphic() {
		return this;
	}

	@Override
	public void refresh() {
		getTable().refresh();
		super.refresh();
		refreshColumns();
	}

	public Class<T> getItemClass() {
		return itemClass;
	}

	public BiPredicate<AbstractColumn<T, ? extends Object>, TableColumn<T, ?>>
		getColumnAddPolicy() {
		return columnAddPolicy;
	}

	public void setColumnAddPolicy(BiPredicate<AbstractColumn<T, ? extends Object>,
		TableColumn<T, ?>> addColumnPolicy) {
		this.columnAddPolicy = addColumnPolicy;
	}

	public BiPredicate<AbstractColumn<T, ? extends Object>, TableColumn<T, ?>>
		getColumnRemovePolicy() {
		return columnRemovePolicy;
	}

	public void setColumnRemovePolicy(BiPredicate<AbstractColumn<T, ? extends Object>,
		TableColumn<T, ?>> removeColumnPolicy) {
		this.columnRemovePolicy = removeColumnPolicy;
	}

}
