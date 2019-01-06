package org.artorg.tools.phantomData.client.scene.control;

import java.util.Collection;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.table.Table;

import javafx.scene.Node;

public interface EntityView {

	Collection<Object> getSelectedItems();

	void refresh();

	Class<?> getItemClass();

	Table<?> getTable();

	Node getNode();

	default Object getSelectedItem() {
		return getSelectedItems().iterator().next();
	}

	default Object getEntityItem() {
		Object item = getSelectedItem();
		if (item instanceof Collection && !((Collection<?>) item).isEmpty())
			item = ((Collection<?>) item).iterator().next();
		final Object tempItem = item;
		if (Main.getEntityClasses().stream().filter(cls -> cls == tempItem.getClass()).findFirst()
				.isPresent())
			return item;
		throw new NullPointerException();
	}

}
