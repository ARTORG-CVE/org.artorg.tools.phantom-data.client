package org.artorg.tools.phantomData.client.scene.control.tableView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.AbstractFilterColumn;
import org.artorg.tools.phantomData.client.scene.control.FilterMenuButton;
import org.artorg.tools.phantomData.client.table.DbTable;
import org.artorg.tools.phantomData.client.table.TableBase;
import org.artorg.tools.phantomData.client.util.CollectionUtil;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;

public class DbFilterTableView<ITEM>
	extends DbTableView<ITEM> {
	protected List<FilterMenuButton<ITEM, ?>> filterMenuButtons;

	{
		super.setEditable(true);
		filterMenuButtons = new ArrayList<FilterMenuButton<ITEM, ?>>();
	}

	
	
	public DbFilterTableView(Class<ITEM> itemClass) {
		this(itemClass, Main.getUIEntity(itemClass).createDbTableBase());
		super.setItems(getTable().getItems());
		super.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		reload();
		refreshColumns();
		autoResizeColumns();
		getTable().applyFilter();
		Platform.runLater(() -> showFilterButtons());
	}
	
	
	
	protected DbFilterTableView(Class<ITEM> itemClass, DbTable<ITEM> table) {
		super(itemClass, table);
	}

	@Override
	public void showHeaderRow() {
		// TODO Auto-generated method stub
		super.showHeaderRow();
		showFilterButtons();
	}

	public void showFilterButtons() {
		for (Node n : super.lookupAll(".column-header > .label"))
			if (n instanceof Label) {
				Label label = (Label) n;

				String columnName = label.getText();
				Optional<FilterMenuButton<ITEM, ?>> filterMenuButton = filterMenuButtons
					.stream().filter(f -> f.getText().equals(columnName)).findFirst();
				if (filterMenuButton.isPresent()) {
					filterMenuButton.get().prefWidthProperty()
						.bind(label.widthProperty());
					filterMenuButton.get().getStyleClass().add("filter-menu-button");
					label.setGraphic(filterMenuButton.get());
					label.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
				}
			}
	}
	
//	@Override
//	public void initTable() {
//		if (!(getTable() instanceof DbTable)) {
//			super.initTable();
//			return;
//		}
//
//		
//	}
//	
	@Override
	public void refreshColumns() {
		System.out.println("DbFilterTableView - refreshColumns");
//		if (!(getTable() instanceof DbTable)) {
//			super.initTable();
//			return;
//		}
		DbTable<ITEM> table = (DbTable<ITEM>) getTable();
		CollectionUtil.addIfAbsent(
			getTable().getColumnCreator().apply(table.getFilteredItems()),
			super.getColumns(), getColumnAddPolicy(),
			(baseColumn, index) -> createTableColumn(table, index));
		CollectionUtil.removeIfAbsent(
			getTable().getColumnCreator().apply(table.getFilteredItems()),
			super.getColumns(), getColumnRemovePolicy());
	}

	@Override
	protected TableColumn<ITEM, ?> createTableColumn(TableBase<ITEM> table, int index) {
		if (!(table instanceof DbTable))
			return super.createTableColumn(table, index);
		DbTable<ITEM> filterTable = (DbTable<ITEM>) table;
		AbstractColumn<ITEM, ?> baseColumn = filterTable.getFilteredColumns().get(index);
		TableColumn<ITEM, Object> tableColumn =
			new TableColumn<ITEM, Object>(baseColumn.getName());
		tableColumn.setSortable(false);

		if (baseColumn instanceof AbstractFilterColumn) {
			AbstractFilterColumn<ITEM, ?> filterColumn =
				(AbstractFilterColumn<ITEM, ?>) baseColumn;
			filterColumn.setSortComparatorQueue(filterTable.getSortComparatorQueue());
			filterMenuButtons
				.add(createFilterMenuButton(filterTable, filterColumn, index));
		}

		tableColumn.setSortable(false);
		tableColumn.setCellFactory(createCellFactory(tableColumn));
		tableColumn.setCellValueFactory(createCellValueFactory(tableColumn,
			cellData -> filterTable.getFilteredValue(cellData.getValue(), index)));
		return tableColumn;
	}

	public FilterMenuButton<ITEM, ?> createFilterMenuButton(DbTable<ITEM> table,
		AbstractFilterColumn<ITEM, ?> filterColumn, int index) {

		FilterMenuButton<ITEM, ?> filterMenuButton = new FilterMenuButton<ITEM, Object>();
		filterMenuButton.setText(filterColumn.getName());
		filterMenuButton.setColumn(filterColumn, () -> {
			table.applyFilter();
		});
		filterMenuButton.setParentList(() -> filterMenuButtons.stream()
			.map(button -> (FilterMenuButton<?, ?>) button).collect(Collectors.toList()));
		return filterMenuButton;
	}

	@Override
	public void reload() {
		System.out.println("DbFilterTableView - reload");
		if (getTable() instanceof DbTable)
			reloadFilterTable(
				(DbTable<ITEM>) getTable());
	}

	private void reloadFilterTable(DbTable<ITEM> table) {
		table.reload();
		
		
//		super.setItems(table.getFilteredItems());
		super.getItems().clear();
		super.getItems().addAll(table.getFilteredItems());
		
		
		
		refresh();
	}

	public List<FilterMenuButton<ITEM, ?>> getFilterMenuButtons() {
		return filterMenuButtons;
	}
	
	

}