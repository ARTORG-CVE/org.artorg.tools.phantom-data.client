package org.artorg.tools.phantomData.client.scene.control.tableView;

import org.artorg.tools.phantomData.client.table.IEditFilterTable;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;

public abstract class DbEditFilterTableView<ITEM extends DbPersistent<ITEM, ?>>
		extends DbFilterTableView<ITEM> {

	public DbEditFilterTableView() {}
	
	public DbEditFilterTableView(Class<ITEM> itemClass) {
		super(itemClass);
	}
			
	@SuppressWarnings("unchecked")
	@Override
	public void initTable() {
		if (getTable() instanceof IEditFilterTable)
			initEditFilterTable((IEditFilterTable<ITEM,Object>) getTable());
		else
			super.initTable();
	}
	
	@SuppressWarnings("unchecked")
	protected void initEditFilterTable(IEditFilterTable<ITEM,Object> table) {
		super.initTable();

		ObservableList<TableColumn<ITEM, ?>> columns = super.getColumns();

		int nCols = table.getFilteredNcols();
		for (int col = 0; col < nCols; col++) {
			TableColumn<ITEM, String> column = (TableColumn<ITEM, String>) columns.get(col);

			final int localCol = col;
			column.setOnEditCommit(new EventHandler<CellEditEvent<ITEM, String>>() {
				@Override
				public void handle(CellEditEvent<ITEM, String> t) {
					ITEM item = ((ITEM) t.getTableView().getItems().get(t.getTablePosition().getRow()));
					table.setFilteredValue(item, localCol, t.getNewValue());
				}
			});
		}
		super.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
	}

}
