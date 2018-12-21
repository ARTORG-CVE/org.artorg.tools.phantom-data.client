package org.artorg.tools.phantomData.client.modelUI;

import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.editor.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.table.DbTable;
import org.artorg.tools.phantomData.client.table.TableBase;

public interface UIEntity<T> {

	Class<T> getItemClass();
	
	String getTableName();
	
	List<AbstractColumn<T, ? extends Object>> createColumns(List<T> items);

	ItemEditFactoryController<T> createEditFactory();

	default TableBase<T> createTableBase() {
		TableBase<T> table = new TableBase<T>(getItemClass()) {
			@Override
			public List<AbstractColumn<T, ? extends Object>> createColumns(List<T> items) {
				return UIEntity.this.createColumns(items);
			}			
		};
		table.setTableName(getTableName());
		return table;
	}
	
	default DbTable<T> createDbTableBase() {
		DbTable<T> table = new DbTable<T>(getItemClass()) {
			@Override
			public List<AbstractColumn<T, ? extends Object>> createColumns(List<T> items) {
				return UIEntity.this.createColumns(items);
			}			
		};
		table.setTableName(getTableName());
		return table;
	}

}
