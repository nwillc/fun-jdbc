package com.github.nwillc.funjdbc.utils;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Copier {
	public static <T> void copy(Consumer<T> setter, Function<Integer,T> getter, Integer index) {
		setter.accept(getter.apply(index));
	}

	public static void multi(Function<Integer,String> function, Integer ... keys){
		Stream.of(keys).forEach(k -> System.out.println(function.apply(k)));
	}

	public static <T> T access(Function<Integer, T> function, Integer key) {
		return function.apply(key);
	}

	public static <T> void println(Function<T,String> function, T value) {
		System.out.println(function.apply(value));
	}
}
