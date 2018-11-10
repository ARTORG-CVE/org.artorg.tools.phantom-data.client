package org.artorg.tools.phantomData.client.scene.control.tableView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.artorg.tools.phantomData.client.scene.control.FilterMenuButton;
import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.AbstractFilterColumn;
import org.artorg.tools.phantomData.client.table.IDbTable;
import org.artorg.tools.phantomData.client.table.IFilterTable;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;

public class DbFilterTableView<ITEM extends DbPersistent<ITEM, ?>> extends DbTableView<ITEM> {
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
		for (Node n : super.lookupAll(".column-header > .label")) {
			if (n instanceof Label) {
				Label label = (Label) n;

				String columnName = label.getText();
				Optional<FilterMenuButton<ITEM, ?>> filterMenuButton = filterMenuButtons.stream()
						.filter(f -> f.getText().equals(columnName)).findFirst();
				if (filterMenuButton.isPresent()) {
					filterMenuButton.get().prefWidthProperty().bind(label.widthProperty());
					filterMenuButton.get().getStyleClass().add("filter-menu-button");
					label.setGraphic(filterMenuButton.get());
					label.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
				}

			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initTable() {
		if (getTable() instanceof IFilterTable)
			initFilterTable((IFilterTable<ITEM>) getTable());
		else
			super.initTable();
	}

	protected void initFilterTable(IFilterTable<ITEM> table) {
		super.getColumns().removeAll(super.getColumns());

		// creating columns
		List<TableColumn<ITEM, ?>> columns = new ArrayList<TableColumn<ITEM, ?>>();
		List<String> columnNames = table.getFilteredColumnNames();

		int nCols = table.getFilteredNcols();

		for (int col = 0; col < nCols; col++) {
			TableColumn<ITEM, Object> column = new TableColumn<ITEM, Object>(columnNames.get(col));
			column.setSortable(false);

			final int localCol = col;
			AbstractColumn<ITEM, ?> filterColumn = table.getFilteredColumns().get(localCol);

			if (filterColumn instanceof AbstractFilterColumn) {
				AbstractFilterColumn<ITEM, ?> filterColumn2 = (AbstractFilterColumn<ITEM, ?>) filterColumn;

				FilterMenuButton<ITEM, ?> filterMenuButton = new FilterMenuButton<ITEM, Object>();
				filterMenuButton.setText(columnNames.get(col));

				filterColumn2.setSortComparatorQueue(table.getSortComparatorQueue());
				filterMenuButton.setColumn(filterColumn2, () -> table.applyFilter());
				filterMenuButtons.add(filterMenuButton);
			}

			column.setCellFactory(tableColumn -> new TableCell<ITEM, Object>() {
				@Override
				protected void updateItem(Object item, boolean empty) {
					if (item instanceof Node) {
						Node node = (Node) item;
						setGraphic(node);

						Platform.runLater(() -> {
							tableColumn.setPrefWidth(this.getTableRow().getHeight());
						});
					} else if (item != null) {
						setText(item.toString());
					}
				}
			});

			column.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<Object>(
					table.getFilteredValue(cellData.getValue(), localCol)));

			columns.add(column);
		}

		super.getColumns().addAll(columns);
		super.setItems(table.getFilteredItems());
		autoResizeColumns();

		Platform.runLater(() -> showFilterButtons());
	}

	static HashMap<String, Image> mapOfFileExtToSmallIcon = new HashMap<String, Image>();

	@Override
	@SuppressWarnings("unchecked")
	public void reload() {
		if (getTable() instanceof IDbTable && getTable() instanceof IFilterTable)
			reloadFilterTable((IDbTable<ITEM> & IFilterTable<ITEM>) getTable());
	}

	private <TABLE extends IDbTable<ITEM> & IFilterTable<ITEM>> void reloadFilterTable(TABLE table) {
		table.readAllData();
		super.setItems(table.getFilteredItems());
		refresh();
	}

}