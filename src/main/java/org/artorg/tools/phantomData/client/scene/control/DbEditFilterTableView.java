package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.table.IDbEditFilterTable;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;

public abstract class DbEditFilterTableView<ITEM extends DbPersistent<ITEM, ?>, TABLE extends IDbEditFilterTable<ITEM>>
		extends DbFactoryFilterTableView<ITEM, TABLE> {

	@SuppressWarnings("unchecked")
	@Override
	public void initTable() {
		super.initTable();

		ObservableList<TableColumn<ITEM, ?>> columns = super.getColumns();

		int nCols = getTable().getFilteredNcols();
		for (int col = 0; col < nCols; col++) {
			TableColumn<ITEM, String> column = (TableColumn<ITEM, String>) columns.get(col);

			final int localCol = col;
			column.setOnEditCommit(new EventHandler<CellEditEvent<ITEM, String>>() {
				@Override
				public void handle(CellEditEvent<ITEM, String> t) {
					ITEM item = ((ITEM) t.getTableView().getItems().get(t.getTablePosition().getRow()));
					getTable().setFilteredValue(item, localCol, t.getNewValue());
				}
			});
		}
	}

}
