package org.artorg.tools.phantomData.client.table;

public interface TableEditFilterable<T> extends TableEditable<T>, FilterableTable<T> {

	void setFilteredValue(T item, int localCol, String newValue);

}
