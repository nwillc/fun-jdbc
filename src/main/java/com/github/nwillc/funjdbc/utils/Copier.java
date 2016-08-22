package com.github.nwillc.funjdbc.utils;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class Copier {

	public static <T,B> void copy2(List<T> strings, B bean, BiConsumer<B,T> setter, BiFunction<List<T>, Integer,T> getter, Integer index) {
		setter.accept(bean,getter.apply(strings,index));
	}
}
