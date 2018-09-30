package org.artorg.tools.phantomData.client.tables.filterable.property;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoEditFilterTable;
import org.artorg.tools.phantomData.client.table.LambdaColumn;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.server.model.property.Property;
import org.artorg.tools.phantomData.server.model.property.PropertyField;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import java.util.UUID;

public abstract class PropertyFilterTable<ITEM extends Property<ITEM, VALUE> & DbPersistent<ITEM,UUID>, VALUE extends Comparable<VALUE>> 
		extends DbUndoRedoEditFilterTable<ITEM> {
	
	{
		List<Column<ITEM>> columns =
				new ArrayList<Column<ITEM>>();
		columns.add(new LambdaColumn<ITEM, PropertyField>(
				"property field", item -> item.getPropertyField(), 
				path -> path.getDescription(), 
				(path,value) -> path.setDescription(value)));
		columns.add(new LambdaColumn<ITEM, ITEM>(
				"value", item -> item, 
				path -> String.valueOf(path.getValue()), 
				(path,value) -> path.setValue(fromString(value))));
		this.setColumns(columns);
	}
	
	protected abstract ICrudConnector<ITEM,?> getPropertyConnector();
	
	protected abstract String toString(VALUE value);
	
	protected abstract VALUE fromString(String s);

}