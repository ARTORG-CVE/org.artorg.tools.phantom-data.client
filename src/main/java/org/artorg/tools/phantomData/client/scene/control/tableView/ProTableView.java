package org.artorg.tools.phantomData.client.scene.control.tableView;

import java.util.function.BiPredicate;

import org.artorg.tools.phantomData.client.scene.layout.AddableToAnchorPane;
import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.TableBase;
import org.artorg.tools.phantomData.client.util.CollectionUtil;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.client.util.TableViewUtils;

import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;

public class ProTableView<ITEM> extends javafx.scene.control.TableView<ITEM>
	implements AddableToAnchorPane {
	private BiPredicate<AbstractColumn<ITEM,?>, TableColumn<ITEM, ?>> addColumnPolicy;
	private BiPredicate<AbstractColumn<ITEM,?>, TableColumn<ITEM, ?>> removeColumnPolicy;
	private final Class<ITEM> itemClass;
	private TableBase<ITEM> table;

	{
		addColumnPolicy =
			(fromColumn, toColumn) -> toColumn.getText().equals(fromColumn.getName());
		removeColumnPolicy =
			(fromColumn, toColumn) -> toColumn.getText().equals(fromColumn.getName());
	}

	@SuppressWarnings("unchecked")
	public ProTableView() {
		itemClass =
			(Class<ITEM>) Reflect.findSubClassParameterType(this, ProTableView.class, 0);
	}

	public ProTableView(Class<ITEM> itemClass) {
		this.itemClass = itemClass;
	}

	public void removeHeaderRow() {
		this.getStyleClass().remove("header");
		this.getStyleClass().add("noheader");
	}

	public void showHeaderRow() {
		this.getStyleClass().remove("noheader");
		this.getStyleClass().add("header");
	}

	public void initTable() {
		refreshColumns();

		super.setItems(table.getItems());
		autoResizeColumns();
		super.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	private void refreshColumns() {
		CollectionUtil.addIfAbsent(table.getColumnCreator().apply(getItems()),
			super.getColumns(), addColumnPolicy, this::createTableColumn);

		CollectionUtil.removeIfAbsent(table.getColumnCreator().apply(getItems()),
			super.getColumns(), removeColumnPolicy);

	}

	private TableColumn<ITEM, ?> createTableColumn(AbstractColumn<ITEM,?> baseColumn,
		int index) {
		TableColumn<ITEM, ?> tableColumn =
			new TableColumn<ITEM, Object>(baseColumn.getName());
		tableColumn.setSortable(false);
		
//		tableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
//			String.valueOf(table.getValue(cellData.getValue(), index))));
		
		
		return tableColumn;
	}

	protected TableColumn<ITEM, ?> createHeaderColumn() {
		TableColumn<ITEM, ?> headerColumn = new TableColumn<ITEM, Object>();
		
		
//		headerColumn.setCellFactory(col -> {
//			TableCell<ITEM, String> cell = new TableCell<ITEM, String>();
//			cell.getStyleClass().add("row-header-cell");
//			return cell;
//		});
		
		
		headerColumn.setSortable(false);
		return headerColumn;
	}

	public void setTable(TableBase<ITEM> table) {
		this.table = table;
		initTable();
	}

	public TableBase<ITEM> getTable() {
		return table;
	}

	public void autoResizeColumns() {
		TableViewUtils.autoResizeColumns(this);
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

	public BiPredicate<AbstractColumn<ITEM,?>, TableColumn<ITEM, ?>> getAddColumnPolicy() {
		return addColumnPolicy;
	}

	public void setAddColumnPolicy(
		BiPredicate<AbstractColumn<ITEM,?>, TableColumn<ITEM, ?>> addColumnPolicy) {
		this.addColumnPolicy = addColumnPolicy;
	}

	public BiPredicate<AbstractColumn<ITEM,?>, TableColumn<ITEM, ?>>
		getRemoveColumnPolicy() {
		return removeColumnPolicy;
	}

	public void setRemoveColumnPolicy(
		BiPredicate<AbstractColumn<ITEM,?>, TableColumn<ITEM, ?>> removeColumnPolicy) {
		this.removeColumnPolicy = removeColumnPolicy;
	}

}
