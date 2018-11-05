package org.artorg.tools.phantomData.client.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class CollectionUtil {

	public static final <E, F> void syncLists(List<E> toList, List<F> fromList, BiPredicate<E, F> predicate,
			BiFunction<F, Integer, E> mapper) {
		List<E> removableColumns = new ArrayList<E>();
		List<E> addableColumns = new ArrayList<E>();
		for (int col = 0; col < toList.size(); col++) {
			final int localCol = col;
			if (!fromList.stream().filter(column -> predicate.test(toList.get(localCol), column)).findFirst()
					.isPresent())
				removableColumns.add(toList.get(col));
		}
		for (int col = 0; col < fromList.size(); col++) {
			final int localCol = col;
			if (!toList.stream().filter(column -> predicate.test(column, fromList.get(localCol))).findFirst()
					.isPresent())
				addableColumns.add(mapper.apply(fromList.get(col), col));
		}
		toList.removeAll(removableColumns);
		toList.addAll(addableColumns);
	}

	public static final <E> void syncLists(List<E> toList, List<E> fromList, BiPredicate<E, E> predicate) {
		List<E> removableColumns = new ArrayList<E>();
		List<E> addableColumns = new ArrayList<E>();
		for (int col = 0; col < toList.size(); col++) {
			final int localCol = col;
			if (!fromList.stream().filter(column -> predicate.test(toList.get(localCol), column)).findFirst()
					.isPresent())
				removableColumns.add(toList.get(col));
		}
		for (int col = 0; col < fromList.size(); col++) {
			final int localCol = col;
			if (!toList.stream().filter(column -> predicate.test(column, fromList.get(localCol))).findFirst()
					.isPresent())
				addableColumns.add(fromList.get(col));
		}
		toList.removeAll(removableColumns);
		toList.addAll(addableColumns);
	}

	public static final <E> void syncLists(List<E> fromList, List<E> toList) {
		addIfAbsent(fromList, toList);
		removeIfAbsent(fromList, toList);
	}

	public static final <E> void addIfAbsent(List<E> fromList, List<E> toList) {
		List<E> addableList = new ArrayList<E>();
		for (int col = 0; col < fromList.size(); col++)
			if (!toList.contains(fromList.get(col)))
				addableList.add(fromList.get(col));
		toList.addAll(addableList);
	}

	public static final <E> void removeIfAbsent(List<E> fromList, List<E> toList) {
		List<E> removableList = new ArrayList<E>();
		for (int col = 0; col < toList.size(); col++)
			if (!fromList.contains(toList.get(col)))
				removableList.add(toList.get(col));
		toList.removeAll(removableList);
	}

}
