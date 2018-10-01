package org.artorg.tools.phantomData.client.tables.filterable.property;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoEditFilterTable;
import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.server.model.property.Property;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public abstract class PropertyFilterTable<ITEM extends Property<ITEM, VALUE> & DbPersistent<ITEM,UUID>, VALUE extends Comparable<VALUE>> 
		extends DbUndoRedoEditFilterTable<ITEM> {
	
	{
		List<AbstractColumn<ITEM>> columns =
				new ArrayList<AbstractColumn<ITEM>>();
		columns.add(new FilterColumn<ITEM>(
				"property field", item -> item.getPropertyField(), 
				path -> path.getDescription(), 
				(path,value) -> path.setDescription(value)));
		columns.add(new FilterColumn<ITEM>(
				"value", item -> item, 
				path -> String.valueOf(path.getValue()), 
				(path,value) -> path.setValue(fromString(value))));
		this.setColumns(columns);
	}
	
	protected abstract ICrudConnector<ITEM,?> getPropertyConnector();
	
	protected abstract String toString(VALUE value);
	
	protected abstract VALUE fromString(String s);

}