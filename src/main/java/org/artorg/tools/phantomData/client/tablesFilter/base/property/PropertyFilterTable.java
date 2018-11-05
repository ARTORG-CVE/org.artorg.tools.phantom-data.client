package org.artorg.tools.phantomData.client.tablesFilter.base.property;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.client.table.IPersonifiedColumns;
import org.artorg.tools.phantomData.server.model.specification.AbstractProperty;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public abstract class PropertyFilterTable<
	ITEM extends AbstractProperty<ITEM, VALUE> & DbPersistent<ITEM, UUID>,
	VALUE extends Comparable<VALUE>>
	extends DbUndoRedoFactoryEditFilterTable<ITEM> implements IPersonifiedColumns {

	{
		setColumnCreator(items -> {
			List<AbstractColumn<ITEM>> columns =
				new ArrayList<AbstractColumn<ITEM>>();
			columns.add(new FilterColumn<ITEM>(
					"Type", item -> item,
					path -> {
						try {
							return Class.forName(path.getPropertyField().getType()).getSimpleName();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
						return path.getPropertyField().getType();
					},
					(path, value) -> {}));
			columns.add(new FilterColumn<ITEM>(
				"Field Name", item -> item.getPropertyField(),
				path -> path.getName(),
				(path, value) -> path.setName(value)));
			columns.add(new FilterColumn<ITEM>(
				"Value", item -> item,
				path -> String.valueOf(path.getValue()),
				(path, value) -> path.setValue(fromString(value))));
			createBaseColumns(columns);
			return columns;
		});
	}

	protected abstract String toString(VALUE value);

	protected abstract VALUE fromString(String s);

}