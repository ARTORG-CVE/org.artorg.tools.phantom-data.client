package org.artorg.tools.phantomData.client.scene.control.table;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.connectors.property.BooleanPropertyConnector;
import org.artorg.tools.phantomData.client.connectors.property.DatePropertyConnector;
import org.artorg.tools.phantomData.client.connectors.property.DoublePropertyConnector;
import org.artorg.tools.phantomData.client.connectors.property.IntegerPropertyConnector;
import org.artorg.tools.phantomData.client.connectors.property.StringPropertyConnector;
import org.artorg.tools.phantomData.server.model.property.Property;
import org.artorg.tools.phantomData.server.model.property.PropertyContainer;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.collections.ObservableList;

@SuppressWarnings("restriction")
public interface PropertyColumns {
	
	default <ITEM extends DatabasePersistent<ID_TYPE>, ID_TYPE> 
		void createPropertyColumns(List<IColumn<ITEM, ?>> columns, 
				ObservableList<ITEM> items, Function<ITEM,PropertyContainer> propContGetter) {
		createPropertyColumns(columns, items, propContGetter, 
				container -> container.getBooleanProperties(), 
				bool -> String.valueOf(bool),
				s -> Boolean.valueOf(s), BooleanPropertyConnector.get());
		createPropertyColumns(columns, items, propContGetter, 
				container -> container.getDoubleProperties(), 
				bool -> String.valueOf(bool),
				s -> Double.valueOf(s), DoublePropertyConnector.get());
		createPropertyColumns(columns, items, propContGetter, 
				container -> container.getIntegerProperties(), 
				bool -> String.valueOf(bool),
				s -> Integer.valueOf(s), IntegerPropertyConnector.get());
		createPropertyColumns(columns, items, propContGetter, 
				container -> container.getStringProperties(), 
				s -> s,
				s -> s, StringPropertyConnector.get());
		
		DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
		Function<String,Date> stringDateFunc = s -> {
				try {
					format.parse(s);
				} catch (ParseException e) {
					e.printStackTrace();
				};
				throw new IllegalArgumentException();
		};
		createPropertyColumns(columns, items, propContGetter, 
				container -> container.getDateProperties(), 
				date -> String.valueOf(date),
				stringDateFunc, DatePropertyConnector.get());
	}
	
	default <ITEM extends DatabasePersistent<ID_TYPE>, 
			PROPERTY_TYPE extends Property<PROPERTY_VALUE_TYPE, SUB_ID_TYPE> & DatabasePersistent<SUB_ID_TYPE>, 
			PROPERTY_VALUE_TYPE extends Comparable<PROPERTY_VALUE_TYPE>, 
			ID_TYPE, 
			SUB_ID_TYPE> 
			void createPropertyColumns(List<IColumn<ITEM, ?>> columns, ObservableList<ITEM> items, 
					Function<ITEM,PropertyContainer> propContGetter, 
					Function<PropertyContainer,Collection<PROPERTY_TYPE>> propsGetter, 
					Function<PROPERTY_VALUE_TYPE, String> toStringFun, Function<String, PROPERTY_VALUE_TYPE> fromStringFun,
					HttpDatabaseCrud<PROPERTY_TYPE , SUB_ID_TYPE> connector) {
		{
			Function<ITEM,Collection<PROPERTY_TYPE>> propertiesGetter = propContGetter.andThen(propsGetter);
			Map<Integer, String> map = new HashMap<Integer, String>();
			Set<PROPERTY_TYPE> set = items.stream().flatMap(s -> propertiesGetter.apply(s).stream())
					.collect(Collectors.toSet());
			set.stream().sorted((p1, p2) -> p1.getPropertyField().getId().compareTo(p2.getPropertyField().getId()))
					.forEach(p -> map.put(p.getPropertyField().getId(), p.getPropertyField().getDescription()));
			map.entrySet().stream()
					.forEach(entry -> columns.add(new ColumnOptional<ITEM, PROPERTY_TYPE, SUB_ID_TYPE>(entry.getValue(),
							item -> propertiesGetter.apply(item).stream()
									.filter(p -> p.getPropertyField().getId() == entry.getKey()).findFirst(),
							path -> toStringFun.apply(path.getValue()),
							(path, value) -> path.setValue(fromStringFun.apply((String) value)), "",
							connector)));
		}
	}
}
