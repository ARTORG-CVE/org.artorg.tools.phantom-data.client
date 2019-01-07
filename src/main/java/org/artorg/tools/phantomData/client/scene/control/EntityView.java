package org.artorg.tools.phantomData.client.scene.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.table.Table;

import javafx.scene.Node;
import javafx.scene.control.TableSelectionModel;

public interface EntityView {

	Collection<Object> getSelectedItems();

	TableSelectionModel<?> getSelectionModel();

	void refresh();

	Class<?> getItemClass();

	Table<?> getTable();

	Node getNode();

	default Object getSelectedItem() {
		Iterator<?> iterator = getSelectedItems().iterator();
		if (iterator.hasNext()) return getSelectedItems().iterator().next();
		return null;
	}

	default List<Object> getEntityItems() {
		Collection<Object> selectedItems = getSelectedItems();
		return selectedItems.stream().map(item -> getEntityItem(item)).filter(item -> item != null)
				.collect(
						Collectors.toCollection(() -> new ArrayList<Object>(selectedItems.size())));
	}

	default Object getEntityItem() {
		return getEntityItem(getSelectedItem());
	}

	default Object getEntityItem(Object item) {
		if (item == null) return null;
		if (item instanceof Collection && !((Collection<?>) item).isEmpty())
			item = ((Collection<?>) item).iterator().next();
		final Object tempItem = item;
		if (Main.getEntityClasses().stream().filter(cls -> cls == tempItem.getClass()).findFirst()
				.isPresent())
			return item;
		throw new NullPointerException();
	}

}
