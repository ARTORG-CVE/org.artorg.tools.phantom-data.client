package org.artorg.tools.phantomData.client.table;

import java.util.HashSet;
import java.util.Set;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.AbstractFilterColumn;
import org.artorg.tools.phantomData.client.column.Column;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.util.CollectionUtil;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DbTable<ITEM extends DbPersistent<ITEM, ?>> extends TableBase<ITEM> {
	private ICrudConnector<ITEM> connector;

	{
		connector = Connectors.getConnector(getItemClass());
	}

	public ICrudConnector<ITEM> getConnector() {
		return this.connector;
	}

	

	public void readAllData() {
		Class<ITEM> itemClass = getItemClass();
		if (itemClass == null) throw new NullPointerException();
		ICrudConnector<ITEM> connector = getConnector();
		if (connector == null) throw new NullPointerException();

		ObservableList<ITEM> items = FXCollections.observableArrayList();
		items.addAll(connector.readAllAsList());

		CollectionUtil.syncLists(items, getItems());

		getColumns().stream().forEach(column -> {
			column.setItems(getItems());
//			column.getItems().clear();
//			column.getItems().addAll(getItems());

		});

		if (isFilterable()) {
			CollectionUtil.syncLists(super.getItems(), getFilteredItems());
			applyFilter();
		}
	}
	
	@Override
	public void updateColumns() {
		getColumns().forEach(column -> column.setIdColumn(false));
		AbstractColumn<ITEM, ? extends Object> idColumn =
			(AbstractColumn<ITEM, ? extends Object>) new Column<ITEM, String>("ID",
				item -> item, path -> path.getId().toString(),
				(path, value) -> path.setId(value));
		idColumn.setIdColumn(true);
		idColumn.setVisibility(false);
		getColumns().add(0, idColumn);
		super.updateColumns();
	}

	@Override
	public String getItemName() {
		return getItemClass().getSimpleName();
	}

	public void createItem(ITEM item) {
		getConnector().create(item);
		getItems().add(item);
	}

}
