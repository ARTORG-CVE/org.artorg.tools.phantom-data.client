package org.artorg.tools.phantomData.client.table;

public interface IEditFilterTable<T> extends IEditTable<T>, IFilterTable<T> {

	void setFilteredValue(T item, int localCol, String newValue);

}
