package org.artorg.tools.phantomData.client.table;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.connectors.property.BooleanPropertyConnector;
import org.artorg.tools.phantomData.client.connectors.property.DatePropertyConnector;
import org.artorg.tools.phantomData.client.connectors.property.DoublePropertyConnector;
import org.artorg.tools.phantomData.client.connectors.property.IntegerPropertyConnector;
import org.artorg.tools.phantomData.client.connectors.property.StringPropertyConnector;
import org.artorg.tools.phantomData.server.model.Special;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;
import org.artorg.tools.phantomData.server.model.property.DateProperty;
import org.artorg.tools.phantomData.server.model.property.DoubleProperty;
import org.artorg.tools.phantomData.server.model.property.IntegerProperty;
import org.artorg.tools.phantomData.server.model.property.PropertyContainer;
import org.artorg.tools.phantomData.server.model.property.StringProperty;

import javafx.collections.ObservableList;

public interface PropertyColumns {
	
	
	default <ITEM> void createPropertyColumns(List<IColumn<Special, ?>> columns, ObservableList<ITEM> items, Function<ITEM,PropertyContainer> propContGetter) {
		{
			Map<Integer, String> map = new HashMap<Integer, String>();
			Set<BooleanProperty> set = items.stream().flatMap(s -> propContGetter.apply(s).getBooleanProperties().stream())
					.collect(Collectors.toSet());
			set.stream().sorted((p1, p2) -> p1.getPropertyField().getId().compareTo(p2.getPropertyField().getId()))
					.forEach(p -> map.put(p.getPropertyField().getId(), p.getPropertyField().getDescription()));
			map.entrySet().stream()
					.forEach(entry -> columns.add(new ColumnOptional<Special, BooleanProperty, Integer>(entry.getValue(),
							item -> item.getPropertyContainer().getBooleanProperties().stream()
									.filter(p -> p.getPropertyField().getId() == entry.getKey()).findFirst(),
							path -> String.valueOf(path.getValue()),
							(path, value) -> path.setValue(Boolean.valueOf((String) value)), "",
							BooleanPropertyConnector.get())));
		}
		
		DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
		
		{
			Map<Integer, String> map = new HashMap<Integer, String>();
			Set<DateProperty> set = items.stream().flatMap(s -> propContGetter.apply(s).getDateProperties().stream())
					.collect(Collectors.toSet());
			set.stream().sorted((p1, p2) -> p1.getPropertyField().getId().compareTo(p2.getPropertyField().getId()))
					.forEach(p -> map.put(p.getPropertyField().getId(), p.getPropertyField().getDescription()));
			map.entrySet().stream()
					.forEach(entry -> columns.add(new ColumnOptional<Special, DateProperty, Integer>(entry.getValue(),
							item -> item.getPropertyContainer().getDateProperties().stream()
									.filter(p -> p.getPropertyField().getId() == entry.getKey()).findFirst(),
							path -> String.valueOf(path.getValue()),
							(path, value) -> {
								try {
									path.setValue(format.parse((String) value));
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}, "",
							DatePropertyConnector.get())));
		}
		
		{
			Map<Integer, String> map = new HashMap<Integer, String>();
			Set<StringProperty> set = items.stream().flatMap(s -> propContGetter.apply(s).getStringProperties().stream())
					.collect(Collectors.toSet());
			set.stream().sorted((p1, p2) -> p1.getPropertyField().getId().compareTo(p2.getPropertyField().getId()))
					.forEach(p -> map.put(p.getPropertyField().getId(), p.getPropertyField().getDescription()));
			map.entrySet().stream()
					.forEach(entry -> columns.add(new ColumnOptional<Special, StringProperty, Integer>(entry.getValue(),
							item -> item.getPropertyContainer().getStringProperties().stream()
									.filter(p -> p.getPropertyField().getId() == entry.getKey()).findFirst(),
							path -> path.getValue(),
							(path, value) -> path.setValue((String) value), "",
							StringPropertyConnector.get())));
		}
		
		{
			Map<Integer, String> map = new HashMap<Integer, String>();
			Set<IntegerProperty> set = items.stream().flatMap(s -> propContGetter.apply(s).getIntegerProperties().stream())
					.collect(Collectors.toSet());
			set.stream().sorted((p1, p2) -> p1.getPropertyField().getId().compareTo(p2.getPropertyField().getId()))
					.forEach(p -> map.put(p.getPropertyField().getId(), p.getPropertyField().getDescription()));
			map.entrySet().stream()
					.forEach(entry -> columns.add(new ColumnOptional<Special, IntegerProperty, Integer>(entry.getValue(),
							item -> item.getPropertyContainer().getIntegerProperties().stream()
									.filter(p -> p.getPropertyField().getId() == entry.getKey()).findFirst(),
							path -> String.valueOf(path.getValue()),
							(path, value) -> path.setValue(Integer.valueOf((String) value)), "",
							IntegerPropertyConnector.get())));
		}
		
		{
			Map<Integer, String> map = new HashMap<Integer, String>();
			Set<DoubleProperty> set = items.stream().flatMap(s -> propContGetter.apply(s).getDoubleProperties().stream())
					.collect(Collectors.toSet());
			set.stream().sorted((p1, p2) -> p1.getPropertyField().getId().compareTo(p2.getPropertyField().getId()))
					.forEach(p -> map.put(p.getPropertyField().getId(), p.getPropertyField().getDescription()));
			map.entrySet().stream()
					.forEach(entry -> columns.add(new ColumnOptional<Special, DoubleProperty, Integer>(entry.getValue(),
							item -> item.getPropertyContainer().getDoubleProperties().stream()
									.filter(p -> p.getPropertyField().getId() == entry.getKey()).findFirst(),
							path -> String.valueOf(path.getValue()),
							(path, value) -> path.setValue(Double.valueOf((String) value)), "",
							DoublePropertyConnector.get())));
		}
		
		
		
	}
	
//	private <T extends HttpDatabaseCrud<T, ID_TYPE>>, VALUE_TYPE, ID_TYPE> void createPropertyColumns(List<IColumn<Special, ?>> columns, Function<PropertyContainer,Collection<T>> propertyGetter,
//	HttpDatabaseCrud<T, ID_TYPE> connector, Function<String,VALUE_TYPE> stringConverter) {
//Map<Integer, String> map = new HashMap<Integer, String>();
//Set<T> set = getItems().stream().flatMap(s -> propertyGetter.apply(s.getPropertyContainer()).stream())
//		.collect(Collectors.toSet());
//set.stream().sorted((p1, p2) -> getPropertyField(p1).getId().compareTo(getPropertyField(p2).getId()))
//		.forEach(p -> map.put(getPropertyField(p).getId(), getPropertyField(p).getDescription()));
//map.entrySet().stream()
//		.forEach(entry -> columns.add(new ColumnOptional<Special, T, Integer>(entry.getValue(),
//				item -> propertyGetter.apply(item.getPropertyContainer()).stream()
//						.filter(p -> getPropertyField(p).getId() == entry.getKey()).findFirst(),
//				path -> String.valueOf(getValue(path)),
//				(path, value) -> setValue(path,stringConverter.apply((String)value)), "",
//				(HttpDatabaseCrud<T, Integer>) connector)));
//}
//
//private PropertyField getPropertyField(Object o) {
//if (o instanceof BooleanProperty)
//	return ((BooleanProperty)o).getPropertyField();
//
//}
//
//private <T> T getValue(Object o) {
//if (o instanceof BooleanProperty)
//	return (T) ((BooleanProperty)o).getValue();
//}
//
//private <T> void setValue(Object o, T value) {
//if (o instanceof BooleanProperty)
//	((BooleanProperty)o).setValue((Boolean)value);
//}

}
