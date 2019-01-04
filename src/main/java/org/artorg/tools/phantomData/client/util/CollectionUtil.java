package org.artorg.tools.phantomData.client.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class CollectionUtil {

	@SuppressWarnings("unchecked")
	public static <T> T[] createGenericArray(Class<T> itemClass, int length) {
		return (T[]) Array.newInstance(itemClass, length);
	}
	
	public static final List<Integer> inverseSelection(List<Integer> indexes, int size) {
		if (size == 0) return new ArrayList<>();
		indexes.sort((i1,i2) -> Integer.compare(i1,i2));
		List<Integer> inverseList = new ArrayList<>();
		int i=0;
		int j=0;
		
		while (j < size && i < indexes.size()) {
			if (indexes.get(i) > j) {
				inverseList.add(j);
				j++;
			} else if  (indexes.get(i) == j) {
				i++;
				j++;
			}
		}
		while (j < size) {
			inverseList.add(j);
			j++;
		}
		return inverseList;
	}
	
	public static final <E> List<E> subList(List<E> list, List<Integer> indexes) {
		List<E> subList = new ArrayList<>();
		for (int i = 0; i < indexes.size(); i++) {
			if (indexes.get(i) < list.size() && indexes.get(i) >= 0)
				subList.add(list.get(indexes.get(i)));
			else
				throw new IllegalArgumentException(
						"index i = " + indexes.get(i) + ", list size = " + list.size());
		}
		return subList;
	}
	
	public static final <E,F> List<Integer> searchRightNotInLeft(List<E> fromList, List<F> toList,
			BiPredicate<E, F> predicate) {
		return searchLeftNotInRight(toList,fromList, (f,e) -> predicate.test(e, f));
	}
	
	public static final <E,F> List<Integer> searchRightInLeft(List<E> fromList, List<F> toList,
			BiPredicate<E, F> predicate) {
		return searchLeftInRight(toList,fromList, (f,e) -> predicate.test(e, f));
	}

	
	public static final <E, F> List<Integer> searchLeftNotInRight(List<E> fromList, List<F> toList,
			BiPredicate<E, F> predicate) {
		List<Integer> matchingIndexes = searchLeftInRight(fromList, toList, predicate);
		return inverseSelection(matchingIndexes, fromList.size());
	}
	
	public static final <E, F> List<Integer> searchLeftInRight(List<E> fromList, List<F> toList,
			BiPredicate<E, F> predicate) {
		List<Integer> matchedLeftIndexes = new ArrayList<>(fromList.size()/2);
		for (int col = 0; col < fromList.size(); col++) {
			final int localCol = col;
			if (toList.stream().filter(column -> predicate.test(fromList.get(localCol), column))
					.findFirst().isPresent())
				matchedLeftIndexes.add(col);
		}
		return matchedLeftIndexes;
	}

	public static final <E> void addIfAbsent(List<E> fromList, List<E> toList,
			BiPredicate<E, E> predicate) {
		List<E> addableList = new ArrayList<>();
		for (int col = 0; col < fromList.size(); col++) {
			final int localCol = col;
			if (!toList.stream().filter(column -> predicate.test(fromList.get(localCol), column))
					.findFirst().isPresent())
				addableList.add(fromList.get(col));
		}
		toList.addAll(addableList);
	}

	public static final <E, F> void removeIfAbsent(List<E> fromList, List<F> toList,
			BiPredicate<E, F> predicate) {
		List<F> removableList = new ArrayList<F>();
		for (int col = 0; col < toList.size(); col++) {
			final int localCol = col;
			if (!fromList.stream().filter(column -> predicate.test(column, toList.get(localCol)))
					.findFirst().isPresent())
				removableList.add(toList.get(col));
		}
		toList.removeAll(removableList);
	}

	public static final <E extends F, F> void addIfAbsent(List<E> fromList, List<F> toList) {
		List<F> addableList = new ArrayList<F>();
		for (int col = 0; col < fromList.size(); col++)
			if (!toList.contains(fromList.get(col))) addableList.add(fromList.get(col));
		toList.addAll(addableList);
	}

	public static final <E> void removeIfAbsent(List<E> fromList, List<E> toList) {
		List<E> removableList = new ArrayList<E>();
		for (int col = 0; col < toList.size(); col++)
			if (!fromList.contains(toList.get(col))) removableList.add(toList.get(col));
		toList.removeAll(removableList);
	}

}
