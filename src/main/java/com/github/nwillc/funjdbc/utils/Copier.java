package com.github.nwillc.funjdbc.utils;

import java.util.function.Consumer;
import java.util.function.Function;

public class Copier {
	public static <T> void copy(Consumer<T> setter, Function<Integer,T> getter, Integer index) {
		setter.accept(getter.apply(index));
	}
}
