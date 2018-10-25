package org.artorg.tools.phantomData.client.table;

import java.lang.reflect.InvocationTargetException;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.commons.beanutils.BeanUtils;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class BeanColumn<T extends DbPersistent<T,?>, 
		SUB_T extends DbPersistent<SUB_T,?>> extends AbstractColumn<T>  {
	private final Function<T, SUB_T> itemToPropertyGetter;
	private final Function<SUB_T, String> propertyToValueGetter;
	private final BiConsumer<SUB_T, String> propertyToValueSetter;
	
	public BeanColumn(String columnName, Function<T, SUB_T> itemToPropertyGetter, Class<SUB_T> pathClass, String propertyName) {
		super(columnName);
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
	public String get(T item) {
		return propertyToValueGetter.apply(itemToPropertyGetter.apply(item));
	}
	
	@Override
	public void set(T item, String value) {
		propertyToValueSetter.accept(itemToPropertyGetter.apply(item), value);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <U extends DbPersistent<U,SUB_ID>, SUB_ID extends Comparable<SUB_ID>> boolean update(T item) {
		System.out.println("updated value in database :)");
		U path = (U) itemToPropertyGetter.apply(item);
		ICrudConnector<U,SUB_ID> connector = Connectors.getConnector(path.getItemClass());
		return connector.update(path);
	}
	
	@Override
	public boolean isIdColumn() {
		throw new UnsupportedOperationException();
	}

	
	
}
