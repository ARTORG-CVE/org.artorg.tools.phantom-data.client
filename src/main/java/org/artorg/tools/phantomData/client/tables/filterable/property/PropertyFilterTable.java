package org.artorg.tools.phantomData.client.tables.filterable.property;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connector.CrudConnector;
import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoEditFilterTable;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.IColumn;
import org.artorg.tools.phantomData.server.model.property.Property;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

public abstract class PropertyFilterTable<ITEM extends Property<ITEM, VALUE>, VALUE extends Comparable<VALUE>> 
		extends DbUndoRedoEditFilterTable<ITEM> {
	
	{
		List<IColumn<ITEM>> columns =
				new ArrayList<IColumn<ITEM>>();
		columns.add(new Column<ITEM, PropertyField>(
				"property field", item -> item.getPropertyField(), 
				path -> path.getDescription(), 
				(path,value) -> path.setDescription(value)));
		columns.add(new Column<ITEM, ITEM>(
				"value", item -> item, 
				path -> String.valueOf(path.getValue()), 
				(path,value) -> path.setValue(fromString(value))));
		this.setColumns(columns);
	}
	
	protected abstract CrudConnector<ITEM,?> getPropertyConnector();
	
	protected abstract String toString(VALUE value);
	
	protected abstract VALUE fromString(String s);

}