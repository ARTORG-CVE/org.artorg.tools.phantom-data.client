package org.artorg.tools.phantomData.client.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamUtils {

	public static <T, R> Collector<T, ?, Stream<R>>
		castFilter(Function<T, R> mapper) {
		return new Collector<T, Collection<R>, Stream<R>>() {

			@Override
			public Supplier<Collection<R>> supplier() {
				return () -> new ArrayList<>();
			}

			@Override
			public BiConsumer<Collection<R>, T> accumulator() {
				return (list, t) -> {
					try {
						list.add(mapper.apply(t));
					} catch (Exception e) {}
				};
			}

			@Override
			public BinaryOperator<Collection<R>> combiner() {
				return (list1, list2) -> {
					list1.addAll(list2);
					return list1;
				};
			}

			@Override
			public Function<Collection<R>, Stream<R>> finisher() {
				return list -> list.stream();
			}

			@Override
			public Set<Characteristics> characteristics() {
				return Collections.emptySet();
			}

		};
	}

	public static Predicate<String> getRegexTextFilterPredicate(List<String> regexes,
		boolean include, boolean findFirst) {
		List<Pattern> includePatterns = regexes.stream()
			.map(regex -> Pattern.compile(regex)).collect(Collectors.toList());
		return s -> {
			Stream<Pattern> stream = includePatterns.stream().filter(pattern -> {
				if (include) return pattern.matcher(s).matches();
				return !pattern.matcher(s).matches();
			});
			if (findFirst) return stream.findFirst().isPresent();
			return stream.count() == includePatterns.size();
		};

	}

//	@SuppressWarnings("unchecked")
//	public static <T> boolean forEach(Predicate<T> predicate, T... t) {
//		boolean succesful = true;
//		for (int i = 0; i < t.length; i++) {
//			if (predicate.test(t[i]) == false) {
//				succesful = false;
//			}
//		}
//		return succesful;
//	}
//
//	public static <T> boolean forEach(Predicate<T> predicate, Collection<T> coll) {
//		return forEach(predicate, coll.stream());
//	}
//
//	public static <T> boolean forEach(Predicate<T> predicate, Stream<T> stream) {
//		return stream.map(e -> predicate.test(e)).filter(b -> b == false).findFirst()
//			.orElse(true);
//	}

}
