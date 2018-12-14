package org.artorg.tools.phantomData.client.modelUI;

import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.editor.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.table.DbTable;
import org.artorg.tools.phantomData.client.table.TableBase;

public interface UIEntity<T> {

	Class<T> getItemClass();

	String getTableName();

	List<AbstractColumn<T,?>> createColumns();

	ItemEditFactoryController<T> createEditFactory();
	
	default TableBase<T> createTableBase() {
		return new TableBase<T>(getItemClass()) {
			{
				setTableName(getTableName());
				setColumnCreator(items -> createColumns());
			}
		};
	}
	
	default DbTable<T> createDbTableBase() {
		return new DbTable<T>(getItemClass()) {
			{
				setTableName(getTableName());
				setColumnCreator(items -> createColumns());
			}
		};
	}

}
