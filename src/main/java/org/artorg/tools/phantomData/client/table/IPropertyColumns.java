package org.artorg.tools.phantomData.client.table;

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
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.server.model.property.Property;
import org.artorg.tools.phantomData.server.model.property.PropertyContainer;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.collections.ObservableList;

public interface IPropertyColumns {
	
	default <ITEM extends PropertyContainer & DbPersistent<ITEM,ID>, ID> 
		void createPropertyColumns(List<IColumn<ITEM>> columns, 
				ObservableList<ITEM> items) {
		createPropertyColumns(columns, items, 
				container -> container.getBooleanProperties(), 
				bool -> String.valueOf(bool),
				s -> Boolean.valueOf(s));
		createPropertyColumns(columns, items, 
				container -> container.getDoubleProperties(), 
				bool -> String.valueOf(bool),
				s -> Double.valueOf(s));
		createPropertyColumns(columns, items, 
				container -> container.getIntegerProperties(), 
				bool -> String.valueOf(bool),
				s -> Integer.valueOf(s));
		createPropertyColumns(columns, items, 
				container -> container.getStringProperties(), 
				s -> s,
				s -> s);
		
		DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
		Function<String,Date> stringDateFunc = s -> {
				try {
					format.parse(s);
				} catch (ParseException e) {
					e.printStackTrace();
				};
				throw new IllegalArgumentException();
		};
		createPropertyColumns(columns, items, 
				container -> container.getDateProperties(), 
				date -> String.valueOf(date),
				stringDateFunc);
	}
	
	default <ITEM extends DbPersistent<ITEM,?>, 
			PROPERTY_TYPE extends Property<PROPERTY_TYPE, PROPERTY_VALUE_TYPE>, 
			PROPERTY_VALUE_TYPE extends Comparable<PROPERTY_VALUE_TYPE>> 
			void createPropertyColumns(List<IColumn<ITEM>> columns, ObservableList<ITEM> items, 
					Function<ITEM,Collection<PROPERTY_TYPE>> propsGetter, 
					Function<PROPERTY_VALUE_TYPE, String> toStringFun, Function<String, PROPERTY_VALUE_TYPE> fromStringFun) {
		{

			Map<UUID, String> map = new HashMap<UUID, String>();
			Set<PROPERTY_TYPE> set = items.stream().flatMap(s -> propsGetter.apply(s).stream())
					.collect(Collectors.toSet());
			set.stream().sorted((p1, p2) -> p1.getPropertyField().getId().compareTo(p2.getPropertyField().getId()))
					.forEach(p -> map.put(p.getPropertyField().getId(), p.getPropertyField().getDescription()));
			map.entrySet().stream()
					.forEach(entry -> columns.add(new ColumnOptional<ITEM, PROPERTY_TYPE>(entry.getValue(),
							item -> propsGetter.apply(item).stream()
									.filter(p -> p.getPropertyField().getId() == entry.getKey()).findFirst(),
							path -> toStringFun.apply(path.getValue()),
							(path, value) -> path.setValue(fromStringFun.apply((String) value)), "")));
		}
	}
}
