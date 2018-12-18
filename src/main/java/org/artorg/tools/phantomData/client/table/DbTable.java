package org.artorg.tools.phantomData.client.table;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.CrudConnector;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.util.CollectionUtil;
import org.artorg.tools.phantomData.server.logging.Logger;

public class DbTable<ITEM> extends TableBase<ITEM> {
	private ICrudConnector<ITEM> connector;

	{
		connector = Connectors.getConnector(getItemClass());
	}

	public ICrudConnector<ITEM> getConnector() {
		return this.connector;
	}
	
	public DbTable(Class<ITEM> itemClass) {
		super(itemClass);
	}
	
	public void reload() {
		Logger.debug.println("DbTable - reload");
		Class<ITEM> itemClass = getItemClass();
		if (itemClass == null) throw new NullPointerException();
		ICrudConnector<ITEM> connector = getConnector();
		if (connector == null) throw new NullPointerException();
		
		if (connector instanceof CrudConnector)
			((CrudConnector<?>)connector).reload();
		readAllData();
	}
	

	public void readAllData() {
		Logger.debug.println("DbTable - readAllData");
		Class<ITEM> itemClass = getItemClass();
		if (itemClass == null) throw new NullPointerException();
		ICrudConnector<ITEM> connector = getConnector();
		if (connector == null) throw new NullPointerException();

		
		getItems().clear();
		getItems().addAll(connector.readAllAsList());

//		CollectionUtil.syncLists(items, getItems());
		
		
		

		getColumns().stream().forEach(column -> {
			column.setItems(getItems());
//			column.getItems().clear();
//			column.getItems().addAll(getItems());

		});

		if (isFilterable()) {
			CollectionUtil.syncLists(super.getItems(), getFilteredItems());
			applyFilter();
		}
	}
	
//	@Override
//	public void updateColumns() {
//		System.out.println("DbTable - updateColumns");
//		getColumns().forEach(column -> column.setIdColumn(false));
//		AbstractColumn<ITEM, ? extends Object> idColumn =
//			(AbstractColumn<ITEM, ? extends Object>) new Column<ITEM, String>("ID",
//				item -> item, path -> path.getId().toString(),
//				(path, value) -> path.setId(value));
//		idColumn.setIdColumn(true);
//		idColumn.setVisibility(false);
//		getColumns().add(0, idColumn);
//		super.updateColumns();
//	}

	@Override
	public String getItemName() {
		return getItemClass().getSimpleName();
	}

	public void createItem(ITEM item) {
		getConnector().create(item);
		getItems().add(item);
	}

}
