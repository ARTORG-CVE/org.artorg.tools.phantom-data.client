package org.artorg.tools.phantomData.client.table;

import java.util.List;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.logging.Logger;
import org.artorg.tools.phantomData.server.model.Identifiable;

import javafx.collections.MapChangeListener;
import javafx.collections.MapChangeListener.Change;

public abstract class DbTable<T> extends Table<T> {
	private final ICrudConnector<T> connector;
	private final MapChangeListener<String, T> listener;

	{
		connector = Connectors.get(getItemClass());
		listener = change -> applyChanges(change, getItems());
	}

	public ICrudConnector<T> getConnector() {
		return this.connector;
	}

	public DbTable(Class<T> itemClass) {
		super(itemClass);
		setListening(true);

	}
	
	public void reload() {
		Logger.debug.println(getItemClass().getSimpleName());

		setListening(false);
		getItems().removeListener(getItemListChangeListener());
		connector.reload();
		getItems().addListener(getItemListChangeListener());
		setListening(true);

		readAllData();
	}

	public void readAllData() {
		Logger.debug.println(getItemClass().getSimpleName());
		if (connector == null) throw new NullPointerException();

		setListening(false);
		getItems().removeListener(getItemListChangeListener());
		getItems().clear();
		getItems().addAll(connector.readAllAsList());
		getItems().addListener(getItemListChangeListener());
		setListening(true);

		updateItems();
		updateColumns();
	}

	private void setListening(boolean b) {
		if (b) connector.addListener(listener);
		else
			connector.removeListener(listener);
	}

	private void applyChanges(Change<? extends String, ? extends T> change, List<T> items) {
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
		} else if (i < items.size()) {
			try {
				items.remove(change.getValueRemoved());
			} catch (UnsupportedOperationException e) {
				e.printStackTrace();
			}
		}
	}

	private static <T> int getIndex(List<T> items, String id) {
		for (int i = 0; i < items.size(); i++)
			if (((Identifiable<?>) items.get(i)).getId().toString().equals(id)) return i;
		return items.size();
	}

}
