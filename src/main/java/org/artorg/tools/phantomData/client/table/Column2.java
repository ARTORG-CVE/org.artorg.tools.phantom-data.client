package org.artorg.tools.phantomData.client.table;

import java.lang.reflect.InvocationTargetException;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.commons.beanutils.BeanUtils;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class Column2<ITEM extends DbPersistent<ITEM>, 
		PATH extends DbPersistent<PATH>> extends Column<ITEM, PATH>  {
	private final Function<ITEM, PATH> itemToPropertyGetter;
	private final Function<PATH, String> propertyToValueGetter;
	private final BiConsumer<PATH, String> propertyToValueSetter;
	
	public Column2(String columnName, Function<ITEM, PATH> itemToPropertyGetter, Class<PATH> pathClass, String propertyName) {
		super(columnName, null, null, null);
		
		this.itemToPropertyGetter = itemToPropertyGetter;
		this.propertyToValueGetter = getValueGetter(pathClass, propertyName);
		this.propertyToValueSetter = getValueSetter(pathClass, propertyName);
	}
	
	private static <PATH> Function<PATH, String> getValueGetter(Class<PATH> pathClass, String propertyName) {
		return path -> {
			try {
				return BeanUtils.getProperty(path, propertyName);
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
			return null;
		};
	}
	
	private static <PATH> BiConsumer<PATH, String> getValueSetter(Class<PATH> pathClass, String propertyName) {
		return (path, value) -> {
			try {
				BeanUtils.setProperty(path, propertyName, value);
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		};
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
		HttpConnectorSpring<PATH> connector = Connectors.getConnector(item.getClass());
		return connector.update(itemToPropertyGetter.apply(item));
	}

	
	
}
