package org.artorg.tools.phantomData.client.scene.control.tableView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.scene.control.FilterMenuButton;
import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.AbstractFilterColumn;
import org.artorg.tools.phantomData.client.table.IDbTable;
import org.artorg.tools.phantomData.client.table.IFilterTable;
import org.artorg.tools.phantomData.client.table.ITable;
import org.artorg.tools.phantomData.client.util.CollectionUtil;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;

public class DbFilterTableView<ITEM extends DbPersistent<ITEM, ?>>
	extends DbTableView<ITEM> {
	protected List<FilterMenuButton<ITEM, ?>> filterMenuButtons;

	{
		super.setEditable(true);
		filterMenuButtons = new ArrayList<FilterMenuButton<ITEM, ?>>();
	}

	public DbFilterTableView() {
		super();
	}

	public DbFilterTableView(Class<ITEM> itemClass) {
		super(itemClass);
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

	@SuppressWarnings("unchecked")
	@Override
	public void initTable() {
		if (!(getTable() instanceof IFilterTable)) {
			super.initTable();
			return;
		}

		IFilterTable<ITEM, ?> table = (IFilterTable<ITEM, ?>) getTable();
		refreshColumns();

		super.setItems(table.getFilteredItems());
		autoResizeColumns();
		super.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		table.applyFilter();

		Platform.runLater(() -> showFilterButtons());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void refreshColumns() {
		if (!(getTable() instanceof IFilterTable)) {
			super.initTable();
			return;
		}
		IFilterTable<ITEM, ?> table = (IFilterTable<ITEM, ?>) getTable();
		CollectionUtil.addIfAbsent(
			getTable().getColumnCreator().apply(table.getFilteredItems()),
			super.getColumns(), getColumnAddPolicy(),
			(baseColumn, index) -> createTableColumn(table, index));
		CollectionUtil.removeIfAbsent(
			getTable().getColumnCreator().apply(table.getFilteredItems()),
			super.getColumns(), getColumnRemovePolicy());
	}

	@Override
	protected TableColumn<ITEM, ?> createTableColumn(ITable<ITEM, ?> table, int index) {
		if (!(table instanceof IFilterTable))
			return super.createTableColumn(table, index);
		IFilterTable<ITEM, ?> filterTable = (IFilterTable<ITEM, ?>) table;
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

	public FilterMenuButton<ITEM, ?> createFilterMenuButton(IFilterTable<ITEM, ?> table,
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
	@SuppressWarnings("unchecked")
	public void reload() {
		if (getTable() instanceof IDbTable && getTable() instanceof IFilterTable)
			reloadFilterTable(
				(IDbTable<ITEM, Object> & IFilterTable<ITEM, Object>) getTable());
	}

	private <TABLE extends IDbTable<ITEM, R> & IFilterTable<ITEM, R>, R> void
		reloadFilterTable(TABLE table) {
		table.readAllData();
		super.setItems(table.getFilteredItems());
		refresh();
	}

}