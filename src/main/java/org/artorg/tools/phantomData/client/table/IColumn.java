package org.artorg.tools.phantomData.client.table;

import org.artorg.tools.phantomData.server.specification.DbPersistent;

public abstract class IColumn<ITEM> {
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
	
	public abstract String get(ITEM item);
	
	public abstract void set(ITEM item, String value);
	
	public abstract <U extends DbPersistent<U,SUB_ID>, SUB_ID> boolean update(ITEM item);
	
	public abstract boolean isIdColumn();
	
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
