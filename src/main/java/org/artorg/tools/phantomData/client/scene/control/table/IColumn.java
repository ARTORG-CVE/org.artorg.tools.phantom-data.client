package org.artorg.tools.phantomData.client.scene.control.table;

import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public abstract class IColumn<ITEM extends DatabasePersistent<?>, SUB_ID_TYPE> {
	private boolean visible;
	private boolean editable;
	private boolean filterable;
	private final String columnName;

	{
		visible = true;
		editable = true;
		filterable = true;
	}
	
	public IColumn(String columnName) {
		this.columnName = columnName;
	}
	
	abstract String get(ITEM item);
	
	abstract void set(ITEM item, String value);
	
	abstract boolean update(ITEM item);
	
	public String getColumnName() {
		return columnName;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisibility(boolean visible) {
		this.visible = visible;
	}
	
	public boolean isEditable() {
		return editable;
	}
	
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isFilterable() {
		return filterable;
	}

	public void setFilterable(boolean filterable) {
		this.filterable = filterable;
	}
	
	

}