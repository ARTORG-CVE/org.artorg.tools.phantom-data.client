package org.artorg.tools.phantomData.client.util;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamUtils {
	
	
	public static Predicate<String> getRegexTextFilterPredicate(List<String> regexes, boolean include, boolean findFirst) {
		List<Pattern> includePatterns = regexes.stream().map(regex -> Pattern.compile(regex))
				.collect(Collectors.toList());
		return s -> {
			Stream<Pattern> stream = includePatterns.stream().filter(pattern -> {
				if (include)
					return pattern.matcher(s).matches();
				return !pattern.matcher(s).matches();
			});
			if (findFirst)
				return stream.findFirst().isPresent();
			return stream.count() == includePatterns.size();
		};

	}

}