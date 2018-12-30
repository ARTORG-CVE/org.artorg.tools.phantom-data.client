package org.artorg.tools.phantomData.client.table;

import java.util.List;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.logging.Logger;
import org.artorg.tools.phantomData.server.model.Identifiable;
import org.artorg.tools.phantomData.server.models.base.property.IntegerProperty;

import javafx.collections.MapChangeListener;
import javafx.collections.MapChangeListener.Change;

public abstract class DbTable<ITEM> extends Table<ITEM> {
	private final ICrudConnector<ITEM> connector;
	private final MapChangeListener<String, ITEM> listener;

	{
		connector = Connectors.get(getItemClass());
		listener = change -> {
			applyChanges(change, getItems());
//			if (isFilterable()) applyChanges(change, getFilteredItems());
		};
	}

	public ICrudConnector<ITEM> getConnector() {
		return this.connector;
	}

	public DbTable(Class<ITEM> itemClass) {
		super(itemClass);
		setListening(true);

	}

	private void setListening(boolean b) {
		if (b) connector.addListener(listener);
		else
			 connector.removeListener(listener);
	}

	private void applyChanges(Change<? extends String, ? extends ITEM> change, List<ITEM> items) {
		int i = getIndex(items, change.getKey());
		if (change.wasAdded()) {
			if (change.wasRemoved()) {
				try {
				if (i < items.size()) items.set(i, change.getValueAdded());
				} catch (UnsupportedOperationException e) {
					e.printStackTrace();
				}
			} else if (i == items.size()) {
				items.add(change.getValueAdded());
			}
		} else if (i < items.size()){
			 try {
				 items.remove(change.getValueRemoved());
				 
//			items.remove(i);
			 } catch (UnsupportedOperationException e) {
				 e.printStackTrace();
			 }
		}
	}

	private <T> int getIndex(List<T> items, String id) {
		for (int i = 0; i < items.size(); i++)
			if (((Identifiable<?>) items.get(i)).getId().toString().equals(id)) return i;
		return items.size();
	}

	public void reload() {
		setListening(false);
		Logger.debug.println(getItemClass().getSimpleName());
		Class<ITEM> itemClass = getItemClass();
		if (itemClass == null) throw new NullPointerException();
		ICrudConnector<ITEM> connector = getConnector();
		if (connector == null) throw new NullPointerException();

		getItems().removeListener(getItemListChangeListener());
		connector.reload();
		readAllData();
		getItems().addListener(getItemListChangeListener());
		setListening(true);
	}

	public void readAllData() {
		setListening(false);
		Logger.debug.println(getItemClass().getSimpleName());
		if (getItemClass() == IntegerProperty.class)
			System.out.println("-> :)");
		Class<ITEM> itemClass = getItemClass();
		if (itemClass == null) throw new NullPointerException();
		ICrudConnector<ITEM> connector = getConnector();
		if (connector == null) throw new NullPointerException();

		getItems().clear();
		getItems().addAll(connector.readAllAsList());

//		CollectionUtil.syncLists(items, getItems());

		setListening(true);
	}

//	@Override
//	public void updateColumns() {
//		System.out.println("DbTable - updateColumns");
//		getColumns().forEach(column -> column.setIdColumn(false));
//		AbstractColumn<ITEM, ? extends Object> idColumn =
//			(AbstractColumn<ITEM, ? extends Object>) new Column<ITEM, String>("ID",
//				item -> item, path -> path.getId().toString(),
//				(path, value) -> path.setId(value));
//		idColumn.setIdColumn(true);
//		idColumn.setVisibility(false);
//		getColumns().add(0, idColumn);
//		super.updateColumns();
//	}

	@Override
	public String getItemName() {
		return getItemClass().getSimpleName();
	}

}
