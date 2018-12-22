package org.artorg.tools.phantomData.client.util;

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

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.column.OptionalColumnCreator;
import org.artorg.tools.phantomData.server.model.AbstractPersonifiedEntity;
import org.artorg.tools.phantomData.server.model.AbstractPropertifiedEntity;
import org.artorg.tools.phantomData.server.model.AbstractProperty;

import javafx.collections.ObservableList;

public class ColumnUtils {

	public static <T, R> void createCountingColumn(Class<T> itemClass, String name,
			List<AbstractColumn<T, ?>> columns, Function<T, ? extends Collection<R>> listGetter) {
		ColumnCreator<T, T> creator = new ColumnCreator<>(itemClass);
		columns.add(creator.createFilterColumn(name,
				path -> String.valueOf(listGetter.apply(path).size())));
	}

	private static SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");

	public static <T extends AbstractPersonifiedEntity<T>> void
			createPersonifiedColumns(Class<T> itemClass, List<AbstractColumn<T, ?>> columns) {
		ColumnCreator<T, T> creator = new ColumnCreator<>(itemClass);
		columns.add(creator.createFilterColumn("Last modified",
				path -> format.format(path.getDateLastModified())));
		columns.add(creator.createFilterColumn("Changed By",
				path -> path.getChanger().getSimpleAcademicName()));
		columns.add(
				creator.createFilterColumn("Added", path -> format.format(path.getDateAdded())));
		columns.add(creator.createFilterColumn("Created By",
				path -> path.getCreator().getSimpleAcademicName()));
	}

	public static <T extends AbstractPropertifiedEntity<T>> void createPropertyColumns(
			Class<T> itemClass, List<AbstractColumn<T, ?>> columns, ObservableList<T> items) {
		createPropertyColumns(itemClass, columns, items,
				container -> container.getBooleanProperties(), bool -> String.valueOf(bool),
				s -> Boolean.valueOf(s));
		createPropertyColumns(itemClass, columns, items,
				container -> container.getDoubleProperties(), bool -> String.valueOf(bool),
				s -> Double.valueOf(s));
		createPropertyColumns(itemClass, columns, items,
				container -> container.getIntegerProperties(), bool -> String.valueOf(bool),
				s -> Integer.valueOf(s));
		createPropertyColumns(itemClass, columns, items,
				container -> container.getStringProperties(), s -> s, s -> s);

		DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
		Function<String, Date> stringDateFunc = s -> {
			try {
				format.parse(s);
			} catch (ParseException e) {
				e.printStackTrace();
			} ;
			throw new IllegalArgumentException();
		};
		createPropertyColumns(itemClass, columns, items, container -> container.getDateProperties(),
				date -> String.valueOf(date), stringDateFunc);
	}

	private static <T, P extends AbstractProperty<P, R>, R> void createPropertyColumns(
			Class<T> itemClass, List<AbstractColumn<T, ?>> columns, ObservableList<T> items,
			Function<T, Collection<P>> propsGetter, Function<R, String> toStringFun,
			Function<String, R> fromStringFun) {
		Map<UUID, String> map = new HashMap<UUID, String>();
		Set<P> set = items.stream().flatMap(s -> propsGetter.apply(s).stream())
				.collect(Collectors.toSet());
		set.stream().sorted(
				(p1, p2) -> p1.getPropertyField().getId().compareTo(p2.getPropertyField().getId()))
				.forEach(
						p -> map.put(p.getPropertyField().getId(), p.getPropertyField().getName()));

		map.entrySet().stream().forEach(entry -> {
			OptionalColumnCreator<T,
					P> creator = new OptionalColumnCreator<>(itemClass,
							item -> propsGetter.apply(item).stream()
									.filter(p -> p.getPropertyField().getId() == entry.getKey())
									.findFirst());
			columns.add(creator.createFilterColumn(entry.getValue(),
					path -> toStringFun.apply(path.getValue()),
					(path, value) -> path.setValue(fromStringFun.apply((String) value)), ""));
		});

	}

}
