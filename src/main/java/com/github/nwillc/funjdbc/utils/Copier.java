package com.github.nwillc.funjdbc.utils;

import java.sql.ResultSet;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class Copier {

	public static <T, B> void copy2(B bean, BiConsumer<B, T> setter, ResultSet rs, BiFunction<ResultSet, Integer, T> getter, Integer index) {
		setter.accept(bean, getter.apply(rs, index));
	}
}
