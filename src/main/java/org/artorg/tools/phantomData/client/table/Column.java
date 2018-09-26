package org.artorg.tools.phantomData.client.table;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class Column<ITEM extends DbPersistent<ITEM>, 
		PATH extends DbPersistent<PATH>> extends IColumn<ITEM> {
	private final Function<ITEM, PATH> itemToPropertyGetter;
	private final Function<PATH, String> propertyToValueGetter;
	private final BiConsumer<PATH, String> propertyToValueSetter;
	
	public Column(String columnName, Function<ITEM, PATH> itemToPropertyGetter, 
			Function<PATH, String> propertyToValueGetter, 
			BiConsumer<PATH, String> propertyToValueSetter) {
		super(columnName);
		this.itemToPropertyGetter = itemToPropertyGetter;
		this.propertyToValueGetter = propertyToValueGetter;
		this.propertyToValueSetter = propertyToValueSetter;
	}
	
	@Override
	public String get(ITEM item) {
		return propertyToValueGetter.apply(itemToPropertyGetter.apply(item));
	}
	
	@Override
	public void set(ITEM item, String value) {
		propertyToValueSetter.accept(itemToPropertyGetter.apply(item), value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean update(ITEM item) {
		System.out.println("updated value in database :)");
		HttpConnectorSpring<PATH> connector = (HttpConnectorSpring<PATH>) Connectors.getConnector(item.getClass());
		return connector.update(itemToPropertyGetter.apply(item));
	}

	
	
}
